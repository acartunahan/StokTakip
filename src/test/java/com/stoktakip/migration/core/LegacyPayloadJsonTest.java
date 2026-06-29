package com.stoktakip.migration.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LegacyPayloadJsonTest {

    @Test
    void nesneEskiMetinDegerleriniKacirir() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("ADI", "Model \"A\"");
        values.put("ACIKLAMA", "satir\nsonu");
        values.put("ID", 10);

        String json = LegacyPayloadJson.object(values);

        assertThat(json).isEqualTo("{\"ADI\":\"Model \\\"A\\\"\",\"ACIKLAMA\":\"satir\\nsonu\",\"ID\":10}");
    }
}
