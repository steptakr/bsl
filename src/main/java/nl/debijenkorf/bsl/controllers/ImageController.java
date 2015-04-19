package nl.debijenkorf.bsl.controllers;

import com.amazonaws.util.IOUtils;
import nl.debijenkorf.bsl.services.ImageResizerService;
import static nl.debijenkorf.bsl.services.ImageResizerService.*;
import nl.debijenkorf.bsl.services.ImageStorageService;
import nl.debijenkorf.bsl.services.impl.AmazonS3ServiceImpl;
import nl.debijenkorf.bsl.services.impl.ImgScalrImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by deBijenkorf on 06/03/15.
 * Modified by
 */

@Controller
@RequestMapping("/image")
public class ImageController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageStorageService storage;

    @Value("${imageService.sourceRootURL}")
    private String sourceRootURL;

    @ResponseBody
    @RequestMapping(value = "/show/{definition}/*", method = RequestMethod.GET)
    public void show (
            @RequestParam(value = "reference", required = true) String filename,
            @PathVariable String definition,
            HttpServletResponse response) throws Exception {

        response.setContentType("image/jpeg");

        // validate image definition
        ImageProfile imgProfile = getImageProfile(definition);

        // serve optimized image if it exists
        try {
            InputStream is = storage.getImage(definition, filename);
            IOUtils.copy(is, response.getOutputStream());
            return;
        } catch (RuntimeException e) {
            logger.info("Optimized image does not exist in storage or storage service is unavailable. Definition: " + definition + ", filename: " + filename, e);
        }

        // unfortunately optimized image does not exist, get original image, compress and serve it
        String url = null;
        try {
            url = sourceRootURL + filename;
            URL urlClient = new URL(url);
            ImageResizerService imageProcessor = new ImgScalrImpl();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageProcessor.process(imgProfile, urlClient.openStream(), baos);

            byte[] imageBytes = baos.toByteArray();
            response.getOutputStream().write(imageBytes);

            // store (asynchronously)
            storage.saveImage(imgProfile.toString(), filename, new ByteArrayInputStream(imageBytes), (long) imageBytes.length);

        } catch (IOException e) {
            logger.info("Original image does not exist at " + url, e);
            throw new ImageNotFoundException();
        } catch (Exception e) {
            logger.info("Unknown exception when getting original image at " + url, e);
            throw new ImageNotFoundException();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/flush/{definition}/*", method = RequestMethod.GET)
    public void flush(
            @RequestParam(value = "reference", required = true) String filename,
            @PathVariable String definition) {
        storage.flushObject(getImageProfile(definition).toString(), filename);
    }

    private ImageProfile getImageProfile(String definition) {
        ImageProfile imgProfile;
        try {
            imgProfile = ImageProfile.valueOf(definition);
        } catch (IllegalArgumentException e) {
            logger.info("Image definition '" + definition + "' does not exist.", e);
            throw new InvalidImageDefinitionException();
        }
        return imgProfile;
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Image not found")
    private static class ImageNotFoundException extends RuntimeException {}

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Image not found")
    private static class InvalidImageDefinitionException extends RuntimeException {}
}
