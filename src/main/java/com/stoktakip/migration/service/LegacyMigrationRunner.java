package com.stoktakip.migration.service;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyMigrationRunner.class);

    private final LegacyMigrationService migrationService;
    private final DataSource targetDataSource;
    private final DataSource legacyDataSource;

    public LegacyMigrationRunner(
        LegacyMigrationService migrationService,
        @Qualifier("dataSource") DataSource targetDataSource,
        @Qualifier("legacyDataSource") DataSource legacyDataSource
    ) {
        this.migrationService = migrationService;
        this.targetDataSource = targetDataSource;
        this.legacyDataSource = legacyDataSource;
    }

    @Override
    public void run(String... args) {
        baglantilariDogrula();
        String ozet = migrationService.calistir();
        log.info("Eski veri gocu tamamlandi. Ozet: {}", ozet);
    }

    private void baglantilariDogrula() {
        try (
            Connection hedef = targetDataSource.getConnection();
            Connection eski = legacyDataSource.getConnection()
        ) {
            DataSourceInfo hedefBilgi = DataSourceInfo.from(hedef);
            DataSourceInfo eskiBilgi = DataSourceInfo.from(eski);
            log.info("Migration hedef datasource: {}", hedefBilgi);
            log.info("Migration legacy datasource: {}", eskiBilgi);
            if (hedefBilgi.sameUserAs(eskiBilgi)) {
                throw new IllegalStateException(
                    "Migration hedef datasource legacy datasource ile ayni kullaniciya baglaniyor. "
                        + "spring.datasource.* hedef stoktakip semasini, "
                        + "app.migration.legacy-datasource.* legacy_readonly semasini gostermeli."
                );
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Migration datasource bilgileri okunamadi.", ex);
        }
    }

    private record DataSourceInfo(String url, String user, String schema) {

        static DataSourceInfo from(Connection connection) throws SQLException {
            return new DataSourceInfo(
                connection.getMetaData().getURL(),
                connection.getMetaData().getUserName(),
                connection.getSchema()
            );
        }

        boolean sameUserAs(DataSourceInfo other) {
            return user != null && other.user != null && user.equalsIgnoreCase(other.user);
        }

        @Override
        public String toString() {
            return "url=" + url + ", user=" + user + ", schema=" + schema;
        }
    }
}
