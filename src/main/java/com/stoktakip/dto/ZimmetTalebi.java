package com.stoktakip.dto;

import com.stoktakip.enums.ZimmetVarlikTuru;

public record ZimmetTalebi(
    ZimmetVarlikTuru varlikTuru,
    String varlikGuid,
    String personelKodu,
    String aciklama,
    String islemYapanPersonel
) {
}
