package com.stoktakip.migration.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyDataSourceConfig {

    @Bean(name = "legacyDataSource")
    public DataSource legacyDataSource(MigrationProperties ayarlar) {
        MigrationProperties.LegacyDataSource eski = ayarlar.getLegacyDataSource();
        return DataSourceBuilder.create()
            .url(eski.getUrl())
            .username(eski.getUsername())
            .password(eski.getPassword())
            .driverClassName(eski.getDriverClassName())
            .build();
    }

    @Bean(name = "eskiJdbcTemplate")
    public JdbcTemplate eskiJdbcTemplate(@Qualifier("legacyDataSource") DataSource legacyDataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(legacyDataSource);
        jdbcTemplate.setFetchSize(500);
        return jdbcTemplate;
    }
}
