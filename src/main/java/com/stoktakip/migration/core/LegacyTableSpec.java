package com.stoktakip.migration.core;

public enum LegacyTableSpec {
    RESAT_DEPO("RESAT_DEPO", "ID", "GUID", "ID"),
    RESAT_MARKA("RESAT_MARKA", "ID", "GUID", "ID"),
    SARF_MARKA("SARF_MARKA", null, "GUID", "GUID"),
    RESAT_MODEL("RESAT_MODEL", "ID", "GUID", "ID"),
    SARF_MODEL("SARF_MODEL", null, "GUID", "GUID"),
    RESAT_FIRMA("RESAT_FIRMA", "ID", "GUID", "ID"),
    SARF_FIRMA("SARF_FIRMA", null, "GUID", "GUID"),
    RESAT_IHALE("RESAT_IHALE", "ID", "GUID", "ID"),
    RESAT_SABIT_KOD("RESAT_SABIT_KOD", "ID", "GUID", "ID"),
    R_TUKETIMCINSLERI("R_TUKETIMCINSLERI", "ID", "GUID", "ID"),
    SARF_KATEGORI("SARF_KATEGORI", null, "GUID", "GUID"),
    R_DEMIRBAS("R_DEMIRBAS", "ID", "GUID", "ID"),
    R_SARFDEMIRBAS("R_SARFDEMIRBAS", "ID", "GUID", "ID"),
    R_APARAT("R_APARAT", "ID", "GUID", "ID"),
    RESAT_DEMIRBAS_RESIM("RESAT_DEMIRBAS_RESIM", "ID", null, "ID"),
    R_BARKOD("R_BARKOD", "ID", null, "ID"),
    R_DEMIRBAS_ACIKLAMA("R_DEMIRBAS_ACIKLAMA", "ID", null, "ID"),
    R_HAREKET("R_HAREKET", "ID", null, "ID"),
    R_SAYIM("R_SAYIM", null, "GUID", "GUID"),
    R_SAYIM_GENEL("R_SAYIM_GENEL", null, null, "R_SAYIMGUID"),
    R_SAYIM_YEREL("R_SAYIM_YEREL", null, null, "R_SAYIMGUID"),
    R_IADE_HAVUZU("R_IADE_HAVUZU", "ID", null, "ID"),
    R_IADE_ONAY_HAVUZU("R_IADE_ONAY_HAVUZU", null, null, "R_DEMIRBASGUID"),
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
