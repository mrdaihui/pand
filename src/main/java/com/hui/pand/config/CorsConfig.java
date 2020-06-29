package com.hui.pand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors-filter.is-open}")
    private boolean isOpenCorsFilter;

    @Value("${cors-filter.origins}")
    private String corsOriginsStr;

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if(isOpenCorsFilter) {
            List<String> corsOrigins = Arrays.asList(corsOriginsStr.split(","));
            for (String corsOrigin : corsOrigins) {
                corsConfiguration.addAllowedOrigin(corsOrigin);
            }
        } else {
            corsConfiguration.addAllowedOrigin("*");
        }
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }
}