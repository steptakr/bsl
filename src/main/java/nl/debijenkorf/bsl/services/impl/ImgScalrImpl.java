package nl.debijenkorf.bsl.services.impl;

import com.amazonaws.util.IOUtils;
import nl.debijenkorf.bsl.services.ImageResizerService;
import org.imgscalr.Scalr;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Image resizer service using ImgScalr library
 *
 * Created by Daniel on 18/04/15.
 */
public class ImgScalrImpl implements ImageResizerService {

    @Override
    public void process (ImageProfile profile, InputStream image, OutputStream os) throws IOException {
        if (profile == ImageProfile.original) {
            IOUtils.copy(image, os);
            return;
        }

        // rescale
        BufferedImage bufferedImage = ImageIO.read(image);
        BufferedImage scaledImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, profile.width, profile.height);

        // recompress
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(ImageIO.createImageOutputStream(os));
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(profile.quality/100F);
        try {
            writer.write(null, new IIOImage(scaledImage, null, null), param);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
