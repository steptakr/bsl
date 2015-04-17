package nl.debijenkorf.bsl.controllers;

import com.wordnik.swagger.annotations.*;

import nl.debijenkorf.bsl.exceptions.ProductNotFoundException;
import nl.debijenkorf.bsl.models.ProductModel;
import nl.debijenkorf.bsl.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
@Api(value = "/product", description = "Operations about products")
public class ProductController {

    private ProductService productService;
    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get specific product info.")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Product not found")})
    public ProductModel Show(
            @ApiParam(name = "sku", required = true, value = "Unique sku of the product that needs to be retrieved (example: 079019008800000)")
            @RequestParam(value = "sku", required = true) String sku,
            @ApiParam(name = "api-version", required = false, value = "Api version code")
            @RequestParam(value = "api-version", required = false, defaultValue = "1.0.0") String apiVersion) throws Exception {

        ProductModel product = productService.getProduct(sku);
        if (product == null) {
            throw new ProductNotFoundException(sku);
        }

        return product;
    }
}
