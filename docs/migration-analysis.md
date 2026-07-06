# Eski Sistem Analizi ve Veri Göç Tasarımı

## 1. Eski Sistem Analizi

`RESAT_*` tabloları demirbaş ana sözlüklerini, depo, firma, ihale, marka/model ve sabit kod kayıtlarını taşır. `R_*` tabloları demirbaş, barkod, hareket, not, sayım ve iade süreçlerini taşır. `SARF_*` tabloları sarf malzeme tarafındaki firma, marka, model, kategori ve log kayıtlarını taşır.

Eski sistemde foreign key constraint olmadığı için ilişkiler GUID kolonlarından çıkarılır. Örnekler: `MARKAGUID -> Marka`, `MODELGUID -> Model`, `FIRMA_GUID/FIRMAGUID -> Firma`, `R_DEMIRBASGUID -> Envanter`, `R_SAYIMGUID -> Sayim`.

## 2. Mevcut Entity Taslaklarındaki Riskler

- `guid` alanı hedef entity anahtarıdır; GUID olmayan eski detay kayıtlarında tekrar çalıştırılabilirlik için sentetik satır anahtarı kullanılır.
- `Envanter` ile `Depo` ilişkisinin `OneToOne` olması doğru değildir; bir depoda çok sayıda envanter olabilir.
- Sarf ve demirbaş firma/marka/model yapıları tek domain modeli altında `KaynakAlani` ile ayrıştırılmalıdır.
- Barkod, hareket, not, sayım, iade ve log kayıtları ayrı child entity olarak tutulmalıdır.
- Hedefte karşılığı olmayan kolonlar kaybolmamalıdır; bu yüzden ham satır JSON olarak saklanır.

## 3. Önerilen Türkçe Entity Modeli

- `Depo`: `RESAT_DEPO`
- `Marka`: `RESAT_MARKA`, `SARF_MARKA`
- `Model`: `RESAT_MODEL`, `SARF_MODEL`
- `Firma`: `RESAT_FIRMA`, `SARF_FIRMA`
- `Ihale`: `RESAT_IHALE`
- `TuketimCinsi`: `R_TUKETIMCINSLERI`
- `Kategori`: `RESAT_SABIT_KOD`, `SARF_KATEGORI`
- `Envanter`: `R_DEMIRBAS`, `R_SARFDEMIRBAS`
- `EnvanterDetay`: Envanter ile ayni `GUID` uzerinden bire bir baglanan satin alma detaylari
- `Aparat`, `EnvanterAciklama`, `DepoZimmetHareket`, `PersonelZimmetHareket`
- `Sayim`, `SayimDetay`
- `IadeHavuzKaydi`, `IadeOnayKaydi`
- `SarfLogKaydi`, `SqlKaydi`
- `MigrationIdMap`, `MigrationLegacyPayload`, `MigrationErrorLog`, `MigrationRun`, `MigrationValidationReport`

## 4. Eski Tablo -> Yeni Entity Eşleme

| Eski tablo | Yeni entity | Not |
|---|---|---|
| RESAT_DEPO | Depo | Ana depo kaydı |
| RESAT_MARKA, SARF_MARKA | Marka | `KaynakAlani` ile ayrılır |
| RESAT_MODEL, SARF_MODEL | Model | Parent/self relation desteklenir |
| RESAT_FIRMA, SARF_FIRMA | Firma | Tek firma modeli |
| RESAT_IHALE | Ihale | Firma ilişkisi opsiyoneldir |
| RESAT_SABIT_KOD, SARF_KATEGORI | Kategori | `TUR` ile ayrilir; `PARENT` GUID uzerinden self relation desteklenir |
| R_TUKETIMCINSLERI | TuketimCinsi | Sarf tüketim cinsi |
| R_DEMIRBAS | Envanter, EnvanterDetay | `EnvanterTuru.DEMIRBAS`; alim yolu/turu, saymanlik no, ihale ve garanti bilgileri detaydadir |
| R_SARFDEMIRBAS | Envanter, EnvanterDetay | `EnvanterTuru.SARF`; firma bilgisi detaydadir |
| R_BARKOD | Envanter.BARKOD | Demirbas barkod kolonu |
| R_HAREKET | DepoZimmetHareket, PersonelZimmetHareket | `NERDEN`/`NEREYE` durumuna gore zimmet hareketi |
| R_SAYIM_GENEL, R_SAYIM_YEREL | SayimDetay | Detay kayıtları |

## 5. Veri Kaybını Önleme Stratejisi

- `Kategori` disindaki ana entity kayitlarinda `id`, `guid`, `olusturmaTarihi`, `durum` alanlari bulunur; kategori tarihi `tarih` alaninda tutulur.
- Idempotency `VERI_GOC_ID_ESLEME` tablosunda `ESKI_TABLO_ADI + ESKI_ANAHTAR + HEDEF_VARLIK_ADI` ile sağlanır.
- Hedefte açık alanı olmayan her legacy kolon `VERI_GOC_ESKI_VERI_YUKU.veriYukuJson` içinde korunur; payload ve hata kayıtlarında kaynak tablo adı ayrıca tutulur.
- İlişkisi bulunamayan kayıt atlanmaz; eksik ilişki `VERI_GOC_HATA_KAYDI` tablosuna yazılır.
- ID/GUID olmayan legacy detay tablolarında sentetik satır anahtarı üretilir.
- `R_SAYIM_GENEL` icindeki marka, model, ihale, garanti, saymanlik, sube ve alim turu snapshot alanlari `SayimDetay` uzerinde sorgulanabilir alanlar olarak tutulur.

## 6. Veri Göç Sırası

Sıra: depo, marka, model, firma, ortak kategori, tüketim cinsi, ihale, demirbaş, sarf, aparat, barkod, not, hareket, sayım, sayım detayları, iade, sarf log, SQL kayıt.

## 7. Teknik Yaklaşım

- Kaynak okuma: `JdbcTemplate`, parça bazlı `OFFSET/FETCH`.
- Hedef yazma: Spring Data JPA.
- Varsayılan çalışma: kuru çalışma.
- Hata stratejisi: tek satır hatası tüm veri göçünü durdurmaz.
- Rapor: kaynak satır, hedef satır, aktarılan, atlanan, tekrar, hatalı, eksik ilişki, null/bozuk GUID ve payload sayaçları JSON özetinde tutulur.
