package com.stoktakip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.stoktakip.dto.IadeTalebi;
import com.stoktakip.dto.DepoTransferTalebi;
import com.stoktakip.dto.ZimmetTalebi;
import com.stoktakip.entity.Aparat;
import com.stoktakip.entity.Depo;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.PersonelZimmetHareket;
import com.stoktakip.enums.EnvanterTuru;
import com.stoktakip.enums.ZimmetHareketTipi;
import com.stoktakip.enums.ZimmetVarlikTuru;
import com.stoktakip.exception.BusinessRuleException;
import com.stoktakip.repository.AparatRepository;
import com.stoktakip.repository.DepoRepository;
import com.stoktakip.repository.DepoZimmetHareketRepository;
import com.stoktakip.repository.EnvanterRepository;
import com.stoktakip.repository.PersonelZimmetHareketRepository;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZimmetServiceTest {

    @Mock EnvanterRepository envanterRepository;
    @Mock AparatRepository aparatRepository;
    @Mock DepoRepository depoRepository;
    @Mock PersonelZimmetHareketRepository personelRepository;
    @Mock DepoZimmetHareketRepository depoHareketRepository;

    private ZimmetService service;

    @BeforeEach
    void setUp() {
        AtomicLong ids = new AtomicLong(1_000_000_000_000L);
        lenient().when(personelRepository.nextId()).thenAnswer(ignored -> ids.getAndIncrement());
        lenient().when(personelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(depoHareketRepository.nextId()).thenAnswer(ignored -> ids.getAndIncrement());
        lenient().when(depoHareketRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new ZimmetService(envanterRepository, aparatRepository, depoRepository,
            personelRepository, depoHareketRepository);
    }

    @Test
    void demirbasPersoneleZimmetlenirVeIadeAlinir() {
        Envanter demirbas = envanter("demirbas-1", EnvanterTuru.DEMIRBAS);
        when(envanterRepository.findByGuidForUpdate("demirbas-1")).thenReturn(Optional.of(demirbas));
        when(personelRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc("demirbas-1"))
            .thenReturn(Optional.empty());

        var yanit = service.personeleZimmetle(talep(ZimmetVarlikTuru.ENVANTER, "demirbas-1", "P-A"));

        assertThat(yanit.envanterTuru()).isEqualTo("DEMIRBAS");
        assertThat(yanit.personelKodu()).isEqualTo("P-A");
        assertThat(yanit.hareketTipi()).isEqualTo(ZimmetHareketTipi.PERSONELE_ZIMMET);

        PersonelZimmetHareket aktif = aktifEnvanterZimmeti(demirbas, "P-A");
        when(personelRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc("demirbas-1"))
            .thenReturn(Optional.of(aktif));
        var iade = service.iadeAl(new IadeTalebi(
            ZimmetVarlikTuru.ENVANTER, "demirbas-1", null, null, "operator"));

        assertThat(iade.hareketTipi()).isEqualTo(ZimmetHareketTipi.IADE);
        assertThat(aktif.getAktif()).isFalse();
    }

    @Test
    void sarfPersoneleZimmetlenirVeIadeAlinir() {
        Envanter sarf = envanter("sarf-1", EnvanterTuru.SARF);
        when(envanterRepository.findByGuidForUpdate("sarf-1")).thenReturn(Optional.of(sarf));
        when(personelRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc("sarf-1"))
            .thenReturn(Optional.empty());
        service.personeleZimmetle(talep(ZimmetVarlikTuru.ENVANTER, "sarf-1", "P-S"));

        PersonelZimmetHareket aktif = aktifEnvanterZimmeti(sarf, "P-S");
        when(personelRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc("sarf-1"))
            .thenReturn(Optional.of(aktif));
        var iade = service.iadeAl(new IadeTalebi(ZimmetVarlikTuru.ENVANTER, "sarf-1", null, null, "operator"));

        assertThat(iade.envanterTuru()).isEqualTo("SARF");
        assertThat(iade.hareketTipi()).isEqualTo(ZimmetHareketTipi.IADE);
        assertThat(aktif.getAktif()).isFalse();
    }

    @Test
    void aparatPersoneleZimmetlenirVeIadeAlinir() {
        Aparat aparat = aparat("aparat-1");
        when(aparatRepository.findByGuidForUpdate("aparat-1")).thenReturn(Optional.of(aparat));
        when(personelRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc("aparat-1"))
            .thenReturn(Optional.empty());
        service.personeleZimmetle(talep(ZimmetVarlikTuru.APARAT, "aparat-1", "P-B"));

        PersonelZimmetHareket aktif = aktifAparatZimmeti(aparat, "P-B");
        when(personelRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc("aparat-1"))
            .thenReturn(Optional.of(aktif));
        var iade = service.iadeAl(new IadeTalebi(ZimmetVarlikTuru.APARAT, "aparat-1", null, null, "operator"));

        assertThat(iade.envanterTuru()).isEqualTo("APARAT");
        assertThat(iade.hareketTipi()).isEqualTo(ZimmetHareketTipi.IADE);
    }

    @Test
    void anaEnvanterVeAparatFarkliPersonellereZimmetlenebilir() {
        Envanter laptop = envanter("laptop", EnvanterTuru.DEMIRBAS);
        Aparat adaptor = aparat("adaptor");
        adaptor.setAnaEnvanter(laptop);
        when(envanterRepository.findByGuidForUpdate("laptop")).thenReturn(Optional.of(laptop));
        when(aparatRepository.findByGuidForUpdate("adaptor")).thenReturn(Optional.of(adaptor));
        when(personelRepository.findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc("laptop"))
            .thenReturn(Optional.empty());
        when(personelRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc("adaptor"))
            .thenReturn(Optional.empty());

        var laptopZimmeti = service.personeleZimmetle(talep(ZimmetVarlikTuru.ENVANTER, "laptop", "PERSONEL-A"));
        var adaptorZimmeti = service.personeleZimmetle(talep(ZimmetVarlikTuru.APARAT, "adaptor", "PERSONEL-B"));

        assertThat(laptopZimmeti.personelKodu()).isEqualTo("PERSONEL-A");
        assertThat(adaptorZimmeti.personelKodu()).isEqualTo("PERSONEL-B");
    }

    @Test
    void ayniAparatEszamanliIkinciAktifZimmeteVerilemez() {
        Aparat aparat = aparat("aparat-2");
        when(aparatRepository.findByGuidForUpdate("aparat-2")).thenReturn(Optional.of(aparat));
        when(personelRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc("aparat-2"))
            .thenReturn(Optional.of(aktifAparatZimmeti(aparat, "PERSONEL-A")));

        assertThatThrownBy(() -> service.personeleZimmetle(
            talep(ZimmetVarlikTuru.APARAT, "aparat-2", "PERSONEL-B")))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("zaten aktif");
    }

    @Test
    void aparatDepolarArasindaBagimsizTasinabilir() {
        Aparat aparat = aparat("aparat-depo");
        Depo eskiDepo = depo("depo-a");
        Depo yeniDepo = depo("depo-b");
        aparat.setDepo(eskiDepo);
        when(aparatRepository.findByGuidForUpdate("aparat-depo")).thenReturn(Optional.of(aparat));
        when(personelRepository.findFirstByAparatGuidAndAktifTrueOrderByIdDesc("aparat-depo"))
            .thenReturn(Optional.empty());
        when(depoRepository.findByGuid("depo-b")).thenReturn(Optional.of(yeniDepo));

        var yanit = service.depoyaTasi(new DepoTransferTalebi(
            ZimmetVarlikTuru.APARAT, "aparat-depo", "depo-b", null, "operator"));

        assertThat(yanit.hareketTipi()).isEqualTo(ZimmetHareketTipi.DEPO_TRANSFERI);
        assertThat(yanit.mevcutDepoGuid()).isEqualTo("depo-a");
        assertThat(yanit.gidecegiDepoGuid()).isEqualTo("depo-b");
        assertThat(aparat.getDepo()).isSameAs(yeniDepo);
    }

    private static ZimmetTalebi talep(ZimmetVarlikTuru tur, String guid, String personel) {
        return new ZimmetTalebi(tur, guid, personel, null, "operator");
    }

    private static Envanter envanter(String guid, EnvanterTuru tur) {
        Envanter envanter = new Envanter();
        envanter.setGuid(guid);
        envanter.setEnvanterTuru(tur);
        envanter.setEnvanterAdi(guid);
        return envanter;
    }

    private static Aparat aparat(String guid) {
        Aparat aparat = new Aparat();
        aparat.setGuid(guid);
        aparat.setAparatAdi(guid);
        return aparat;
    }

    private static Depo depo(String guid) {
        Depo depo = new Depo();
        depo.setGuid(guid);
        depo.setAd(guid);
        return depo;
    }

    private static PersonelZimmetHareket aktifEnvanterZimmeti(Envanter envanter, String personel) {
        PersonelZimmetHareket hareket = new PersonelZimmetHareket();
        hareket.setId(99L);
        hareket.setEnvanter(envanter);
        hareket.setPersonelKodu(personel);
        hareket.setAktif(true);
        return hareket;
    }

    private static PersonelZimmetHareket aktifAparatZimmeti(Aparat aparat, String personel) {
        PersonelZimmetHareket hareket = new PersonelZimmetHareket();
        hareket.setId(100L);
        hareket.setAparat(aparat);
        hareket.setPersonelKodu(personel);
        hareket.setAktif(true);
        return hareket;
    }
}
