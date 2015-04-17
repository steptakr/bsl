package nl.debijenkorf.bsl;

import com.mangofactory.swagger.plugin.EnableSwagger;
import nl.debijenkorf.bsl.services.BSLStartService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

@EnableSwagger
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan
@Configuration
@ImportResource("classpath:spring-config.xml")
public class Application {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);


        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

        BSLStartService startService = context.getBean(BSLStartService.class);
        System.out.println(startService.getMessage());
    }
}
