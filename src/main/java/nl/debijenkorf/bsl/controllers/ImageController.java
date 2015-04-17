package nl.debijenkorf.bsl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by deBijenkorf on 06/03/15.
 */

@Controller
@RequestMapping("/image")
public class ImageController {

    public ImageController(){}

    @ResponseBody
    @RequestMapping(value = "/show/{definition}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] show (
            @RequestParam(value = "filename", required = true) String filename,
            @PathVariable String definition
    ) throws Exception {

        //get the hardcoded image
        BufferedImage image;
        URL url = new URL("http://cdn.debijenkorf.nl/INTERSHOP/static/WFS/dbk-shop-Site/-/dbk-shop/nl_NL/product-images/077/770/13_0777701000516430_pro_flt_frt_01_1108_1528_1022139.jpg");
        image = ImageIO.read(url);

        //do some cool stuff here using the "definition" and "filename" variables

        //prepare the output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();

        return baos.toByteArray();
    }

}
