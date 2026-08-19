package com.stoktakip.dto;

import com.stoktakip.enums.ZimmetHareketTipi;
import com.stoktakip.enums.ZimmetVarlikTuru;
import java.time.LocalDateTime;

public record ZimmetHareketYaniti(
    Long id,
    ZimmetVarlikTuru varlikTuru,
    String varlikGuid,
    String envanterTuru,
    ZimmetHareketTipi hareketTipi,
    String personelKodu,
    String mevcutDepoGuid,
    String gidecegiDepoGuid,
    String aciklama,
    LocalDateTime tarih,
    boolean aktif
) {
}
