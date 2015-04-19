package nl.debijenkorf.bsl.services.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.*;
import nl.debijenkorf.bsl.controllers.ImageController;
import nl.debijenkorf.bsl.services.ImageStorageService;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * Implementation of ImageStorageService based on Amazon S3 service
 *
 * Created by Daniel on 18/04/15.
 */
@Service
public class AmazonS3ServiceImpl implements ImageStorageService {

    private static final Pattern s3FileNamePattern = Pattern.compile("(.{1,4})?(.{0,4})?.*\\.[^\\.]+");

    @Value("${imageService.s3Bucket}")
    private String s3Bucket;

    @Value("${imageService.AWSAccessKey}")
    private String AWSAccessKey;

    @Value("${imageService.AWSSecretKey}")
    private String AWSSecretKey;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AmazonS3ServiceImpl.class);


    @Override
    public InputStream getImage(String folder, String filename) {
        AmazonS3 client = getS3Client();
        String path = getPath(folder, filename);
        S3Object object = client.getObject(new GetObjectRequest(
                s3Bucket, path));
        return object.getObjectContent();
    }

    @Async
    @Override
    public void saveImage(String folder, String filename, InputStream image, Long contentLength) {
        AmazonS3 client = getS3Client();
        String path = getPath(folder, filename);
        ObjectMetadata meta = new ObjectMetadata();
        if (contentLength != null) {
            meta.setContentLength(contentLength);
        }

        boolean firstAttemptOk = false;
        try {
            client.putObject(new PutObjectRequest(s3Bucket, path, image, meta));
            firstAttemptOk = true;
        } catch (RuntimeException e) {
            logger.warn("Failed 1/2 attempt saving image to Amazon S3. Folder: " + folder + ", filename: " + filename, e);
        }

        // retry if last attempt was not successful
        if (!firstAttemptOk) {
            try {
                Thread.sleep(200);
                client.putObject(new PutObjectRequest(s3Bucket, path, image, meta));
            } catch (InterruptedException e) {
                logger.error("Thread interrupted when waiting for second attempt", e);
            } catch (RuntimeException e) {
                logger.error("Failed 2/2 attempt saving image to Amazon S3. Folder: " + folder + ", filename: " + filename, e);
            }
        }
    }

    @Override
    public void flushObject(String folder, String filename) {
        try {
            AmazonS3 client = getS3Client();
            String path = getPath(folder, filename);
            client.deleteObject(new DeleteObjectRequest(s3Bucket, path));
        } catch (RuntimeException e) {
            logger.error("Failed flushing image in '" + folder + "' folder with filename '" + filename, e);
        }
    }

    private AmazonS3 getS3Client() {
        AWSCredentials cred = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return AWSAccessKey;
            }

            @Override
            public String getAWSSecretKey() {
                return AWSSecretKey;
            }
        };
        return new AmazonS3Client(cred);
    }

    /**
     * Path will contains
     * @param folder which folder this image should be stored
     * @param filename unique filename (per folder)
     * @return full path to the file on an Amazon S3 bucket
     */
    protected String getPath(String folder, String filename) {
        // replace each forward slash with underscore
        String s3FileName = filename.replaceAll("/", "_");

        StringBuilder path = new StringBuilder(256);
        path.append(folder);

        // extract s3 folder naming strategy from filename
        String folderLvl1;
        String folderLvl2;
        Matcher matcher = s3FileNamePattern.matcher(s3FileName);
        if (matcher.find()) {
            folderLvl1 = matcher.group(1);
            folderLvl2 = matcher.group(2);
        } else {
            logger.error("Invalid filename: " + filename);
            throw new RuntimeException("Invalid filename");
        }
        path.append("/");
        if (folderLvl1 != null && folderLvl1.length() == 4) {
            path.append(folderLvl1).append("/");
        }
        if (folderLvl2 != null && folderLvl2.length() == 4) {
            path.append(folderLvl2).append("/");
        }

        path.append(s3FileName);
        return path.toString();
    }
}
