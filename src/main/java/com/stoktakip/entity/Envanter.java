package com.stoktakip.entity;

import jakarta.persistence.Id;
import com.stoktakip.enums.EnvanterTuru;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;


@Entity
@Table(name = "ENVANTER")
public class Envanter {

    @Column(name = "ID")
    private Long id;

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @Column(name = "OLUSTURMA_TARIHI")
    private LocalDateTime olusturmaTarihi;

    @Column(name = "DURUM", length = 32)
    private String durum;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENVANTER_TURU", length = 32, nullable = false)
    private EnvanterTuru envanterTuru;

    @Column(name = "AD", length = 255, nullable = false)
    private String ad;

    @Column(name = "SERI_NO", length = 100)
    private String seriNo;

    @Column(name = "BIRIM_KODU")
    private Long birimKodu;

    @Column(name = "ALIM_TARIHI")
    private LocalDateTime alimTarihi;

    @Column(name = "ACIKLAMA", length = 1000)
    private String aciklama;

    @Column(name = "ZIMMETLI_PERSONEL_KODU", length = 50)
    private String zimmetliPersonelKodu;

    @Column(name = "SUBE_NO", length = 100)
    private String subeNo;

    @Column(name = "ADET_METNI", length = 100)
    private String adetMetni;

    @Column(name = "SARF_TUR_METNI", length = 100)
    private String sarfTurMetni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESAT_SABIT_KODGUID", referencedColumnName = "GUID")
    private OrtakKategori sabitKod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPOGUID", referencedColumnName = "GUID")
    private Depo depo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKAGUID", referencedColumnName = "GUID")
    private Marka marka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODELGUID", referencedColumnName = "GUID")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADIR_TUKETIMCINSLERIGUID", referencedColumnName = "GUID")
    private TuketimCinsi tuketimCinsi;

    @OneToOne(mappedBy = "envanter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private EnvanterDetay detay;

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

    public EnvanterTuru getEnvanterTuru() {
        return envanterTuru;
    }

    public void setEnvanterTuru(EnvanterTuru envanterTuru) {
        this.envanterTuru = envanterTuru;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSeriNo() {
        return seriNo;
    }

    public void setSeriNo(String seriNo) {
        this.seriNo = seriNo;
    }

    public Long getBirimKodu() {
        return birimKodu;
    }

    public void setBirimKodu(Long birimKodu) {
        this.birimKodu = birimKodu;
    }

    public LocalDateTime getAlimTarihi() {
        return alimTarihi;
    }

    public void setAlimTarihi(LocalDateTime alimTarihi) {
        this.alimTarihi = alimTarihi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getZimmetliPersonelKodu() {
        return zimmetliPersonelKodu;
    }

    public void setZimmetliPersonelKodu(String zimmetliPersonelKodu) {
        this.zimmetliPersonelKodu = zimmetliPersonelKodu;
    }

    public String getSubeNo() {
        return subeNo;
    }

    public void setSubeNo(String subeNo) {
        this.subeNo = subeNo;
    }

    public String getAdetMetni() {
        return adetMetni;
    }

    public void setAdetMetni(String adetMetni) {
        this.adetMetni = adetMetni;
    }

    public String getSarfTurMetni() {
        return sarfTurMetni;
    }

    public void setSarfTurMetni(String sarfTurMetni) {
        this.sarfTurMetni = sarfTurMetni;
    }

    public OrtakKategori getSabitKod() {
        return sabitKod;
    }

    public void setSabitKod(OrtakKategori sabitKod) {
        this.sabitKod = sabitKod;
    }

    public Depo getDepo() {
        return depo;
    }

    public void setDepo(Depo depo) {
        this.depo = depo;
    }

    public Marka getMarka() {
        return marka;
    }

    public void setMarka(Marka marka) {
        this.marka = marka;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public TuketimCinsi getTuketimCinsi() {
        return tuketimCinsi;
    }

    public void setTuketimCinsi(TuketimCinsi tuketimCinsi) {
        this.tuketimCinsi = tuketimCinsi;
    }

    public EnvanterDetay getDetay() {
        return detay;
    }

    public void setDetay(EnvanterDetay detay) {
        this.detay = detay;
        if (detay != null && detay.getEnvanter() != this) {
            detay.setEnvanter(this);
        }
    }

    public EnvanterDetay detayOlustur() {
        if (detay == null) {
            setDetay(new EnvanterDetay());
        }
        return detay;
    }
}
