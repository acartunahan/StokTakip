package com.stoktakip.migration.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.stoktakip.enums.SayimKapsami;
import com.stoktakip.enums.KayitDurumu;
import com.stoktakip.enums.EvetHayirDegeri;
import org.junit.jupiter.api.Test;

class LegacyFlagMapperTest {

    @Test
    void ortakEskiBayraklariEsler() {
        assertThat(LegacyFlagMapper.kayitDurumu("1")).isEqualTo(KayitDurumu.AKTIF);
        assertThat(LegacyFlagMapper.kayitDurumu("0")).isEqualTo(KayitDurumu.PASIF);
        assertThat(LegacyFlagMapper.evetHayir("E")).isEqualTo(EvetHayirDegeri.EVET);
        assertThat(LegacyFlagMapper.sayimKapsami("Y")).isEqualTo(SayimKapsami.YEREL);
    }
}
