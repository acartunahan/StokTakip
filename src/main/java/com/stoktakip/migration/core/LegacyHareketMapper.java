package com.stoktakip.migration.core;

public final class LegacyHareketMapper {

    private LegacyHareketMapper() {
    }

    public static LegacyHareketHedefi zimmetHareketHedefi(LegacyRow row) {
        if (kolonDoluMu(row, "NERDEN") || kolonDoluMu(row, "NEREYE")) {
            return LegacyHareketHedefi.DEPO_ZIMMET_HAREKET;
        }
        return LegacyHareketHedefi.PERSONEL_ZIMMET_HAREKET;
    }

    private static boolean kolonDoluMu(LegacyRow row, String column) {
        return row.string(column) != null;
    }
}
