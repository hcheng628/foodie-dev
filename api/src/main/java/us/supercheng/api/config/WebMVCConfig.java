package us.supercheng.api.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebMVCConfig {

    @Bean
    public RestTemplate restTemplateInit(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}