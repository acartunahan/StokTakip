package com.stoktakip.migration.service;

import com.stoktakip.enums.SayimKapsami;
import com.stoktakip.enums.EnvanterTuru;
import com.stoktakip.enums.KaynakAlani;
import com.stoktakip.entity.Aparat;
import com.stoktakip.entity.Marka;
import com.stoktakip.entity.OrtakKategori;
import com.stoktakip.entity.Firma;
import com.stoktakip.entity.TuketimCinsi;
import com.stoktakip.entity.Depo;
import com.stoktakip.entity.EnvanterBarkod;
import com.stoktakip.entity.EnvanterDetay;
import com.stoktakip.entity.EnvanterResim;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.EnvanterHareketi;
import com.stoktakip.entity.EnvanterAciklama;
import com.stoktakip.entity.Model;
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
import com.stoktakip.migration.entity.MigrationRun;
import com.stoktakip.repository.AparatRepository;
import com.stoktakip.repository.MarkaRepository;
import com.stoktakip.repository.FirmaRepository;
import com.stoktakip.repository.TuketimCinsiRepository;
import com.stoktakip.repository.DepoRepository;
import com.stoktakip.repository.EnvanterBarkodRepository;
import com.stoktakip.repository.EnvanterResimRepository;
import com.stoktakip.repository.EnvanterRepository;
import com.stoktakip.repository.EnvanterHareketiRepository;
import com.stoktakip.repository.EnvanterAciklamaRepository;
import com.stoktakip.repository.ModelRepository;
import com.stoktakip.repository.OrtakKategoriRepository;
import com.stoktakip.repository.IadeOnayKaydiRepository;
import com.stoktakip.repository.IadeHavuzKaydiRepository;
import com.stoktakip.repository.SqlKaydiRepository;
import com.stoktakip.repository.SayimDetayRepository;
import com.stoktakip.repository.SayimRepository;
import com.stoktakip.repository.SarfLogKaydiRepository;
import com.stoktakip.repository.IhaleRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationService {

    private static final LegacyTableSpec[] VERI_GOC_SIRASI = {
        LegacyTableSpec.RESAT_DEPO,
        LegacyTableSpec.RESAT_MARKA,
        LegacyTableSpec.SARF_MARKA,
        LegacyTableSpec.RESAT_MODEL,
        LegacyTableSpec.SARF_MODEL,
        LegacyTableSpec.RESAT_FIRMA,
        LegacyTableSpec.SARF_FIRMA,
        LegacyTableSpec.RESAT_SABIT_KOD,
        LegacyTableSpec.R_TUKETIMCINSLERI,
        LegacyTableSpec.SARF_KATEGORI,
        LegacyTableSpec.RESAT_IHALE,
        LegacyTableSpec.R_DEMIRBAS,
        LegacyTableSpec.R_SARFDEMIRBAS,
        LegacyTableSpec.R_APARAT,
        LegacyTableSpec.RESAT_DEMIRBAS_RESIM,
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
    private final DepoRepository depoRepository;
    private final MarkaRepository markaRepository;
    private final ModelRepository modelRepository;
    private final FirmaRepository firmaRepository;
    private final IhaleRepository ihaleRepository;
    private final TuketimCinsiRepository tuketimCinsiRepository;
    private final OrtakKategoriRepository ortakKategoriRepository;
    private final EnvanterRepository envanterRepository;
    private final AparatRepository aparatRepository;
    private final EnvanterResimRepository envanterResimRepository;
    private final EnvanterBarkodRepository envanterBarkodRepository;
    private final EnvanterAciklamaRepository envanterAciklamaRepository;
    private final EnvanterHareketiRepository envanterHareketiRepository;
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
        DepoRepository depoRepository,
        MarkaRepository markaRepository,
        ModelRepository modelRepository,
        FirmaRepository firmaRepository,
        IhaleRepository ihaleRepository,
        TuketimCinsiRepository tuketimCinsiRepository,
        OrtakKategoriRepository ortakKategoriRepository,
        EnvanterRepository envanterRepository,
        AparatRepository aparatRepository,
        EnvanterResimRepository envanterResimRepository,
        EnvanterBarkodRepository envanterBarkodRepository,
        EnvanterAciklamaRepository envanterAciklamaRepository,
        EnvanterHareketiRepository envanterHareketiRepository,
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
        this.depoRepository = depoRepository;
        this.markaRepository = markaRepository;
        this.modelRepository = modelRepository;
        this.firmaRepository = firmaRepository;
        this.ihaleRepository = ihaleRepository;
        this.tuketimCinsiRepository = tuketimCinsiRepository;
        this.ortakKategoriRepository = ortakKategoriRepository;
        this.envanterRepository = envanterRepository;
        this.aparatRepository = aparatRepository;
        this.envanterResimRepository = envanterResimRepository;
        this.envanterBarkodRepository = envanterBarkodRepository;
        this.envanterAciklamaRepository = envanterAciklamaRepository;
        this.envanterHareketiRepository = envanterHareketiRepository;
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
            return;
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
            case RESAT_DEMIRBAS_RESIM -> envanterResminiAktar(context, row);
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
            depot.setPersonelKodu(row.string("PERSONELKODU"));
        }, columns("ID", "GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH", "PERSONELKODU"));
    }

    private void resatMarkayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Marka", markaRepository, Marka::new, brand -> {
            brand.setAd(varsayilanMetin(row.string("ADI"), row.anahtar()));
            brand.setAciklama(row.string("ACIKLAMA"));
            brand.setKaynakAlani(KaynakAlani.DEMIRBAS);
        }, columns("ID", "GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void sarfMarkayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Marka", markaRepository, Marka::new, brand -> {
            brand.setAd(varsayilanMetin(row.string("AD"), row.anahtar()));
            brand.setKaynakAlani(KaynakAlani.SARF);
            brand.setDepo(cozucu.depot(context, row, "DEPO"));
            brand.setDurum(LegacyFlagMapper.durum(row.string("AKTIF")));
        }, columns("ID", "AD", "AKTIF", "DEPO"));
    }

    private void resatModeliAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Model", modelRepository, Model::new, model -> {
            model.setAd(varsayilanMetin(row.string("ADI"), row.anahtar()));
            model.setAciklama(row.string("ACIKLAMA"));
            model.setKaynakAlani(KaynakAlani.DEMIRBAS);
            model.setUstModel(cozucu.resatModel(context, row, "PARENT"));
        }, columns("ID", "GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH", "PARENT"));
    }

    private void sarfModeliAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Model", modelRepository, Model::new, model -> {
            model.setAd(varsayilanMetin(row.string("AD"), row.anahtar()));
            model.setKaynakAlani(KaynakAlani.SARF);
            model.setMarka(cozucu.sarfBrand(context, row, "MARKA"));
            model.setDepo(cozucu.depot(context, row, "DEPO"));
            model.setDurum(LegacyFlagMapper.durum(row.string("AKTIF")));
        }, columns("GID", "MARKA", "AD", "AKTIF", "DEPO"));
    }

    private void resatFirmayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Firma", firmaRepository, Firma::new, company -> {
            firmayiEsle(row, company, "FIRMA_ADI", "NOTLAR", "YETKILITEL", "IRTIBATTEL");
            company.setKaynakAlani(KaynakAlani.DEMIRBAS);
        }, columns("ID", "GUID", "FIRMA_ADI", "YETKILI", "YETKILITEL", "IRTIBAT", "IRTIBATTEL",
            "TEL1", "TEL2", "FAX", "WEB", "ADRES", "NOTLAR", "DURUM", "K_TARIH"));
    }

    private void sarfFirmayiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Firma", firmaRepository, Firma::new, company -> {
            firmayiEsle(row, company, "AD", "NOTE", "YETKILI_TEL", "IRTIBAT_TEL");
            company.setKaynakAlani(KaynakAlani.SARF);
            company.setDepo(cozucu.depot(context, row, "DEPO"));
        }, columns("GUID", "AD", "YETKILI", "YETKILI_TEL", "IRTIBAT", "IRTIBAT_TEL", "TEL1", "TEL2",
            "FAX", "WEB", "ADRES", "NOTE", "DEPO"));
    }

    private void firmayiEsle(
        LegacyRow row,
        Firma company,
        String adKolonu,
        String aciklamaKolonu,
        String yetkiliTelefonKolonu,
        String irtibatTelefonKolonu
    ) {
        company.setAd(varsayilanMetin(row.string(adKolonu), row.anahtar()));
        company.setYetkiliKisi(row.string("YETKILI"));
        company.setYetkiliTelefon(row.string(yetkiliTelefonKolonu));
        company.setIrtibatKisi(row.string("IRTIBAT"));
        company.setIrtibatTelefon(row.string(irtibatTelefonKolonu));
        company.setTelefon1(row.string("TEL1"));
        company.setTelefon2(row.string("TEL2"));
        company.setFaks(row.string("FAX"));
        company.setWebAdresi(row.string("WEB"));
        company.setAdres(row.string("ADRES"));
        company.setAciklama(row.string(aciklamaKolonu));
    }

    private void ihaleyiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Ihale", ihaleRepository, Ihale::new, tender -> {
            tender.setAd(varsayilanMetin(row.string("IHALE_ADI"), row.anahtar()));
            tender.setIhaleTarihi(row.dateTime("IHALE_TAR"));
            tender.setKabulTarihi(row.dateTime("KABUL_TAR"));
            tender.setSozlesmeTarihi(row.dateTime("SOZ_TAR"));
            tender.setBirimKodu(row.longValue("BIRIM"));
            tender.setPersonel(row.string("PERSONEL"));
            tender.setAciklama(row.string("ACIKLAMA"));
            tender.setKomisyonBaskani(row.string("KOM_BASKANI"));
            tender.setUye1(row.string("UYE1"));
            tender.setUye2(row.string("UYE2"));
            tender.setYerindeGaranti(row.longValue("YERINDE_GARANTI"));
            tender.setOnarimSuresi(row.longValue("ONARIM_SURESI"));
            tender.setFirma(cozucu.resatCompany(context, row, "FIRMA_ID"));
        }, columns("ID", "GUID", "IHALE_ADI", "IHALE_TAR", "KABUL_TAR", "BIRIM", "PERSONEL",
            "FIRMA_ID", "SOZ_TAR", "ACIKLAMA", "DURUM", "K_TARIH", "KOM_BASKANI", "UYE1", "UYE2",
            "YERINDE_GARANTI", "ONARIM_SURESI"));
    }

    private void sabitKoduAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "OrtakKategori", ortakKategoriRepository, OrtakKategori::new, kategori -> {
            kategori.setKategoriTuru("SABIT_KOD");
            kategori.setAd(varsayilanMetin(row.string("AD"), row.anahtar()));
            kategori.setAciklama(row.string("YORUM"));
            kategori.setUstKategori(cozucu.sabitKod(context, row, "PARENT"));
            kategori.setRDepoGuid(null);
            kategori.setDurum(tekKarakter(row.string("DURUM")));
        }, columns("ID", "GUID", "PARENT", "AD", "YORUM", "DURUM", "K_TARIH"));
    }

    private void tuketimCinsiniAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "TuketimCinsi", tuketimCinsiRepository, TuketimCinsi::new, type -> {
            type.setAd(varsayilanMetin(row.string("ADI"), row.anahtar()));
            type.setAciklama(row.string("ACIKLAMA"));
        }, columns("ID", "GUID", "ADI", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void kategoriyiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "OrtakKategori", ortakKategoriRepository, OrtakKategori::new, kategori -> {
            kategori.setKategoriTuru("SARF");
            kategori.setAd(varsayilanMetin(row.string("TITLE"), row.anahtar()));
            kategori.setAciklama(null);
            kategori.setUstKategori(cozucu.category(context, row, "PARENT"));
            kategori.setRDepoGuid(row.string("DEPO"));
            kategori.setDurum(tekKarakter(row.string("DURUM")));
        }, columns("GUID", "PARENT", "TITLE", "DEPO"));
    }

    private void demirbasiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Envanter", envanterRepository, Envanter::new, item -> {
            item.setEnvanterTuru(EnvanterTuru.DEMIRBAS);
            item.setAd(varsayilanMetin(row.string("M_ADI"), row.anahtar()));
            item.setSeriNo(row.string("SERI_NO"));
            item.setBirimKodu(row.longValue("BIRIM"));
            item.setAciklama(row.string("ACIKLAMA"));
            item.setZimmetliPersonelKodu(row.string("PERSZIMMET"));
            item.setSubeNo(row.string("ILSUBEDBASNO"));
            item.setSabitKod(cozucu.sabitKod(context, row, "SABITKOD"));
            item.setDepo(cozucu.depot(context, row, "DEPO_GUID"));
            item.setMarka(cozucu.resatBrand(context, row, "MARKA"));
            item.setModel(cozucu.resatModel(context, row, "MODEL"));
            EnvanterDetay detail = item.detayOlustur();
            detail.setAlimYolu(row.longValue("ALIM_YOLU"));
            detail.setAlimTuru(row.string("ALIM_TUR"));
            detail.setGarantiBaslangicTarihi(row.dateTime("G_BAS_TAR"));
            detail.setGarantiBitisTarihi(row.dateTime("G_BIT_TAR"));
            detail.setSaymanlikNo(row.string("SAYMANLIKNO"));
            detail.setIhale(cozucu.tender(context, row, "IHALE_ID"));
        }, columns("ID", "GUID", "SERI_NO", "BIRIM", "SABITKOD", "DEPO_GUID",
            "ALIM_YOLU", "M_ADI", "MARKA", "MODEL", "IHALE_ID", "G_BAS_TAR",
            "G_BIT_TAR", "ACIKLAMA", "DURUM", "K_TARIH", "SAYMANLIKNO", "PERSZIMMET",
            "ILSUBEDBASNO", "ALIM_TUR"));
    }

    private void sarfiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Envanter", envanterRepository, Envanter::new, item -> {
            item.setEnvanterTuru(EnvanterTuru.SARF);
            item.setAd(varsayilanMetin(row.string("TUR"), row.anahtar()));
            item.setAdetMetni(row.string("ADET"));
            item.setSarfTurMetni(row.string("TUR"));
            item.setAlimTarihi(row.dateTime("A_TARIH"));
            item.setAciklama(row.string("ACIKLAMA"));
            item.setTuketimCinsi(cozucu.tuketimCinsi(context, row, "ADI"));
            item.setMarka(cozucu.resatBrand(context, row, "MARKA"));
            item.setModel(cozucu.resatModel(context, row, "MODEL"));
            item.setDepo(cozucu.depot(context, row, "ALANDEPO"));
            item.detayOlustur().setFirma(cozucu.resatCompany(context, row, "FIRMA"));
        }, columns("ID", "GUID", "ADI", "MARKA", "MODEL", "ADET", "TUR",
            "FIRMA", "A_TARIH", "ACIKLAMA", "DURUM", "K_TARIH", "ALANDEPO"));
    }

    private void aparatiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "Aparat", aparatRepository, Aparat::new, apparatus -> {
            apparatus.setAd(varsayilanMetin(row.string("APA_ADI"), row.anahtar()));
            apparatus.setAciklama(row.string("ACIKLAMA"));
            apparatus.setMarka(cozucu.resatBrand(context, row, "MARKA"));
            apparatus.setModel(cozucu.resatModel(context, row, "MODEL"));
        }, columns("ID", "GUID", "APA_ADI", "MARKA", "MODEL", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void envanterResminiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "EnvanterResim", envanterResimRepository, EnvanterResim::new, image -> {
            image.setAd(row.string("ADI"));
            image.setResimYolu(row.string("YOLU"));
            image.setAciklama(row.string("ACIKLAMA"));
            image.setMarka(cozucu.resatBrand(context, row, "MARKA"));
            image.setModel(cozucu.resatModel(context, row, "MODEL"));
        }, columns("ID", "MARKA", "MODEL", "ADI", "YOLU", "ACIKLAMA", "DURUM", "K_TARIH"));
    }

    private void barkoduAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "EnvanterBarkod", envanterBarkodRepository, EnvanterBarkod::new, barkod -> {
            barkod.setEnvanter(cozucu.inventory(context, row, "MGUID"));
            barkod.setBarkod(varsayilanMetin(row.string("BARKOD"), row.anahtar()));
            barkod.setSeriMi(LegacyFlagMapper.evetHayir(row.string("SERIMI")));
        }, columns("ID", "MGUID", "BARKOD", "SERIMI"));
    }

    private void notuAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "EnvanterAciklama", envanterAciklamaRepository, EnvanterAciklama::new, note -> {
            note.setEnvanter(cozucu.inventory(context, row, "MGUID"));
            note.setNotMetni(row.string("ACIKLAMA"));
            note.setZimmetliPersonelMetni(row.string("ZIMMETSAHIBI"));
            note.setEkleyen(row.string("EKLEYEN"));
            note.setEkleyenPersonel(row.string("EKLEYENSICIL"));
        }, columns("ID", "MGUID", "ACIKLAMA", "ZIMMETSAHIBI", "K_TARIH", "EKLEYEN", "EKLEYENSICIL"));
    }

    private void hareketiAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "EnvanterHareketi", envanterHareketiRepository, EnvanterHareketi::new, movement -> {
            movement.setEnvanter(cozucu.inventory(context, row, "MGUID"));
            movement.setHareketTuru(LegacyFlagMapper.hareketTuru(row.string("ADI")));
            movement.setAd(row.string("ADI"));
            movement.setNeredenDepo(cozucu.depot(context, row, "NERDEN"));
            movement.setNereyeDepo(cozucu.depot(context, row, "NEREYE"));
            movement.setPersonelKodu(row.string("KULLANICI"));
            movement.setAciklama(row.string("ACIKLAMA"));
            movement.setHareketNo(row.longValue("HNO"));
        }, columns("ID", "MGUID", "ADI", "NERDEN", "NEREYE", "KULLANICI", "ACIKLAMA", "K_TARIH", "HNO"));
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
            log.setSarfMalzemeGuid(row.string("MALZEME"));
            log.setDepo(cozucu.depot(context, row, "DEPO"));
        }, columns("ID", "SICIL", "BILGI", "ISLEM", "MALZEME", "DEPO", "TARIH"));
    }

    private void sqlKaydiniAktar(MigrationContext context, LegacyRow row) {
        yazici.yaz(context, row, "SqlKaydi", sqlKaydiRepository, SqlKaydi::new, sql -> {
            sql.setAd(row.string("ADI"));
            sql.setIcerik(row.string("ICERIK"));
            sql.setPersonelKodu(row.string("KULLANICI"));
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
            case RESAT_SABIT_KOD -> ortakKategoriRepository.count();
            case R_TUKETIMCINSLERI -> tuketimCinsiRepository.count();
            case SARF_KATEGORI -> ortakKategoriRepository.count();
            case R_DEMIRBAS, R_SARFDEMIRBAS -> envanterRepository.count();
            case R_APARAT -> aparatRepository.count();
            case RESAT_DEMIRBAS_RESIM -> envanterResimRepository.count();
            case R_BARKOD -> envanterBarkodRepository.count();
            case R_DEMIRBAS_ACIKLAMA -> envanterAciklamaRepository.count();
            case R_HAREKET -> envanterHareketiRepository.count();
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

    private static String tekKarakter(String value) {
        return value == null || value.isBlank() ? null : value.substring(0, 1);
    }

    private static String ilkDolu(String ilkDolu, String second) {
        return ilkDolu == null || ilkDolu.isBlank() ? second : ilkDolu;
    }
}
