package com.stoktakip.migration.service;

import com.stoktakip.enums.SayimKapsami;
import com.stoktakip.enums.EnvanterTuru;
import com.stoktakip.enums.KaynakAlani;
import com.stoktakip.entity.Aparat;
import com.stoktakip.entity.DepoZimmetHareket;
import com.stoktakip.entity.Marka;
import com.stoktakip.entity.Kategori;
import com.stoktakip.entity.Firma;
import com.stoktakip.entity.TuketimCinsi;
import com.stoktakip.entity.Depo;
import com.stoktakip.entity.EnvanterDetay;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.EnvanterAciklama;
import com.stoktakip.entity.Model;
import com.stoktakip.entity.PersonelZimmetHareket;
import com.stoktakip.entity.IadeOnayKaydi;
import com.stoktakip.entity.IadeHavuzKaydi;
import com.stoktakip.entity.SqlKaydi;
import com.stoktakip.entity.Sayim;
import com.stoktakip.entity.SayimDetay;
import com.stoktakip.entity.SarfLogKaydi;
import com.stoktakip.entity.Ihale;
import com.stoktakip.migration.config.MigrationProperties;
import com.stoktakip.migration.core.LegacyFlagMapper;
import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.LegacyTableSpec;
import com.stoktakip.migration.core.MigrationContext;
import com.stoktakip.migration.entity.MigrationIdMap;
import com.stoktakip.migration.entity.MigrationRun;
import com.stoktakip.migration.repository.MigrationIdMapRepository;
import com.stoktakip.repository.AparatRepository;
import com.stoktakip.repository.DepoZimmetHareketRepository;
import com.stoktakip.repository.MarkaRepository;
import com.stoktakip.repository.FirmaRepository;
import com.stoktakip.repository.TuketimCinsiRepository;
import com.stoktakip.repository.DepoRepository;
import com.stoktakip.repository.EnvanterRepository;
import com.stoktakip.repository.EnvanterAciklamaRepository;
import com.stoktakip.repository.ModelRepository;
import com.stoktakip.repository.KategoriRepository;
import com.stoktakip.repository.PersonelZimmetHareketRepository;
import com.stoktakip.repository.IadeOnayKaydiRepository;
import com.stoktakip.repository.IadeHavuzKaydiRepository;
import com.stoktakip.repository.SqlKaydiRepository;
import com.stoktakip.repository.SayimDetayRepository;
import com.stoktakip.repository.SayimRepository;
import com.stoktakip.repository.SarfLogKaydiRepository;
import com.stoktakip.repository.IhaleRepository;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationService {

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private static final LegacyTableSpec[] VERI_GOC_SIRASI = {
        LegacyTableSpec.RESAT_DEPO,
        LegacyTableSpec.RESAT_MARKA,
        LegacyTableSpec.SARF_MARKA,
        LegacyTableSpec.RESAT_SABIT_KOD,
        LegacyTableSpec.RESAT_MODEL,
        LegacyTableSpec.SARF_MODEL,
        LegacyTableSpec.RESAT_FIRMA,
        LegacyTableSpec.SARF_FIRMA,
        LegacyTableSpec.R_TUKETIMCINSLERI,
        LegacyTableSpec.SARF_KATEGORI,
        LegacyTableSpec.RESAT_IHALE,
        LegacyTableSpec.R_DEMIRBAS,
        LegacyTableSpec.R_SARFDEMIRBAS,
        LegacyTableSpec.R_APARAT,
        LegacyTableSpec.R_BARKOD,
        LegacyTableSpec.R_DEMIRBAS_ACIKLAMA,
        LegacyTableSpec.R_HAREKET,
        LegacyTableSpec.R_SAYIM,
        LegacyTableSpec.R_SAYIM_GENEL,
        LegacyTableSpec.R_SAYIM_YEREL,
        LegacyTableSpec.R_IADE_HAVUZU,
        LegacyTableSpec.R_IADE_ONAY_HAVUZU,
        LegacyTableSpec.SARF_LOG,
        LegacyTableSpec.R_SQL_KAYIT
    };

    private final MigrationProperties ayarlar;
    private final LegacySourceReader okuyucu;
    private final TargetEntityWriter yazici;
    private final LegacyReferenceResolver cozucu;
    private final MigrationAuditService denetimServisi;
    private final TransactionTemplate transactionTemplate;
    private final MigrationIdMapRepository idMapRepository;
    private final DepoRepository depoRepository;
    private final MarkaRepository markaRepository;
    private final ModelRepository modelRepository;
    private final FirmaRepository firmaRepository;
    private final IhaleRepository ihaleRepository;
    private final TuketimCinsiRepository tuketimCinsiRepository;
    private final KategoriRepository kategoriRepository;
    private final EnvanterRepository envanterRepository;
    private final AparatRepository aparatRepository;
    private final EnvanterAciklamaRepository envanterAciklamaRepository;
    private final DepoZimmetHareketRepository depoZimmetHareketRepository;
    private final PersonelZimmetHareketRepository personelZimmetHareketRepository;
    private final SayimRepository sayimRepository;
    private final SayimDetayRepository sayimDetayRepository;
    private final IadeHavuzKaydiRepository iadeHavuzKaydiRepository;
    private final IadeOnayKaydiRepository iadeOnayKaydiRepository;
    private final SarfLogKaydiRepository sarfLogKaydiRepository;
    private final SqlKaydiRepository sqlKaydiRepository;

    public LegacyMigrationService(
        MigrationProperties ayarlar,
        LegacySourceReader okuyucu,
        TargetEntityWriter yazici,
        LegacyReferenceResolver cozucu,
        MigrationAuditService denetimServisi,
        TransactionTemplate transactionTemplate,
        MigrationIdMapRepository idMapRepository,
        DepoRepository depoRepository,
        MarkaRepository markaRepository,
        ModelRepository modelRepository,
        FirmaRepository firmaRepository,
        IhaleRepository ihaleRepository,
        TuketimCinsiRepository tuketimCinsiRepository,
        KategoriRepository kategoriRepository,
        EnvanterRepository envanterRepository,
        AparatRepository aparatRepository,
        EnvanterAciklamaRepository envanterAciklamaRepository,
        DepoZimmetHareketRepository depoZimmetHareketRepository,
        PersonelZimmetHareketRepository personelZimmetHareketRepository,
        SayimRepository sayimRepository,
        SayimDetayRepository sayimDetayRepository,
        IadeHavuzKaydiRepository iadeHavuzKaydiRepository,
        IadeOnayKaydiRepository iadeOnayKaydiRepository,
        SarfLogKaydiRepository sarfLogKaydiRepository,
        SqlKaydiRepository sqlKaydiRepository
    ) {
        this.ayarlar = ayarlar;
        this.okuyucu = okuyucu;
        this.yazici = yazici;
        this.cozucu = cozucu;
        this.denetimServisi = denetimServisi;
        this.transactionTemplate = transactionTemplate;
        this.idMapRepository = idMapRepository;
        this.depoRepository = depoRepository;
        this.markaRepository = markaRepository;
        this.modelRepository = modelRepository;
        this.firmaRepository = firmaRepository;
        this.ihaleRepository = ihaleRepository;
        this.tuketimCinsiRepository = tuketimCinsiRepository;
        this.kategoriRepository = kategoriRepository;
        this.envanterRepository = envanterRepository;
        this.aparatRepository = aparatRepository;
        this.envanterAciklamaRepository = envanterAciklamaRepository;
        this.depoZimmetHareketRepository = depoZimmetHareketRepository;
        this.personelZimmetHareketRepository = personelZimmetHareketRepository;
        this.sayimRepository = sayimRepository;
        this.sayimDetayRepository = sayimDetayRepository;
        this.iadeHavuzKaydiRepository = iadeHavuzKaydiRepository;
        this.iadeOnayKaydiRepository = iadeOnayKaydiRepository;
        this.sarfLogKaydiRepository = sarfLogKaydiRepository;
        this.sqlKaydiRepository = sqlKaydiRepository;
    }

    public String calistir() {
        MigrationRun calistir = denetimServisi.calismaBaslat(ayarlar.isDryRun());
        MigrationContext context = new MigrationContext(calistir.getId(), ayarlar.isDryRun());
        try {
            for (LegacyTableSpec spec : VERI_GOC_SIRASI) {
                tabloyuAktar(context, spec);
            }
            String summary = context.ozetJson();
            denetimServisi.raporla(context, "veri-goc-dogrulama-ozeti", summary);
            denetimServisi.calismaTamamla(calistir, "SUCCESS", summary);
            return summary;
        } catch (RuntimeException exception) {
            String summary = context.ozetJson();
            denetimServisi.calismaTamamla(calistir, "FAILED", summary);
            throw exception;
        }
    }

    private void tabloyuAktar(MigrationContext context, LegacyTableSpec spec) {
        long kaynakSatirSayisi = okuyucu.say(spec);
        context.stats(spec.tabloAdi()).kaynakSatirSayisi(kaynakSatirSayisi);
        int chunkSize = Math.max(1, ayarlar.getChunkSize());
        if (spec == LegacyTableSpec.RESAT_SABIT_KOD || spec == LegacyTableSpec.SARF_KATEGORI) {
            parentOncelikliTabloyuAktar(context, spec, kaynakSatirSayisi, chunkSize);
            return;
        }
        for (int offset = 0; offset < kaynakSatirSayisi; offset += chunkSize) {
            for (LegacyRow row : okuyucu.parcaOku(spec, offset, chunkSize)) {
                try {
                    satiriAktar(context, spec, row);
                } catch (RuntimeException exception) {
                    denetimServisi.hata(context, row, null, "Row migration failed: " + exception.getMessage(), exception);
                }
            }
        }
        context.stats(spec.tabloAdi()).hedefSatirSayisi(hedefSayisi(spec));
    }

    private void parentOncelikliTabloyuAktar(
        MigrationContext context,
        LegacyTableSpec spec,
        long kaynakSatirSayisi,
        int chunkSize
    ) {
        List<LegacyRow> satirlar = new ArrayList<>();
        for (int offset = 0; offset < kaynakSatirSayisi; offset += chunkSize) {
            satirlar.addAll(okuyucu.parcaOku(spec, offset, chunkSize));
        }
        for (LegacyRow row : parentOncelikliSirala(spec, satirlar)) {
            try {
                satiriAktar(context, spec, row);
            } catch (RuntimeException exception) {
                denetimServisi.hata(context, row, null, "Row migration failed: " + exception.getMessage(), exception);
            }
        }
        context.stats(spec.tabloAdi()).hedefSatirSayisi(hedefSayisi(spec));
    }

    private static List<LegacyRow> parentOncelikliSirala(LegacyTableSpec spec, List<LegacyRow> satirlar) {
        String parentKolonu = parentKolonu(spec);
        Map<String, LegacyRow> guidSatirlari = new LinkedHashMap<>();
        for (LegacyRow row : satirlar) {
            String guid = row.guid();
            if (guid != null && !guid.isBlank()) {
                guidSatirlari.putIfAbsent(guid, row);
            }
        }

        List<LegacyRow> sirali = new ArrayList<>();
        Set<String> ziyaretEdilen = new HashSet<>();
        Set<String> ziyaretEdiliyor = new HashSet<>();
        for (LegacyRow row : satirlar) {
            parentOncelikliEkle(row, parentKolonu, guidSatirlari, ziyaretEdilen, ziyaretEdiliyor, sirali);
        }
        return sirali;
    }

    private static void parentOncelikliEkle(
        LegacyRow row,
        String parentKolonu,
        Map<String, LegacyRow> guidSatirlari,
        Set<String> ziyaretEdilen,
        Set<String> ziyaretEdiliyor,
        List<LegacyRow> sirali
    ) {
        String guid = row.guid();
        if (guid == null || guid.isBlank()) {
            sirali.add(row);
            return;
        }
        if (ziyaretEdilen.contains(guid)) {
            return;
        }
        if (!ziyaretEdiliyor.add(guid)) {
            throw new IllegalStateException("Kategori parent dongusu bulundu: " + guid);
        }

        LegacyRow parent = guidSatirlari.get(row.string(parentKolonu));
        if (parent != null) {
            parentOncelikliEkle(parent, parentKolonu, guidSatirlari, ziyaretEdilen, ziyaretEdiliyor, sirali);
        }
        ziyaretEdiliyor.remove(guid);
        ziyaretEdilen.add(guid);
        sirali.add(row);
    }

    private static String parentKolonu(LegacyTableSpec spec) {
        return switch (spec) {
            case RESAT_SABIT_KOD, SARF_KATEGORI -> "PARENT";
            default -> throw new IllegalArgumentException("Parent kolonu yok: " + spec.tabloAdi());
        };
    }

    private void satiriAktar(MigrationContext context, LegacyTableSpec spec, LegacyRow row) {
        switch (spec) {
            case RESAT_DEPO -> depoyuAktar(context, row);
            case RESAT_MARKA -> resatMarkayiAktar(context, row);
            case SARF_MARKA -> sarfMarkayiAktar(context, row);
            case RESAT_MODEL -> resatModeliAktar(context, row);
            case SARF_MODEL -> sarfModeliAktar(context, row);
            case RESAT_FIRMA -> resatFirmayiAktar(context, row);
            case SARF_FIRMA -> sarfFirmayiAktar(context, row);
            case RESAT_IHALE -> ihaleyiAktar(context, row);
            case RESAT_SABIT_KOD -> sabitKoduAktar(context, row);
            case R_TUKETIMCINSLERI -> tuketimCinsiniAktar(context, row);
            case SARF_KATEGORI -> kategoriyiAktar(context, row);
            case R_DEMIRBAS -> demirbasiAktar(context, row);
            case R_SARFDEMIRBAS -> sarfiAktar(context, row);
            case R_APARAT -> aparatiAktar(context, row);
            case R_BARKOD -> barkoduAktar(context, row);
            case R_DEMIRBAS_ACIKLAMA -> notuAktar(context, row);
            case R_HAREKET -> hareketiAktar(context, row);
            case R_SAYIM -> sayimiAktar(context, row);
            case R_SAYIM_GENEL -> genelSayimDetayiniAktar(context, row);
            case R_SAYIM_YEREL -> yerelSayimDetayiniAktar(context, row);
            case R_IADE_HAVUZU -> iadeHavuzunuAktar(context, row);
            case R_IADE_ONAY_HAVUZU -> iadeOnayiniAktar(context, row);
            case SARF_LOG -> sarfLogunuAktar(context, row);
            case R_SQL_KAYIT -> sqlKaydiniAktar(context, row);
        }
    }

    private void depoyuAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Depo", depoRepository, Depo::new, depot -> {
            depot.setAd(varsayilanMetin(row.string("ADI"), row.anahtar()));
            depot.setAciklama(row.string("ACIKLAMA"));
            depot.setPersonelKodu(sinirliMetin(context, row, "PERSONELKODU", 20));
        }, columns("ID", "GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH", "PERSONELKODU"));
    }

    private void resatMarkayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Marka", markaRepository, Marka::new, marka -> {
            alanYaz(marka, "markaAdi", varsayilanMetin(row.string("ADI"), row.anahtar()));
            alanYaz(marka, "aciklama", row.string("ACIKLAMA"));
            alanYaz(marka, "aktif", aktifDegeri(row.string("DURUM")));
        }, columns("GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void sarfMarkayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Marka", markaRepository, Marka::new, marka -> {
            alanYaz(marka, "markaAdi", varsayilanMetin(row.string("AD"), row.anahtar()));
            alanYaz(marka, "aktif", aktifDegeri(row.string("AKTIF")));
        }, columns("ID", "AD", "AKTIF"));
    }

    private void resatModeliAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Model", modelRepository, Model::new, model -> {
            alanYaz(model, "modelAdi", varsayilanMetin(row.string("ADI"), row.anahtar()));
            alanYaz(model, "aciklama", row.string("ACIKLAMA"));
            alanYaz(model, "aktif", aktifDegeri(row.string("DURUM")));
            alanYaz(model, "marka", cozucu.resatBrand(context, row, "PARENT"));
            alanYaz(model, "kategori", cozucu.sabitKod(context, row, "SABITKOD"));
        }, columns("GUID", "ADI", "SABITKOD", "ACIKLAMA", "DURUM", "K_TARIH", "PARENT"));
    }

    private void sarfModeliAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Model", modelRepository, Model::new, model -> {
            alanYaz(model, "modelAdi", varsayilanMetin(row.string("AD"), row.anahtar()));
            alanYaz(model, "aktif", aktifDegeri(row.string("AKTIF")));
            alanYaz(model, "marka", cozucu.sarfBrand(context, row, "MARKA"));
        }, columns("GID", "MARKA", "AD", "AKTIF"));
    }

    private void resatFirmayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Firma", firmaRepository, Firma::new, company -> {
            firmayiEsle(row, company, "FIRMA_ADI", "NOTLAR", "YETKILITEL", "IRTIBATTEL");
            alanYaz(company, "kaynakAlani", KaynakAlani.DEMIRBAS);
        }, columns("GUID", "FIRMA_ADI", "YETKILI", "YETKILITEL", "IRTIBAT", "IRTIBATTEL",
            "TEL1", "TEL2", "FAX", "WEB", "ADRES", "NOTLAR", "DURUM", "K_TARIH"));
    }

    private void sarfFirmayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Firma", firmaRepository, Firma::new, company -> {
            firmayiEsle(row, company, "AD", "NOTE", "YETKILI_TEL", "IRTIBAT_TEL");
            alanYaz(company, "kaynakAlani", KaynakAlani.SARF);
        }, columns("GUID", "AD", "YETKILI", "YETKILI_TEL", "IRTIBAT", "IRTIBAT_TEL", "TEL1", "TEL2",
            "FAX", "WEB", "ADRES", "NOTE"));
    }

    private void firmayiEsle(
        LegacyRow row,
        Firma company,
        String adKolonu,
        String aciklamaKolonu,
        String yetkiliTelefonKolonu,
        String irtibatTelefonKolonu
    ) {
        alanYaz(company, "ad", varsayilanMetin(row.string(adKolonu), row.anahtar()));
        alanYaz(company, "yetkiliKisi", row.string("YETKILI"));
        alanYaz(company, "yetkiliTelefon", row.string(yetkiliTelefonKolonu));
        alanYaz(company, "irtibatKisi", row.string("IRTIBAT"));
        alanYaz(company, "irtibatTelefon", row.string(irtibatTelefonKolonu));
        alanYaz(company, "telefon1", row.string("TEL1"));
        alanYaz(company, "telefon2", row.string("TEL2"));
        alanYaz(company, "faks", row.string("FAX"));
        alanYaz(company, "webAdresi", row.string("WEB"));
        alanYaz(company, "adres", row.string("ADRES"));
        alanYaz(company, "aciklama", row.string(aciklamaKolonu));
    }

    private void ihaleyiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Ihale", ihaleRepository, Ihale::new, tender -> {
            alanYaz(tender, "ad", varsayilanMetin(row.string("IHALE_ADI"), row.anahtar()));
            alanYaz(tender, "ihaleTarihi", row.dateTime("IHALE_TAR"));
            alanYaz(tender, "kabulTarihi", row.dateTime("KABUL_TAR"));
            alanYaz(tender, "sozlesmeTarihi", row.dateTime("SOZ_TAR"));
            alanYaz(tender, "birimKodu", row.longValue("BIRIM"));
            alanYaz(tender, "personel", row.string("PERSONEL"));
            alanYaz(tender, "aciklama", row.string("ACIKLAMA"));
            alanYaz(tender, "komisyonBaskani", row.string("KOM_BASKANI"));
            alanYaz(tender, "uye1", row.string("UYE1"));
            alanYaz(tender, "uye2", row.string("UYE2"));
            alanYaz(tender, "yerindeGaranti", row.longValue("YERINDE_GARANTI"));
            alanYaz(tender, "onarimSuresi", row.longValue("ONARIM_SURESI"));
            alanYaz(tender, "firma", cozucu.resatCompany(context, row, "FIRMA_ID"));
        }, columns("GUID", "IHALE_ADI", "IHALE_TAR", "KABUL_TAR", "BIRIM", "PERSONEL",
            "FIRMA_ID", "SOZ_TAR", "ACIKLAMA", "DURUM", "K_TARIH", "KOM_BASKANI", "UYE1", "UYE2",
            "YERINDE_GARANTI", "ONARIM_SURESI"));
    }

    private void sabitKoduAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Kategori", kategoriRepository, Kategori::new, kategori -> {
            alanYaz(kategori, "parent", kategoriParentiniCoz(context, row));
            alanYaz(kategori, "baslik", varsayilanMetin(row.string("AD"), row.anahtar()));
            alanYaz(kategori, "aktif", aktifDegeri(row.string("DURUM")));
            alanYaz(kategori, "olusturmaTarihi", row.dateTime("K_TARIH"));
        }, columns("GUID", "PARENT", "AD", "DURUM", "K_TARIH"));
    }

    private void tuketimCinsiniAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "TuketimCinsi", tuketimCinsiRepository, TuketimCinsi::new, type -> {
            type.setAd(varsayilanMetin(row.string("ADI"), row.anahtar()));
            type.setAciklama(row.string("ACIKLAMA"));
        }, columns("GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void kategoriyiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Kategori", kategoriRepository, Kategori::new, kategori -> {
            alanYaz(kategori, "parent", kategoriParentiniCoz(context, row));
            alanYaz(kategori, "baslik", varsayilanMetin(row.string("TITLE"), row.anahtar()));
        }, columns("GUID", "PARENT", "TITLE"));
    }

    private Kategori kategoriParentiniCoz(MigrationContext context, LegacyRow row) {
        String parentGuid = row.string("PARENT");
        if (parentGuid == null || parentGuid.isBlank()) {
            return null;
        }

        String kategoriGuid = hedefGuid(row);
        if (parentGuid.equals(kategoriGuid)) {
            context.stats(row.tabloAdi()).eksikIliski();
            throw new IllegalStateException(row.tabloAdi() + ".PARENT kategori kendi GUID degeri olamaz: " + parentGuid);
        }

        Kategori parent = kategoriRepository.findByGuid(parentGuid)
            .orElseThrow(() -> {
                context.stats(row.tabloAdi()).eksikIliski();
                return new IllegalStateException(
                    row.tabloAdi() + ".PARENT hedef KATEGORI.GUID icinde bulunamadi: " + parentGuid
                );
            });
        if (kategoriParentDongusuOlusur(kategoriGuid, parent)) {
            context.stats(row.tabloAdi()).eksikIliski();
            throw new IllegalStateException(
                row.tabloAdi() + ".PARENT kategori parent dongusu olusturur: "
                    + kategoriGuid + " -> " + parentGuid
            );
        }
        return parent;
    }

    private void demirbasiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Envanter", envanterRepository, Envanter::new, item -> {
            alanYaz(item, "envanterTuru", EnvanterTuru.DEMIRBAS);
            alanYaz(item, "demirbasNumarasi", row.longValue("D_NO"));
            alanYaz(item, "envanterAdi", varsayilanMetin(row.string("M_ADI"), row.anahtar()));
            alanYaz(item, "seriNo", row.string("SERI_NO"));
            alanYaz(item, "sube", row.longValue("BIRIM"));
            alanYaz(item, "aciklama", row.string("ACIKLAMA"));
            alanYaz(item, "ilSubeDaireBaskanlikNumarasi", row.string("ILSUBEDBASNO"));
            alanYaz(item, "depo", cozucu.depot(context, row, "DEPO_GUID"));
            alanYaz(item, "model", cozucu.resatModel(context, row, "MODEL"));
            EnvanterDetay detail = envanterDetayiOlustur(item);
            alanYaz(detail, "alimYolu", row.longValue("ALIM_YOLU"));
            alanYaz(detail, "alimTuru", row.string("ALIM_TUR"));
            alanYaz(detail, "garantiBaslangicTarihi", row.dateTime("G_BAS_TAR"));
            alanYaz(detail, "garantiBitisTarihi", row.dateTime("G_BIT_TAR"));
            alanYaz(detail, "saymanlikNo", row.string("SAYMANLIKNO"));
            alanYaz(detail, "ihale", cozucu.tender(context, row, "IHALE_ID"));
        }, columns("GUID", "D_NO", "SERI_NO", "BIRIM", "DEPO_GUID",
            "ALIM_YOLU", "M_ADI", "MARKA", "MODEL", "IHALE_ID", "G_BAS_TAR",
            "G_BIT_TAR", "ACIKLAMA", "DURUM", "K_TARIH", "SAYMANLIKNO", "PERSZIMMET",
            "ILSUBEDBASNO", "ALIM_TUR"));
    }

    private void sarfiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Envanter", envanterRepository, Envanter::new, item -> {
            alanYaz(item, "envanterTuru", EnvanterTuru.SARF);
            alanYaz(item, "envanterAdi", varsayilanMetin(row.string("TUR"), row.anahtar()));
            alanYaz(item, "seriNo", sinirliMetin(context, row, "SERINO", 100));
            alanYaz(item, "aciklama", row.string("ACIKLAMA"));
            alanYaz(item, "model", cozucu.sarfModel(context, row, "MODEL"));
            alanYaz(item, "depo", cozucu.depot(context, row, "ALANDEPO"));
            EnvanterDetay detail = envanterDetayiOlustur(item);
            alanYaz(detail, "firma", cozucu.sarfCompany(context, row, "FIRMA"));
        }, columns("GUID", "ADI", "MARKA", "MODEL", "ADET", "TUR",
            "FIRMA", "SERINO", "A_TARIH", "ACIKLAMA", "DURUM", "K_TARIH", "ALANDEPO"));
    }

    private void aparatiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Aparat", aparatRepository, Aparat::new, apparatus -> {
            alanYaz(apparatus, "aparatAdi", varsayilanMetin(row.string("APA_ADI"), row.anahtar()));
            alanYaz(apparatus, "aciklama", row.string("ACIKLAMA"));
            alanYaz(apparatus, "marka", aparatinMarkasiniCoz(context, row));
            alanYaz(apparatus, "model", aparatinModeliniCoz(context, row));
        }, columns("GUID", "APA_ADI", "MARKA", "MODEL", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private Marka aparatinMarkasiniCoz(MigrationContext context, LegacyRow row) {
        String markaKolonu = row.string("MARKAGUID") != null ? "MARKAGUID" : "MARKA";
        return cozucu.resatBrand(context, row, markaKolonu);
    }

    private Model aparatinModeliniCoz(MigrationContext context, LegacyRow row) {
        String modelKolonu = row.string("MODELGUID") != null ? "MODELGUID" : "MODEL";
        return cozucu.resatModel(context, row, modelKolonu);
    }

    private void barkoduAktar(MigrationContext context, LegacyRow row) {
        String hedefVarlikAdi = "Envanter";
        if (idMapRepository
            .findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(row.tabloAdi(), row.anahtar(), hedefVarlikAdi)
            .isPresent()) {
            context.stats(row.tabloAdi()).tekrar();
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        String barkod = row.string("BARKOD");
        if (barkod == null) {
            denetimServisi.hata(context, row, "BARKOD", "Barkod degeri bos oldugu icin ENVANTER.BARKOD guncellenmedi.", null);
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        Envanter envanter = barkodEnvanteriniCoz(context, row);
        if (envanter == null) {
            if (barkodEnvanterGuidKolonu(row) == null) {
                denetimServisi.hata(context, row, null, "Barkod satirinda envanter GUID bilgisi bulunamadigi icin aktarim atlandi.", null);
            }
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        String hedefGuid = String.valueOf(alanOku(envanter, "guid"));
        String mevcutBarkod = (String) alanOku(envanter, "barkod");
        if (mevcutBarkod != null && !mevcutBarkod.isBlank()) {
            denetimServisi.hata(
                context,
                row,
                "BARKOD",
                "Ayni envanter icin birden fazla barkod kaydi bulundu. Mevcut barkod korunacak: " + hedefGuid,
                null
            );
            if (!context.dryRun()) {
                transactionTemplate.executeWithoutResult(status -> {
                    idEslemeOlustur(context, row, hedefVarlikAdi, hedefGuid);
                    denetimServisi.veriYuku(context, row, hedefVarlikAdi, hedefGuid, barkodKolonlari());
                });
            } else {
                denetimServisi.veriYuku(context, row, hedefVarlikAdi, hedefGuid, barkodKolonlari());
            }
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        alanYaz(envanter, "barkod", barkod);
        if (context.dryRun()) {
            denetimServisi.veriYuku(context, row, hedefVarlikAdi, hedefGuid, barkodKolonlari());
            context.stats(row.tabloAdi()).aktarildi();
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            Envanter kaydedilen = envanterRepository.save(envanter);
            String kaydedilenGuid = String.valueOf(alanOku(kaydedilen, "guid"));
            idEslemeOlustur(context, row, hedefVarlikAdi, kaydedilenGuid);
            denetimServisi.veriYuku(context, row, hedefVarlikAdi, kaydedilenGuid, barkodKolonlari());
        });
        context.stats(row.tabloAdi()).aktarildi();
    }

    private Envanter barkodEnvanteriniCoz(MigrationContext context, LegacyRow row) {
        String guidKolonu = barkodEnvanterGuidKolonu(row);
        return guidKolonu == null ? null : cozucu.inventory(context, row, guidKolonu);
    }

    private static String barkodEnvanterGuidKolonu(LegacyRow row) {
        if (row.string("R_DEMIRBASGUID") != null) {
            return "R_DEMIRBASGUID";
        }
        return row.string("MGUID") == null ? null : "MGUID";
    }

    private static Set<String> barkodKolonlari() {
        return columns("ID", "MGUID", "R_DEMIRBASGUID", "BARKOD");
    }

    private void notuAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "EnvanterAciklama", envanterAciklamaRepository, EnvanterAciklama::new, note -> {
            note.setEnvanter(cozucu.inventory(context, row, "MGUID"));
            note.setNotMetni(row.string("ACIKLAMA"));
            note.setZimmetliPersonelMetni(row.string("ZIMMETSAHIBI"));
            note.setEkleyen(row.string("EKLEYEN"));
            note.setEkleyenPersonel(sinirliMetin(context, row, "EKLEYENSICIL", 20));
        }, columns("ID", "MGUID", "ACIKLAMA", "ZIMMETSAHIBI", "K_TARIH", "EKLEYEN", "EKLEYENSICIL"));
    }

    private void hareketiAktar(MigrationContext context, LegacyRow row) {
        Long kaynakId = row.id();
        if (kaynakId == null) {
            denetimServisi.hata(context, row, "ID", "R_HAREKET.ID bos oldugu icin hareket kaydi aktarilmadi.", null);
            context.stats(row.tabloAdi()).atlandi();
            return;
        }
        if (zimmetHareketiVarMi(kaynakId)) {
            context.stats(row.tabloAdi()).tekrar();
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        Envanter envanter = cozucu.inventory(context, row, "MGUID");
        if (envanter == null) {
            if (row.string("MGUID") == null) {
                denetimServisi.hata(context, row, "MGUID", "R_HAREKET.MGUID bos oldugu icin hareket kaydi aktarilmadi.", null);
            }
            context.stats(row.tabloAdi()).atlandi();
            return;
        }

        if (depoHareketiMi(row)) {
            DepoZimmetHareket hareket = new DepoZimmetHareket();
            ortakZimmetHareketAlanlariniYaz(hareket, kaynakId, envanter, row);
            hareket.setMevcutDepo(depoVarsa(context, row, "NERDEN"));
            hareket.setGidecegiDepo(depoVarsa(context, row, "NEREYE"));
            depoZimmetHareketiniKaydet(context, row, hareket);
            return;
        }

        PersonelZimmetHareket hareket = new PersonelZimmetHareket();
        ortakZimmetHareketAlanlariniYaz(hareket, kaynakId, envanter, row);
        personelZimmetHareketiniKaydet(context, row, hareket);
    }

    private boolean zimmetHareketiVarMi(Long kaynakId) {
        return depoZimmetHareketRepository.existsById(kaynakId)
            || personelZimmetHareketRepository.existsById(kaynakId);
    }

    private static boolean depoHareketiMi(LegacyRow row) {
        return row.string("NERDEN") != null || row.string("NEREYE") != null;
    }

    private Depo depoVarsa(MigrationContext context, LegacyRow row, String column) {
        return row.string(column) == null ? null : cozucu.depot(context, row, column);
    }

    private static void ortakZimmetHareketAlanlariniYaz(
        DepoZimmetHareket hareket,
        Long kaynakId,
        Envanter envanter,
        LegacyRow row
    ) {
        hareket.setId(kaynakId);
        hareket.setEnvanter(envanter);
        hareket.setZimmetTuru(row.string("ADI"));
        hareket.setIslemYapanPersonel(row.string("KULLANICI"));
        hareket.setAciklama(row.string("ACIKLAMA"));
        hareket.setOlusturmaTarihi(row.dateTime("K_TARIH"));
    }

    private static void ortakZimmetHareketAlanlariniYaz(
        PersonelZimmetHareket hareket,
        Long kaynakId,
        Envanter envanter,
        LegacyRow row
    ) {
        hareket.setId(kaynakId);
        hareket.setEnvanter(envanter);
        hareket.setZimmetTuru(row.string("ADI"));
        hareket.setIslemYapanPersonel(row.string("KULLANICI"));
        hareket.setAciklama(row.string("ACIKLAMA"));
        hareket.setOlusturmaTarihi(row.dateTime("K_TARIH"));
    }

    private void depoZimmetHareketiniKaydet(MigrationContext context, LegacyRow row, DepoZimmetHareket hareket) {
        if (context.dryRun()) {
            context.stats(row.tabloAdi()).aktarildi();
            denetimServisi.veriYuku(context, row, "DepoZimmetHareket", null, hareketKolonlari());
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            DepoZimmetHareket kaydedilen = depoZimmetHareketRepository.save(hareket);
            String hedefId = String.valueOf(kaydedilen.getId());
            idEslemeOlustur(context, row, "DepoZimmetHareket", hedefId);
            denetimServisi.veriYuku(context, row, "DepoZimmetHareket", hedefId, hareketKolonlari());
        });
        context.stats(row.tabloAdi()).aktarildi();
    }

    private void personelZimmetHareketiniKaydet(
        MigrationContext context,
        LegacyRow row,
        PersonelZimmetHareket hareket
    ) {
        if (context.dryRun()) {
            context.stats(row.tabloAdi()).aktarildi();
            denetimServisi.veriYuku(context, row, "PersonelZimmetHareket", null, hareketKolonlari());
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            PersonelZimmetHareket kaydedilen = personelZimmetHareketRepository.save(hareket);
            String hedefId = String.valueOf(kaydedilen.getId());
            idEslemeOlustur(context, row, "PersonelZimmetHareket", hedefId);
            denetimServisi.veriYuku(context, row, "PersonelZimmetHareket", hedefId, hareketKolonlari());
        });
        context.stats(row.tabloAdi()).aktarildi();
    }

    private void idEslemeOlustur(MigrationContext context, LegacyRow row, String hedefVarlikAdi, String hedefVarlikGuid) {
        if (idMapRepository
            .findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(row.tabloAdi(), row.anahtar(), hedefVarlikAdi)
            .isPresent()) {
            return;
        }

        MigrationIdMap idMap = new MigrationIdMap();
        idMap.setRunId(context.calismaId());
        idMap.setEskiId(row.id());
        idMap.setEskiGuid(row.guid());
        idMap.setEskiTabloAdi(row.tabloAdi());
        idMap.setEskiAnahtar(row.anahtar());
        idMap.setHedefVarlikAdi(hedefVarlikAdi);
        idMap.setHedefVarlikGuid(hedefVarlikGuid);
        idMap.setMigratedAt(LocalDateTime.now());
        idMapRepository.save(idMap);
    }

    private static Set<String> hareketKolonlari() {
        return columns("ID", "MGUID", "ADI", "NERDEN", "NEREYE", "KULLANICI", "ACIKLAMA", "K_TARIH");
    }

    private void sayimiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Sayim", sayimRepository, Sayim::new, say -> {
            say.setSayimKapsami(LegacyFlagMapper.sayimKapsami(row.string("TUR")));
            say.setBaslik(row.string("BASLIK"));
            say.setBaslangicTarihi(row.dateTime("BASTAR"));
            say.setBitisTarihi(row.dateTime("BITTAR"));
            say.setDepo(cozucu.depot(context, row, "DEPO"));
            say.setEskiDurumKodu(row.longValue("DURUM"));
        }, columns("ID", "TUR", "BASLIK", "BASTAR", "BITTAR", "DEPO", "DURUM"));
    }

    private void genelSayimDetayiniAktar(MigrationContext context, LegacyRow row) {
        LegacyRow synthetic = row.withSyntheticGuid(sentetikAnahtar(row, "SAYIM", "MALZEME", "DEPO", "TARIH", "ISLEM"));
        yazici.yaz(context, synthetic, "SayimDetay", sayimDetayRepository, SayimDetay::new, detail -> {
            detail.setSayimKapsami(SayimKapsami.GENEL);
            detail.setSayim(cozucu.stockCount(context, synthetic, "SAYIM"));
            detail.setEnvanter(cozucu.inventory(context, synthetic, "MALZEME"));
            detail.setDepo(cozucu.depot(context, synthetic, "DEPO"));
            detail.setIslem(LegacyFlagMapper.sayimIslemi(row.string("ISLEM")));
            detail.setSayimTarihi(row.dateTime("TARIH"));
            detail.setAnlikAd(row.string("M_ADI"));
            detail.setMarka(cozucu.resatBrand(context, synthetic, "MARKA"));
            detail.setModel(cozucu.resatModel(context, synthetic, "MODEL"));
            detail.setIhale(cozucu.tender(context, synthetic, "IHALEGUID"));
            detail.setGarantiBaslangicTarihi(row.dateTime("GARANTI_BASLANGIC_TARIHI"));
            detail.setGarantiBitisTarihi(row.dateTime("GARANTI_BITIS_TARIHI"));
            detail.setAciklama(row.string("ACIKLAMA"));
            detail.setSaymanlikNo(row.string("SAYMANLIKNO"));
            detail.setZimmetliPersonelKodu(row.string("ZIMMETSAHIBI"));
            detail.setSubeNo(row.string("ILSUBENO"));
            detail.setAlimTuru(row.string("ALIM_TUR"));
        }, columns("SAYIM", "MALZEME", "DEPO", "ISLEM", "TARIH",
            "M_ADI", "MARKA", "MODEL", "IHALEGUID", "GARANTI_BASLANGIC_TARIHI",
            "GARANTI_BITIS_TARIHI", "ACIKLAMA", "DURUM", "SAYMANLIKNO", "ZIMMETSAHIBI",
            "ILSUBENO", "ALIM_TUR"));
    }

    private void yerelSayimDetayiniAktar(MigrationContext context, LegacyRow row) {
        LegacyRow synthetic = row.withSyntheticGuid(sentetikAnahtar(row, "SAYIM", "MALZEME", "ZIMMET", "ITARIH", "ISLEM", "TUR"));
        yazici.yaz(context, synthetic, "SayimDetay", sayimDetayRepository, SayimDetay::new, detail -> {
            detail.setSayimKapsami(LegacyFlagMapper.sayimKapsami(row.string("TUR")));
            detail.setSayim(cozucu.stockCount(context, synthetic, "SAYIM"));
            detail.setEnvanter(cozucu.inventory(context, synthetic, "MALZEME"));
            detail.setIslem(LegacyFlagMapper.sayimIslemi(row.string("ISLEM")));
            detail.setSayimTarihi(row.dateTime("ITARIH"));
            detail.setZimmetliPersonelKodu(row.string("ZIMMET"));
        }, columns("SAYIM", "MALZEME", "ZIMMET", "ISLEM", "ITARIH", "TUR"));
    }

    private void iadeHavuzunuAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "IadeHavuzKaydi", iadeHavuzKaydiRepository, IadeHavuzKaydi::new, entry -> {
            entry.setEnvanter(cozucu.inventory(context, row, "MGUID"));
            entry.setEskiDurumKodu(row.longValue("DURUM"));
        }, columns("ID", "MGUID", "DURUM", "TARIH"));
    }

    private void iadeOnayiniAktar(MigrationContext context, LegacyRow row) {
        LegacyRow synthetic = row.withSyntheticGuid(sentetikAnahtar(row, "MALZEME", "KAYNAK", "HEDEF", "TARIH"));
        yazici.yaz(context, synthetic, "IadeOnayKaydi", iadeOnayKaydiRepository, IadeOnayKaydi::new, entry -> {
            entry.setEnvanter(cozucu.inventory(context, synthetic, "MALZEME"));
            entry.setSuankiDepo(cozucu.depot(context, synthetic, "KAYNAK"));
            entry.setGidecegiDepo(cozucu.depot(context, synthetic, "HEDEF"));
        }, columns("MALZEME", "KAYNAK", "HEDEF", "TARIH"));
    }

    private void sarfLogunuAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "SarfLogKaydi", sarfLogKaydiRepository, SarfLogKaydi::new, log -> {
            log.setPersonelKodu(row.string("SICIL"));
            log.setAciklama(row.string("BILGI"));
            log.setIslem(row.string("ISLEM"));
            log.setSarfMalzemeGuid(sinirliMetin(context, row, "MALZEME", 64));
            log.setDepo(cozucu.depot(context, row, "DEPO"));
        }, columns("ID", "SICIL", "BILGI", "ISLEM", "MALZEME", "DEPO", "TARIH"));
    }

    private void sqlKaydiniAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "SqlKaydi", sqlKaydiRepository, SqlKaydi::new, sql -> {
            sql.setAd(row.string("ADI"));
            sql.setIcerik(row.string("ICERIK"));
            sql.setPersonelKodu(sinirliMetin(context, row, "KULLANICI", 20));
        }, columns("ID", "GUID", "ADI", "ICERIK", "KULLANICI", "DURUM", "K_TARIH"));
    }

    private static Set<String> columns(String... columns) {
        return Set.of(columns);
    }

    private long hedefSayisi(LegacyTableSpec spec) {
        return switch (spec) {
            case RESAT_DEPO -> depoRepository.count();
            case RESAT_MARKA, SARF_MARKA -> markaRepository.count();
            case RESAT_MODEL, SARF_MODEL -> modelRepository.count();
            case RESAT_FIRMA, SARF_FIRMA -> firmaRepository.count();
            case RESAT_IHALE -> ihaleRepository.count();
            case RESAT_SABIT_KOD -> kategoriRepository.count();
            case R_TUKETIMCINSLERI -> tuketimCinsiRepository.count();
            case SARF_KATEGORI -> kategoriRepository.count();
            case R_DEMIRBAS, R_SARFDEMIRBAS -> envanterRepository.count();
            case R_APARAT -> aparatRepository.count();
            case R_BARKOD -> envanterRepository.countByBarkodIsNotNull();
            case R_DEMIRBAS_ACIKLAMA -> envanterAciklamaRepository.count();
            case R_HAREKET -> depoZimmetHareketRepository.count() + personelZimmetHareketRepository.count();
            case R_SAYIM -> sayimRepository.count();
            case R_SAYIM_GENEL, R_SAYIM_YEREL -> sayimDetayRepository.count();
            case R_IADE_HAVUZU -> iadeHavuzKaydiRepository.count();
            case R_IADE_ONAY_HAVUZU -> iadeOnayKaydiRepository.count();
            case SARF_LOG -> sarfLogKaydiRepository.count();
            case R_SQL_KAYIT -> sqlKaydiRepository.count();
        };
    }

    private static String sentetikAnahtar(LegacyRow row, String... columns) {
        return Arrays.stream(columns)
            .map(column -> column + '=' + Objects.toString(ilkDolu(row.string(column), String.valueOf(row.get(column))), ""))
            .collect(Collectors.joining("|"));
    }

    private static String varsayilanMetin(String value, String fallback) {
        return value == null || value.isBlank() ? "LEGACY " + fallback : value;
    }

    private String sinirliMetin(MigrationContext context, LegacyRow row, String column, int maxLength) {
        String value = row.string(column);
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        denetimServisi.hata(
            context,
            row,
            column,
            column + " hedef kolon uzunlugunu astigi icin " + maxLength
                + " karaktere kisaltildi. Tam deger payload JSON icinde korunuyor.",
            null
        );
        return value.substring(0, maxLength);
    }

    private static Boolean aktifDegeri(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "1", "A", "AKTIF", "TRUE", "E", "Y" -> Boolean.TRUE;
            case "0", "P", "PASIF", "FALSE", "H", "N", "D", "S", "SIL", "SILINMIS" -> Boolean.FALSE;
            default -> null;
        };
    }

    private static boolean kategoriParentDongusuOlusur(String kategoriGuid, Kategori parent) {
        Set<String> ziyaretEdilen = new HashSet<>();
        Kategori current = parent;
        while (current != null) {
            String currentGuid = stringDegeri(alanOku(current, "guid"));
            if (currentGuid == null || !ziyaretEdilen.add(currentGuid)) {
                return true;
            }
            if (currentGuid.equals(kategoriGuid)) {
                return true;
            }
            Object next = alanOku(current, "parent");
            current = next instanceof Kategori kategori ? kategori : null;
        }
        return false;
    }

    private static String hedefGuid(LegacyRow row) {
        String guid = row.guid();
        return guid == null || guid.isBlank() ? row.anahtar() : guid;
    }

    private static String stringDegeri(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static EnvanterDetay envanterDetayiOlustur(Envanter item) {
        Object mevcutDetay = alanOku(item, "detay");
        if (mevcutDetay instanceof EnvanterDetay detay) {
            return detay;
        }

        EnvanterDetay detay = new EnvanterDetay();
        Object envanterGuid = alanOku(item, "guid");
        alanYaz(detay, "envanter", item);
        alanYaz(detay, "guid", envanterDetayGuid(envanterGuid));
        alanYaz(item, "detay", detay);
        return detay;
    }

    private static String envanterDetayGuid(Object envanterGuid) {
        if (envanterGuid == null) {
            throw new IllegalStateException("EnvanterDetay GUID uretmek icin Envanter.GUID bos olamaz.");
        }
        String source = "ENVANTER_DETAY:" + envanterGuid;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HEX_FORMAT.formatHex(digest.digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algoritmasi bulunamadi.", exception);
        }
    }

    private static void alanYaz(Object entity, String alanAdi, Object value) {
        try {
            Field field = alan(entity.getClass(), alanAdi);
            field.set(entity, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani yazilamadi: " + alanAdi, exception);
        }
    }

    private static Object alanOku(Object entity, String alanAdi) {
        try {
            Field field = alan(entity.getClass(), alanAdi);
            return field.get(entity);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani okunamadi: " + alanAdi, exception);
        }
    }

    private static Field alan(Class<?> type, String alanAdi) {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(alanAdi);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new IllegalStateException("Entity alani bulunamadi: " + alanAdi);
    }

    private static String ilkDolu(String ilkDolu, String second) {
        return ilkDolu == null || ilkDolu.isBlank() ? second : ilkDolu;
    }
}
