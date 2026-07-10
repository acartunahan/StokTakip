package com.stoktakip.migration.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LegacyHareketMapperTest {

    @Test
    void nerdenVeNereyeBosIsePersonelZimmetHareketineYonlendirir() {
        LegacyRow nullRow = hareketSatiri(null, null);
        LegacyRow bosRow = hareketSatiri("", "   ");

        assertThat(LegacyHareketMapper.zimmetHareketHedefi(nullRow))
            .isEqualTo(LegacyHareketHedefi.PERSONEL_ZIMMET_HAREKET);
        assertThat(LegacyHareketMapper.zimmetHareketHedefi(bosRow))
            .isEqualTo(LegacyHareketHedefi.PERSONEL_ZIMMET_HAREKET);
    }

    @Test
    void nerdenVeyaNereyeDoluIseDepoZimmetHareketineYonlendirir() {
        assertThat(LegacyHareketMapper.zimmetHareketHedefi(hareketSatiri("DEPO-1", null)))
            .isEqualTo(LegacyHareketHedefi.DEPO_ZIMMET_HAREKET);
        assertThat(LegacyHareketMapper.zimmetHareketHedefi(hareketSatiri(null, "DEPO-2")))
            .isEqualTo(LegacyHareketHedefi.DEPO_ZIMMET_HAREKET);
        assertThat(LegacyHareketMapper.zimmetHareketHedefi(hareketSatiri("  DEPO-1  ", "   ")))
            .isEqualTo(LegacyHareketHedefi.DEPO_ZIMMET_HAREKET);
    }

    private static LegacyRow hareketSatiri(String nerden, String nereye) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("ID", 1L);
        values.put("NERDEN", nerden);
        values.put("NEREYE", nereye);
        return new LegacyRow(LegacyTableSpec.R_HAREKET, values);
    }
}
