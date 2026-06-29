package com.stoktakip.migration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyMigrationRunner.class);

    private final LegacyMigrationService migrationService;

    public LegacyMigrationRunner(LegacyMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void run(String... args) {
        String ozet = migrationService.calistir();
        log.info("Eski veri gocu tamamlandi. Ozet: {}", ozet);
    }
}
