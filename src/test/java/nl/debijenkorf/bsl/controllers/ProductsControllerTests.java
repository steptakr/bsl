package nl.debijenkorf.bsl.controllers;

import nl.debijenkorf.bsl.Application;
import nl.debijenkorf.bsl.services.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ProductsControllerTests {

    private MockMvc mockMvc;

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void get_forNonExistingProductId_ShouldReturnHttpStatusCode404() throws Exception {

        when(productService.getProduct( any(String.class))).thenReturn(null);

        mockMvc.perform(get("/product/show?sku=1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(any(String.class));
    }

    @Test
    public void get_shouldCallProductServiceGetProductWithParams() throws Exception {
        mockMvc.perform(get("/product/show?sku=1"));
        verify(productService, times(1)).getProduct(anyString());
    }

    /*
    @Test
    public void get_forAExistingProductShouldReturnTheProductMessage() throws Exception {
        // product-sku: 079019008800000 (le-creuset-braadpan-ovaal)

        ProductModel product = new ProductModel();
        product.setSku("079019008800000");
        when(productService.getProduct(any(String.class))).thenReturn(product);

        mockMvc.perform(get("/product/show?code=079019008800000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.sku", is("079019008800000")));

        verify(productService, times(1)).getProduct(any(String.class));
    }
    */
}
