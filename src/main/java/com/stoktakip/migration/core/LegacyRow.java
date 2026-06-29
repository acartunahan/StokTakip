package com.stoktakip.migration.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class LegacyRow {

    private final LegacyTableSpec tabloTanimi;
    private final Map<String, Object> values;
    private final String syntheticGuid;

    public LegacyRow(LegacyTableSpec tabloTanimi, Map<String, Object> values) {
        this(tabloTanimi, values, null);
    }

    private LegacyRow(LegacyTableSpec tabloTanimi, Map<String, Object> values, String syntheticGuid) {
        this.tabloTanimi = tabloTanimi;
        this.values = normalize(values);
        this.syntheticGuid = syntheticGuid;
    }

    public LegacyRow withSyntheticGuid(String syntheticGuid) {
        return new LegacyRow(tabloTanimi, values, syntheticGuid);
    }

    public LegacyTableSpec tabloTanimi() {
        return tabloTanimi;
    }

    public String tabloAdi() {
        return tabloTanimi.tabloAdi();
    }

    public Map<String, Object> values() {
        return values;
    }

    public Long id() {
        return tabloTanimi.idKolonu() == null ? null : longValue(tabloTanimi.idKolonu());
    }

    public String guid() {
        if (syntheticGuid != null && !syntheticGuid.isBlank()) {
            return syntheticGuid;
        }
        return tabloTanimi.guidKolonu() == null ? null : string(tabloTanimi.guidKolonu());
    }

    public String anahtar() {
        String guid = guid();
        if (guid != null && !guid.isBlank()) {
            return "GUID:" + guid;
        }
        Long id = id();
        if (id != null) {
            return "ID:" + id;
        }
        return "ROW:" + Integer.toHexString(values.hashCode());
    }

    public Object get(String column) {
        return values.get(key(column));
    }

    public String string(String column) {
        Object value = get(column);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    public Long longValue(String column) {
        Object value = get(column);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        return new BigDecimal(text).longValue();
    }

    public LocalDateTime dateTime(String column) {
        Object value = get(column);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate().atStartOfDay();
        }
        if (value instanceof java.util.Date date) {
            return new Timestamp(date.getTime()).toLocalDateTime();
        }
        return null;
    }

    private static Map<String, Object> normalize(Map<String, Object> input) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        input.forEach((key, value) -> normalized.put(key(key), value));
        return normalized;
    }

    private static String key(String column) {
        return column == null ? null : column.toUpperCase(Locale.ROOT);
    }
}
