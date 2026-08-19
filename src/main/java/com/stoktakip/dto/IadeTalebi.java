package com.stoktakip.dto;

import com.stoktakip.enums.ZimmetVarlikTuru;

public record IadeTalebi(
    ZimmetVarlikTuru varlikTuru,
    String varlikGuid,
    String depoGuid,
    String aciklama,
    String islemYapanPersonel
) {
}
