package com.stoktakip.entity;

import jakarta.persistence.Id;
import com.stoktakip.enums.HareketTuru;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ENVANTER_HAREKETI")
public class EnvanterHareketi {

    @Column(name = "ID")
    private Long id;

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @Column(name = "OLUSTURMA_TARIHI")
    private LocalDateTime olusturmaTarihi;

    @Column(name = "DURUM", length = 32)
    private String durum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "R_DEMIRBASGUID", referencedColumnName = "GUID")
    private Envanter envanter;

    @Enumerated(EnumType.STRING)
    @Column(name = "HAREKET_TURU", length = 32)
    private HareketTuru hareketTuru;

    @Column(name = "AD", length = 100)
    private String ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NERDEN", referencedColumnName = "GUID")
    private Depo neredenDepo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEREYE", referencedColumnName = "GUID")
    private Depo nereyeDepo;

    @Column(name = "PERSONEL_KODU", length = 50)
    private String personelKodu;

    @Lob
    @Column(name = "ACIKLAMA")
    private String aciklama;

    @Column(name = "HAREKET_NO")
    private Long hareketNo;

    public Envanter getEnvanter() {
        return envanter;
    }

    public void setEnvanter(Envanter envanter) {
        this.envanter = envanter;
    }

    public HareketTuru getHareketTuru() {
        return hareketTuru;
    }

    public void setHareketTuru(HareketTuru hareketTuru) {
        this.hareketTuru = hareketTuru;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public Depo getNeredenDepo() {
        return neredenDepo;
    }

    public void setNeredenDepo(Depo neredenDepo) {
        this.neredenDepo = neredenDepo;
    }

    public Depo getNereyeDepo() {
        return nereyeDepo;
    }

    public void setNereyeDepo(Depo nereyeDepo) {
        this.nereyeDepo = nereyeDepo;
    }

    public String getPersonelKodu() {
        return personelKodu;
    }

    public void setPersonelKodu(String personelKodu) {
        this.personelKodu = personelKodu;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public Long getHareketNo() {
        return hareketNo;
    }

    public void setHareketNo(Long hareketNo) {
        this.hareketNo = hareketNo;
    }
}
