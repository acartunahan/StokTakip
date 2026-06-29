package com.stoktakip.migration.service;

import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.LegacyTableSpec;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacySourceReader {

    private final JdbcTemplate eskiJdbcTemplate;

    public LegacySourceReader(@Qualifier("eskiJdbcTemplate") JdbcTemplate eskiJdbcTemplate) {
        this.eskiJdbcTemplate = eskiJdbcTemplate;
    }

    public long say(LegacyTableSpec spec) {
        Number say = eskiJdbcTemplate.queryForObject("select count(*) from " + spec.tabloAdi(), Number.class);
        return say == null ? 0L : say.longValue();
    }

    public List<LegacyRow> parcaOku(LegacyTableSpec spec, int offset, int chunkSize) {
        String sql = "select * from " + spec.tabloAdi()
            + " order by " + spec.siralamaKolonu() + " nulls last"
            + " offset ? rows fetch next ? rows only";
        return eskiJdbcTemplate.queryForList(sql, offset, chunkSize)
            .stream()
            .map(row -> new LegacyRow(spec, row))
            .toList();
    }
}
