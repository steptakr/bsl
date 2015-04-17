package nl.debijenkorf.bsl.lib.intershop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntershopConfig {

    @Value("${intershop.productEndpoint}")
    private String productEndpoint;

    public String getProductEndpoint() {
        return productEndpoint;
    }
}
