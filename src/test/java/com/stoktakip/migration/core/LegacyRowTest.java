package com.stoktakip.migration.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LegacyRowTest {

    @Test
    void anahtarOnceGuidSonraIdSonraSatirHashKullanir() {
        LegacyRow idRow = new LegacyRow(LegacyTableSpec.RESAT_DEPO, Map.of("ID", BigDecimal.TEN, "GUID", "abc-guid"));
        LegacyRow guidRow = new LegacyRow(LegacyTableSpec.SARF_MARKA, Map.of("ID", "brand-guid"));
        LegacyRow syntheticRow = new LegacyRow(LegacyTableSpec.R_SAYIM_GENEL, Map.of("SAYIM", "sayim"))
            .withSyntheticGuid("sayim|demirbas");

        assertThat(idRow.anahtar()).isEqualTo("GUID:abc-guid");
        assertThat(guidRow.anahtar()).isEqualTo("GUID:brand-guid");
        assertThat(syntheticRow.anahtar()).isEqualTo("GUID:sayim|demirbas");
    }
}
