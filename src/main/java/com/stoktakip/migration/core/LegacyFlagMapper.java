package com.stoktakip.migration.core;

import com.stoktakip.enums.SayimIslemi;
import com.stoktakip.enums.SayimKapsami;
import com.stoktakip.enums.KayitDurumu;
import com.stoktakip.enums.EvetHayirDegeri;

public final class LegacyFlagMapper {

    private LegacyFlagMapper() {
    }

    public static String durum(String raw) {
        return kayitDurumu(raw).name();
    }

    public static KayitDurumu kayitDurumu(String raw) {
        if (raw == null || raw.isBlank()) {
            return KayitDurumu.BILINMIYOR;
        }
        String value = raw.trim().toUpperCase();
        return switch (value) {
            case "1", "A", "AKTIF" -> KayitDurumu.AKTIF;
            case "0", "P", "PASIF" -> KayitDurumu.PASIF;
            case "D", "S", "SIL", "SILINMIS" -> KayitDurumu.SILINMIS;
            default -> KayitDurumu.BILINMIYOR;
        };
    }

    public static EvetHayirDegeri evetHayir(String raw) {
        if (raw == null || raw.isBlank()) {
            return EvetHayirDegeri.BILINMIYOR;
        }
        String value = raw.trim().toUpperCase();
        return switch (value) {
            case "1", "E", "Y", "EVET", "TRUE" -> EvetHayirDegeri.EVET;
            case "0", "H", "N", "HAYIR", "FALSE" -> EvetHayirDegeri.HAYIR;
            default -> EvetHayirDegeri.BILINMIYOR;
        };
    }

    public static SayimKapsami sayimKapsami(String raw) {
        if (raw == null || raw.isBlank()) {
            return SayimKapsami.BILINMIYOR;
        }
        String value = raw.trim().toUpperCase();
        return switch (value) {
            case "G", "GENEL" -> SayimKapsami.GENEL;
            case "Y", "L", "YEREL" -> SayimKapsami.YEREL;
            default -> SayimKapsami.BILINMIYOR;
        };
    }

    public static SayimIslemi sayimIslemi(String raw) {
        if (raw == null || raw.isBlank()) {
            return SayimIslemi.BILINMIYOR;
        }
        String value = raw.trim().toUpperCase();
        return switch (value) {
            case "B", "F", "BULUNDU" -> SayimIslemi.BULUNDU;
            case "E", "M", "EKSIK" -> SayimIslemi.EKSIK;
            case "A", "X", "FAZLA" -> SayimIslemi.FAZLA;
            default -> SayimIslemi.BILINMIYOR;
        };
    }

}
