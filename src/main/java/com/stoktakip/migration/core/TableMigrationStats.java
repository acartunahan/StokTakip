package com.stoktakip.migration.core;

public class TableMigrationStats {

    private long kaynakSatirSayisi;
    private long hedefSatirSayisi;
    private long aktarilanSatirSayisi;
    private long atlananSatirSayisi;
    private long tekrarSatirSayisi;
    private long hataliSatirSayisi;
    private long eksikIliskiSayisi;
    private long nullGuidSayisi;
    private long bozukGuidSayisi;
    private long veriYukuSatirSayisi;

    public void kaynakSatirSayisi(long say) {
        this.kaynakSatirSayisi = say;
    }

    public void hedefSatirSayisi(long say) {
        this.hedefSatirSayisi = say;
    }

    public void aktarildi() {
        aktarilanSatirSayisi++;
    }

    public void atlandi() {
        atlananSatirSayisi++;
    }

    public void tekrar() {
        tekrarSatirSayisi++;
    }

    public void hata() {
        hataliSatirSayisi++;
    }

    public void eksikIliski() {
        eksikIliskiSayisi++;
    }

    public void nullGuid() {
        nullGuidSayisi++;
    }

    public void bozukGuid() {
        bozukGuidSayisi++;
    }

    public void veriYuku() {
        veriYukuSatirSayisi++;
    }

    public String toJson(String tabloAdi) {
        return "{\"table\":\"" + tabloAdi + "\","
            + "\"kaynakSatirSayisi\":" + kaynakSatirSayisi + ','
            + "\"hedefSatirSayisi\":" + hedefSatirSayisi + ','
            + "\"aktarilanSatirSayisi\":" + aktarilanSatirSayisi + ','
            + "\"atlananSatirSayisi\":" + atlananSatirSayisi + ','
            + "\"tekrarSatirSayisi\":" + tekrarSatirSayisi + ','
            + "\"hataliSatirSayisi\":" + hataliSatirSayisi + ','
            + "\"eksikIliskiSayisi\":" + eksikIliskiSayisi + ','
            + "\"nullGuidSayisi\":" + nullGuidSayisi + ','
            + "\"bozukGuidSayisi\":" + bozukGuidSayisi + ','
            + "\"veriYukuSatirSayisi\":" + veriYukuSatirSayisi
            + '}';
    }
}
