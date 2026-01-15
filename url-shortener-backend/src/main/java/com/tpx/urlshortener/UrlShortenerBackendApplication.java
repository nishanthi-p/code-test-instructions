package com.tpx.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class UrlShortenerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerBackendApplication.class, args);
    }

}
