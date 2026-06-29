package com.stoktakip;

import com.stoktakip.migration.config.MigrationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MigrationProperties.class)
public class StokTakipApplication {

    public static void main(String[] args) {
        SpringApplication.run(StokTakipApplication.class, args);
    }
}
