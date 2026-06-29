package com.stoktakip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORTAK_KATEGORI")
public class OrtakKategori {

    @Column(name = "ID", insertable = false, updatable = false)
    private Long id;

    @Id
    @Column(name = "GUID", length = 36)
    private String guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UST_KATEGORI_GUID", referencedColumnName = "GUID")
    private OrtakKategori ustKategori;

    @Column(name = "KATEGORI_TURU", length = 20, nullable = false)
    private String kategoriTuru;

    @Column(name = "ADI", length = 100, nullable = false)
    private String ad;

    @Column(name = "ACIKLAMA", length = 200)
    private String aciklama;

    @Column(name = "R_DEPOGUID", length = 36)
    private String rDepoGuid;

    @Column(name = "DURUM", length = 1)
    private String durum;

    @Column(name = "K_TARIH")
    private LocalDateTime olusturmaTarihi;

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

    public OrtakKategori getUstKategori() {
        return ustKategori;
    }

    public void setUstKategori(OrtakKategori ustKategori) {
        this.ustKategori = ustKategori;
    }

    public String getKategoriTuru() {
        return kategoriTuru;
    }

    public void setKategoriTuru(String kategoriTuru) {
        this.kategoriTuru = kategoriTuru;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getRDepoGuid() {
        return rDepoGuid;
    }

    public void setRDepoGuid(String rDepoGuid) {
        this.rDepoGuid = rDepoGuid;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}
