package com.stoktakip.dto;

import com.stoktakip.enums.ZimmetVarlikTuru;

public record DepoTransferTalebi(
    ZimmetVarlikTuru varlikTuru,
    String varlikGuid,
    String gidecegiDepoGuid,
    String aciklama,
    String islemYapanPersonel
) {
}
