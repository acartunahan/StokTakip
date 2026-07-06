package com.stoktakip.migration.core;

public enum LegacyTableSpec {
    RESAT_DEPO("RESAT_DEPO", "ID", "GUID", "ID"),
    RESAT_MARKA("RESAT_MARKA", null, "GUID", "GUID"),
    SARF_MARKA("SARF_MARKA", null, "ID", "ID"),
    RESAT_MODEL("RESAT_MODEL", null, "GUID", "GUID"),
    SARF_MODEL("SARF_MODEL", null, "GID", "GID"),
    RESAT_FIRMA("RESAT_FIRMA", null, "GUID", "GUID"),
    SARF_FIRMA("SARF_FIRMA", null, "GUID", "GUID"),
    RESAT_IHALE("RESAT_IHALE", null, "GUID", "GUID"),
    RESAT_SABIT_KOD("RESAT_SABIT_KOD", null, "GUID", "GUID"),
    R_TUKETIMCINSLERI("R_TUKETIMCINSLERI", null, "GUID", "GUID"),
    SARF_KATEGORI("SARF_KATEGORI", null, "GUID", "GUID"),
    R_DEMIRBAS("R_DEMIRBAS", null, "GUID", "GUID"),
    R_SARFDEMIRBAS("R_SARFDEMIRBAS", null, "GUID", "GUID"),
    R_APARAT("R_APARAT", null, "GUID", "GUID"),
    R_BARKOD("R_BARKOD", "ID", null, "ID"),
    R_DEMIRBAS_ACIKLAMA("R_DEMIRBAS_ACIKLAMA", "ID", null, "ID"),
    R_HAREKET("R_HAREKET", "ID", null, "ID"),
    R_SAYIM("R_SAYIM", null, "ID", "ID"),
    R_SAYIM_GENEL("R_SAYIM_GENEL", null, null, "SAYIM"),
    R_SAYIM_YEREL("R_SAYIM_YEREL", null, null, "SAYIM"),
    R_IADE_HAVUZU("R_IADE_HAVUZU", "ID", null, "ID"),
    R_IADE_ONAY_HAVUZU("R_IADE_ONAY_HAVUZU", null, null, "MALZEME"),
    SARF_LOG("SARF_LOG", "ID", null, "ID"),
    R_SQL_KAYIT("R_SQL_KAYIT", "ID", "GUID", "ID");

    private final String tabloAdi;
    private final String idKolonu;
    private final String guidKolonu;
    private final String siralamaKolonu;

    LegacyTableSpec(String tabloAdi, String idKolonu, String guidKolonu, String siralamaKolonu) {
        this.tabloAdi = tabloAdi;
        this.idKolonu = idKolonu;
        this.guidKolonu = guidKolonu;
        this.siralamaKolonu = siralamaKolonu;
    }

    public String tabloAdi() {
        return tabloAdi;
    }

    public String idKolonu() {
        return idKolonu;
    }

    public String guidKolonu() {
        return guidKolonu;
    }

    public String siralamaKolonu() {
        return siralamaKolonu;
    }
}
