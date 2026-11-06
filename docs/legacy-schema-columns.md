# Eski veritabanı: göçün okuduğu tablo ve kolonlar

Bu liste göç kodunun (`LegacyMigrationService`) okuduğu kolonlardır. Gerçek tablolarda **fazladan kolon olması sorun değil**; önemli olan buradaki her kolonun gerçek tabloda **aynı adla** bulunması.

Elle karşılaştırmak yerine `docs/sql/016_check_legacy_schema.sql` dosyasını eski veritabanında çalıştırabilirsin: eksik tablo ve kolonları listeler.

Toplam 23 tablo, 189 kolon. *(göç dışı)* işaretli tablolar şu an yorum satırında, uyuşmasalar da göçü etkilemez.

## RESAT_DEPO

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `GUID` | VARCHAR2(128) |
| `ADI` | VARCHAR2(100) |
| `ACIKLAMA` | VARCHAR2(500) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |
| `PERSONELKODU` | VARCHAR2(50) |

## RESAT_FIRMA

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `FIRMA_ADI` | VARCHAR2(255) |
| `YETKILI` | VARCHAR2(255) |
| `YETKILITEL` | VARCHAR2(50) |
| `IRTIBAT` | VARCHAR2(255) |
| `IRTIBATTEL` | VARCHAR2(50) |
| `TEL1` | VARCHAR2(50) |
| `TEL2` | VARCHAR2(50) |
| `FAX` | VARCHAR2(50) |
| `WEB` | VARCHAR2(255) |
| `ADRES` | VARCHAR2(500) |
| `NOTLAR` | VARCHAR2(500) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |

## RESAT_IHALE

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `IHALE_ADI` | VARCHAR2(100) |
| `IHALE_TAR` | DATE |
| `KABUL_TAR` | DATE |
| `BIRIM` | NUMBER |
| `PERSONEL` | VARCHAR2(100) |
| `FIRMA_ID` | VARCHAR2(128) |
| `SOZ_TAR` | DATE |
| `ACIKLAMA` | VARCHAR2(500) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |
| `KOM_BASKANI` | VARCHAR2(100) |
| `UYE1` | VARCHAR2(100) |
| `UYE2` | VARCHAR2(100) |
| `YERINDE_GARANTI` | NUMBER |
| `ONARIM_SURESI` | NUMBER |

## RESAT_MARKA

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(36) |
| `ADI` | VARCHAR2(50) |
| `ACIKLAMA` | VARCHAR2(200) |
| `DURUM` | CHAR(1) |
| `K_TARIH` | DATE |

## RESAT_MODEL

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(36) |
| `ADI` | VARCHAR2(50) |
| `SABITKOD` | VARCHAR2(128) |
| `ACIKLAMA` | VARCHAR2(200) |
| `DURUM` | CHAR(1) |
| `K_TARIH` | DATE |
| `PARENT` | VARCHAR2(36) |

## RESAT_SABIT_KOD

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `PARENT` | VARCHAR2(128) |
| `AD` | VARCHAR2(100) |
| `YORUM` | VARCHAR2(500) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |

## R_BARKOD

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `MGUID` | VARCHAR2(128) |
| `BARKOD` | VARCHAR2(100) |
| `SERIMI` | VARCHAR2(10) |

## R_DEMIRBAS

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `D_NO` | NUMBER |
| `SERI_NO` | VARCHAR2(100) |
| `BIRIM` | NUMBER |
| `DEPO_GUID` | VARCHAR2(128) |
| `ALIM_YOLU` | NUMBER |
| `M_ADI` | VARCHAR2(255) |
| `MARKA` | VARCHAR2(128) |
| `MODEL` | VARCHAR2(128) |
| `IHALE_ID` | VARCHAR2(128) |
| `G_BAS_TAR` | DATE |
| `G_BIT_TAR` | DATE |
| `ACIKLAMA` | VARCHAR2(1000) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |
| `SAYMANLIKNO` | VARCHAR2(100) |
| `PERSZIMMET` | VARCHAR2(100) |
| `ILSUBEDBASNO` | VARCHAR2(100) |
| `ALIM_TUR` | VARCHAR2(20) |

## R_DEMIRBAS_ACIKLAMA

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `MGUID` | VARCHAR2(128) |
| `ACIKLAMA` | VARCHAR2(1000) |
| `ZIMMETSAHIBI` | VARCHAR2(100) |
| `K_TARIH` | DATE |
| `EKLEYEN` | VARCHAR2(150) |
| `EKLEYENSICIL` | VARCHAR2(50) |

## R_HAREKET

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `MGUID` | VARCHAR2(128) |
| `ADI` | VARCHAR2(100) |
| `NERDEN` | VARCHAR2(128) |
| `NEREYE` | VARCHAR2(128) |
| `KULLANICI` | VARCHAR2(100) |
| `ACIKLAMA` | VARCHAR2(1000) |
| `K_TARIH` | DATE |

## R_IADE_HAVUZU *(göç dışı)*

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `MGUID` | VARCHAR2(128) |
| `DURUM` | NUMBER |
| `TARIH` | DATE |

## R_IADE_ONAY_HAVUZU *(göç dışı)*

