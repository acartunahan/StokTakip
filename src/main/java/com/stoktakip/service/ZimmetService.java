package com.stoktakip.service;

import com.stoktakip.dto.DepoTransferTalebi;
import com.stoktakip.dto.IadeTalebi;
import com.stoktakip.dto.ZimmetHareketYaniti;
import com.stoktakip.dto.ZimmetTalebi;
import com.stoktakip.entity.Aparat;
import com.stoktakip.entity.Depo;
import com.stoktakip.entity.DepoZimmetHareket;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.PersonelZimmetHareket;
import com.stoktakip.enums.ZimmetHareketTipi;
import com.stoktakip.enums.ZimmetVarlikTuru;
import com.stoktakip.exception.BusinessRuleException;
import com.stoktakip.repository.AparatRepository;
import com.stoktakip.repository.DepoRepository;
import com.stoktakip.repository.DepoZimmetHareketRepository;
import com.stoktakip.repository.EnvanterRepository;
import com.stoktakip.repository.PersonelZimmetHareketRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ZimmetService {

    private final EnvanterRepository envanterRepository;
    private final AparatRepository aparatRepository;
    private final DepoRepository depoRepository;
    private final PersonelZimmetHareketRepository personelHareketRepository;
    private final DepoZimmetHareketRepository depoHareketRepository;

    public ZimmetService(
        EnvanterRepository envanterRepository,
        AparatRepository aparatRepository,
        DepoRepository depoRepository,
        PersonelZimmetHareketRepository personelHareketRepository,
        DepoZimmetHareketRepository depoHareketRepository
    ) {
        this.envanterRepository = envanterRepository;
        this.aparatRepository = aparatRepository;
        this.depoRepository = depoRepository;
        this.personelHareketRepository = personelHareketRepository;
        this.depoHareketRepository = depoHareketRepository;
    }

    @Transactional
    public ZimmetHareketYaniti personeleZimmetle(ZimmetTalebi talep) {
        zorunlu(talep, talep == null ? null : talep.varlikTuru(), talep == null ? null : talep.varlikGuid());
        if (talep.personelKodu() == null || talep.personelKodu().isBlank()) {
            throw new BusinessRuleException("personelKodu zorunludur");
        }
        Varlik varlik = varligiKilitle(talep.varlikTuru(), talep.varlikGuid());
        if (aktifZimmet(varlik).isPresent()) {
            throw new BusinessRuleException("Varlik zaten aktif olarak bir personele zimmetli");
        }

        varlik.depodanCikar();
        PersonelZimmetHareket hareket = yeniPersonelHareket(varlik, ZimmetHareketTipi.PERSONELE_ZIMMET);
        hareket.setPersonelKodu(talep.personelKodu().trim());
        hareket.setAciklama(talep.aciklama());
        hareket.setIslemYapanPersonel(talep.islemYapanPersonel());
        hareket.setAktif(true);
        return yanit(personelHareketRepository.save(hareket), varlik);
    }

    @Transactional
    public ZimmetHareketYaniti iadeAl(IadeTalebi talep) {
        zorunlu(talep, talep == null ? null : talep.varlikTuru(), talep == null ? null : talep.varlikGuid());
        Varlik varlik = varligiKilitle(talep.varlikTuru(), talep.varlikGuid());
        PersonelZimmetHareket aktif = aktifZimmet(varlik)
            .orElseThrow(() -> new BusinessRuleException("Varligin aktif personel zimmeti bulunamadi"));
        aktif.setAktif(false);
        personelHareketRepository.save(aktif);

        if (talep.depoGuid() != null && !talep.depoGuid().isBlank()) {
            varlik.depoyaKoy(depo(talep.depoGuid()));
        }
        PersonelZimmetHareket iade = yeniPersonelHareket(varlik, ZimmetHareketTipi.IADE);
        iade.setPersonelKodu(aktif.getPersonelKodu());
        iade.setAciklama(talep.aciklama());
        iade.setIslemYapanPersonel(talep.islemYapanPersonel());
        iade.setAktif(false);
        return yanit(personelHareketRepository.save(iade), varlik);
    }

    @Transactional
    public ZimmetHareketYaniti depoyaTasi(DepoTransferTalebi talep) {
        zorunlu(talep, talep == null ? null : talep.varlikTuru(), talep == null ? null : talep.varlikGuid());
        if (talep.gidecegiDepoGuid() == null || talep.gidecegiDepoGuid().isBlank()) {
            throw new BusinessRuleException("gidecegiDepoGuid zorunludur");
        }
        Varlik varlik = varligiKilitle(talep.varlikTuru(), talep.varlikGuid());
        Depo mevcutDepo = varlik.depo();
        aktifZimmet(varlik).ifPresent(aktif -> {
            aktif.setAktif(false);
            personelHareketRepository.save(aktif);
        });
        Depo gidecegiDepo = depo(talep.gidecegiDepoGuid());
        varlik.depoyaKoy(gidecegiDepo);

        DepoZimmetHareket hareket = new DepoZimmetHareket();
        hareket.setId(depoHareketRepository.nextId());
        varlik.hareketeAta(hareket);
        hareket.setHareketTipi(ZimmetHareketTipi.DEPO_TRANSFERI);
        hareket.setZimmetTuru(varlik.altTur());
        hareket.setMevcutDepo(mevcutDepo);
        hareket.setGidecegiDepo(gidecegiDepo);
        hareket.setAciklama(talep.aciklama());
        hareket.setIslemYapanPersonel(talep.islemYapanPersonel());
        return yanit(depoHareketRepository.save(hareket), varlik);
    }

    @Transactional(readOnly = true)
    public List<ZimmetHareketYaniti> personelGecmisi(ZimmetVarlikTuru tur, String guid) {
        Varlik varlik = varligiBul(tur, guid);
        List<PersonelZimmetHareket> hareketler = tur == ZimmetVarlikTuru.ENVANTER
            ? personelHareketRepository.findByEnvanterGuidOrderByIdDesc(guid)
            : personelHareketRepository.findByAparatGuidOrderByIdDesc(guid);
        return hareketler.stream().map(h -> yanit(h, varlik)).toList();
    }

    @Transactional(readOnly = true)
    public List<ZimmetHareketYaniti> depoGecmisi(ZimmetVarlikTuru tur, String guid) {
        Varlik varlik = varligiBul(tur, guid);
        List<DepoZimmetHareket> hareketler = tur == ZimmetVarlikTuru.ENVANTER
            ? depoHareketRepository.findByEnvanterGuidOrderByIdDesc(guid)
            : depoHareketRepository.findByAparatGuidOrderByIdDesc(guid);
        return hareketler.stream().map(h -> yanit(h, varlik)).toList();
    }

    private java.util.Optional<PersonelZimmetHareket> aktifZimmet(Varlik varlik) {
        return varlik.tur() == ZimmetVarlikTuru.ENVANTER
            ? personelHareketRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc(varlik.guid())
            : personelHareketRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc(varlik.guid());
    }

    private PersonelZimmetHareket yeniPersonelHareket(Varlik varlik, ZimmetHareketTipi tip) {
        PersonelZimmetHareket hareket = new PersonelZimmetHareket();
        hareket.setId(personelHareketRepository.nextId());
        varlik.hareketeAta(hareket);
        hareket.setHareketTipi(tip);
        hareket.setZimmetTuru(varlik.altTur());
        return hareket;
    }

    private Varlik varligiKilitle(ZimmetVarlikTuru tur, String guid) {
        return switch (tur) {
            case ENVANTER -> Varlik.envanter(envanterRepository.findByGuidForUpdate(guid)
                .orElseThrow(() -> new BusinessRuleException("Envanter bulunamadi: " + guid)));
            case APARAT -> Varlik.aparat(aparatRepository.findByGuidForUpdate(guid)
                .orElseThrow(() -> new BusinessRuleException("Aparat bulunamadi: " + guid)));
        };
    }

    private Varlik varligiBul(ZimmetVarlikTuru tur, String guid) {
        zorunlu(guid, tur, guid);
        return switch (tur) {
            case ENVANTER -> Varlik.envanter(envanterRepository.findByGuid(guid)
                .orElseThrow(() -> new BusinessRuleException("Envanter bulunamadi: " + guid)));
            case APARAT -> Varlik.aparat(aparatRepository.findByGuid(guid)
                .orElseThrow(() -> new BusinessRuleException("Aparat bulunamadi: " + guid)));
        };
    }

    private Depo depo(String guid) {
        return depoRepository.findByGuid(guid)
            .orElseThrow(() -> new BusinessRuleException("Depo bulunamadi: " + guid));
    }

    private static void zorunlu(Object talep, ZimmetVarlikTuru tur, String guid) {
        if (talep == null || tur == null || guid == null || guid.isBlank()) {
            throw new BusinessRuleException("varlikTuru ve varlikGuid zorunludur");
        }
    }

    private static ZimmetHareketYaniti yanit(PersonelZimmetHareket hareket, Varlik varlik) {
        return new ZimmetHareketYaniti(hareket.getId(), varlik.tur(), varlik.guid(), varlik.altTur(),
            hareket.getHareketTipi(), hareket.getPersonelKodu(), null, null, hareket.getAciklama(),
            hareket.getOlusturmaTarihi(), Boolean.TRUE.equals(hareket.getAktif()));
    }

    private static ZimmetHareketYaniti yanit(DepoZimmetHareket hareket, Varlik varlik) {
        return new ZimmetHareketYaniti(hareket.getId(), varlik.tur(), varlik.guid(), varlik.altTur(),
            hareket.getHareketTipi(), null, guid(hareket.getMevcutDepo()), guid(hareket.getGidecegiDepo()),
            hareket.getAciklama(), hareket.getOlusturmaTarihi(), Boolean.TRUE.equals(hareket.getAktif()));
    }

    private static String guid(Depo depo) {
        return depo == null ? null : depo.getGuid();
    }

    private record Varlik(Envanter envanter, Aparat aparat) {
        static Varlik envanter(Envanter value) { return new Varlik(value, null); }
        static Varlik aparat(Aparat value) { return new Varlik(null, value); }
        ZimmetVarlikTuru tur() { return envanter != null ? ZimmetVarlikTuru.ENVANTER : ZimmetVarlikTuru.APARAT; }
        String guid() { return envanter != null ? envanter.getGuid() : aparat.getGuid(); }
        String altTur() { return envanter != null ? envanter.getEnvanterTuru().name() : "APARAT"; }
        Depo depo() { return envanter != null ? envanter.getDepo() : aparat.getDepo(); }
        void depodanCikar() { depoyaKoy(null); }
        void depoyaKoy(Depo depo) { if (envanter != null) envanter.setDepo(depo); else aparat.setDepo(depo); }
        void hareketeAta(PersonelZimmetHareket hareket) { if (envanter != null) hareket.setEnvanter(envanter); else hareket.setAparat(aparat); }
        void hareketeAta(DepoZimmetHareket hareket) { if (envanter != null) hareket.setEnvanter(envanter); else hareket.setAparat(aparat); }
    }
}
