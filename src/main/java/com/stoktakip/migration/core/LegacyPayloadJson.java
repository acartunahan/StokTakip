package com.stoktakip.migration.core;

import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public final class LegacyPayloadJson {

    private LegacyPayloadJson() {
    }

    public static String object(Map<String, ?> values) {
        StringBuilder json = new StringBuilder("{");
        Iterator<? extends Map.Entry<String, ?>> iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ?> entry = iterator.next();
            json.append('"').append(escape(entry.getKey())).append('"').append(':');
            appendValue(json, entry.getValue());
            if (iterator.hasNext()) {
                json.append(',');
            }
        }
        return json.append('}').toString();
    }

    public static String array(Collection<String> values) {
        StringBuilder json = new StringBuilder("[");
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            json.append('"').append(escape(iterator.next())).append('"');
            if (iterator.hasNext()) {
                json.append(',');
            }
        }
        return json.append(']').toString();
    }

    private static void appendValue(StringBuilder json, Object value) {
        if (value == null) {
            json.append("null");
        } else if (value instanceof Number || value instanceof Boolean) {
            json.append(value);
        } else if (value instanceof TemporalAccessor) {
            json.append('"').append(escape(String.valueOf(value))).append('"');
        } else {
            json.append('"').append(escape(String.valueOf(value))).append('"');
        }
    }

    private static String escape(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
    }
}
