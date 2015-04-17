package nl.debijenkorf.bsl.services.impl;

import nl.debijenkorf.bsl.lib.intershop.IntershopConfig;
import nl.debijenkorf.bsl.services.ProductService;
import nl.debijenkorf.bsl.models.ProductModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    IntershopConfig intershopConfig;

    public ProductModel getProduct(String productSku) throws Exception {
        ProductModel product = null;

        String url = getProductSearchUrl(productSku);
        JSONObject json = readJSON(url);

        if (json != null) {
            JSONArray elements = json.has("elements") ? json.getJSONArray("elements") : null;
            if (elements == null || elements.length() == 0) {
                return null;
            }

            //get first item from response
            JSONObject jsonProduct = elements.getJSONObject(0);
            product = mapJSONtoProductObject(jsonProduct);

        }


        return product;
    }


    private String getProductSearchUrl(String productSku){
        return String.format("%s?SKU=%s", intershopConfig.getProductEndpoint(), productSku);
    }

    private JSONObject readJSON(String url) throws Exception{
        //read and parse the JSON from Intershop
        JSONObject json = null;
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }


            json = new JSONObject(sb.toString());
        }
        finally {
            is.close();
        }
        return json;
    }

    private ProductModel mapJSONtoProductObject(JSONObject jsonObject) {

        ProductModel product = null;

        if (jsonObject == null) {
            return null;
        }

        product = new ProductModel();

        product.setSku(getValueOrDefault(jsonObject, "defaultVariation"));
        product.setName(getValueOrDefault(jsonObject, "displayName"));
        product.setBrand(getValueOrDefault(jsonObject, "manufacturer"));
        //product.setSubbrand(new Brand(getValueOrDefault(productProperties, "sub_brand_code"),getValueOrDefault(productProperties, "sub_brand_name")));

        /*
        // images
        ArrayList<ProductImage> images = new ArrayList<ProductImage>();
        String imageId = getValueOrDefault(productProperties, "image_1_id");
        if(imageId != null && !imageId.equals("")){
            ProductImage image = new ProductImage();
            image.setId(getValueOrDefault(productProperties, "image_1_id"));
            images.add(image);
        }
        imageId = getValueOrDefault(productProperties, "image_2_id");
        if(imageId != null && !imageId.equals("")){
            ProductImage image = new ProductImage();
            image.setId(getValueOrDefault(productProperties, "image_2_id"));
            images.add(image);
        }
        imageId = getValueOrDefault(productProperties, "image_3_id");
        if(imageId != null && !imageId.equals("")){
            ProductImage image = new ProductImage();
            image.setId(getValueOrDefault(productProperties, "image_3_id"));
            images.add(image);
        }
        if(images.size()>0) {
            product.setImages(images);
        }

        JSONArray additives = (productProperties.has("additives") ? productProperties.getJSONArray("additives") : null);
        if (additives != null) {
            String[] strAdditives = new String[additives.length()];
            for (int i = 0; i < additives.length(); i++) {
                strAdditives[i] = additives.getString(i);
            }
            product.setAdditives(strAdditives);
        }
        */

        return product;
    }

    private String getValueOrDefault(JSONObject jsonObject, String key){
        if(jsonObject !=null && jsonObject.has(key)){
            String val = jsonObject.getString(key);
            return val;
        }

        return "";
    }
}