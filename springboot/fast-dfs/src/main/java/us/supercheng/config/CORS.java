package us.supercheng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CORS {

    @Bean
    public CorsFilter cors() {
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(
                Arrays.asList("http://localhost:8080",
                "http://127.0.0.1:8080",
                "http://0.0.0.0:8080",
                "http://192.168.31.46:8080",
                        "*"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        src.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(src);
    }
}