| Kolon | Tip (test şemasında) |
| --- | --- |
| `MALZEME` | VARCHAR2(128) |
| `KAYNAK` | VARCHAR2(128) |
| `HEDEF` | VARCHAR2(128) |
| `TARIH` | DATE |

## R_SARFDEMIRBAS

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `GUID` | VARCHAR2(36) |
| `ADI` | VARCHAR2(50) |
| `ADET` | VARCHAR2(36) |
| `TUR` | VARCHAR2(36) |
| `ACIKLAMA` | VARCHAR2(200) |
| `DURUM` | CHAR(1) |
| `K_TARIH` | DATE |
| `ALANDEPO` | VARCHAR2(36) — *007'de yoktu, test verisinde eklendi; gerçek adını kontrol et* |

## R_SAYIM *(göç dışı)*

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | VARCHAR2(36) |
| `TUR` | CHAR(1) |
| `BASLIK` | VARCHAR2(60) |
| `BASTAR` | DATE |
| `BITTAR` | DATE |
| `DEPO` | VARCHAR2(128) |
| `DURUM` | NUMBER |

## R_SAYIM_GENEL *(göç dışı)*

| Kolon | Tip (test şemasında) |
| --- | --- |
| `SAYIM` | VARCHAR2(128) |
| `MALZEME` | VARCHAR2(128) |
| `DEPO` | VARCHAR2(128) |
| `ISLEM` | CHAR(1) |
| `TARIH` | DATE |
| `M_ADI` | VARCHAR2(255) |
| `MARKA` | VARCHAR2(128) |
| `MODEL` | VARCHAR2(128) |
| `SERI_NO` | VARCHAR2(50) |
| `ACIKLAMA` | VARCHAR2(150) |
| `MERKEZNOT` | VARCHAR2(150) |
| `IHALEGUID` | VARCHAR2(128) |
| `GARANTI_BASLANGIC_TARIHI` | DATE |
| `GARANTI_BITIS_TARIHI` | DATE |
| `DURUM` | VARCHAR2(10) |
| `SAYMANLIKNO` | VARCHAR2(100) |
| `ZIMMETSAHIBI` | VARCHAR2(100) |
| `ILSUBENO` | VARCHAR2(100) |
| `ALIM_TUR` | VARCHAR2(20) |

## R_SAYIM_YEREL *(göç dışı)*

| Kolon | Tip (test şemasında) |
| --- | --- |
| `SAYIM` | VARCHAR2(128) |
| `MALZEME` | VARCHAR2(128) |
| `ZIMMET` | VARCHAR2(100) |
| `ISLEM` | CHAR(1) |
| `ITARIH` | DATE |
| `TUR` | CHAR(1) |

## R_SQL_KAYIT

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `GUID` | VARCHAR2(128) |
| `ADI` | VARCHAR2(100) |
| `ICERIK` | CLOB |
| `KULLANICI` | VARCHAR2(100) |
| `DURUM` | VARCHAR2(10) |
| `K_TARIH` | DATE |

## R_TUKETIMCINSLERI

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `GUID` | VARCHAR2(36) |
| `ADI` | VARCHAR2(100) |
| `MODEL` | VARCHAR2(36) |
| `FIRMA` | VARCHAR2(36) |
| `SERINO` | VARCHAR2(100) |
| `A_TARIH` | DATE |
| `ALANDEPO` | VARCHAR2(36) |
| `ACIKLAMA` | VARCHAR2(200) |
| `DURUM` | CHAR(1) |
| `K_TARIH` | DATE |

## SARF_FIRMA

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `AD` | VARCHAR2(255) |
| `YETKILI` | VARCHAR2(255) |
| `YETKILI_TEL` | VARCHAR2(50) |
| `IRTIBAT` | VARCHAR2(255) |
| `IRTIBAT_TEL` | VARCHAR2(50) |
| `TEL1` | VARCHAR2(50) |
| `TEL2` | VARCHAR2(50) |
| `FAX` | VARCHAR2(50) |
| `WEB` | VARCHAR2(255) |
| `ADRES` | VARCHAR2(500) |
| `NOTE` | VARCHAR2(500) |

## SARF_KATEGORI

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GUID` | VARCHAR2(128) |
| `PARENT` | VARCHAR2(128) |
| `TITLE` | VARCHAR2(120) |

## SARF_LOG

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | NUMBER |
| `SICIL` | VARCHAR2(50) |
| `BILGI` | VARCHAR2(1000) |
| `ISLEM` | VARCHAR2(20) |
| `MALZEME` | VARCHAR2(128) |
| `DEPO` | VARCHAR2(128) |
| `TARIH` | DATE |

## SARF_MARKA

| Kolon | Tip (test şemasında) |
| --- | --- |
| `ID` | VARCHAR2(36) |
| `AD` | VARCHAR2(255) |
| `AKTIF` | CHAR(1) |

## SARF_MODEL

| Kolon | Tip (test şemasında) |
| --- | --- |
| `GID` | VARCHAR2(36) |
| `MARKA` | VARCHAR2(36) |
| `AD` | VARCHAR2(255) |
| `AKTIF` | CHAR(1) |
