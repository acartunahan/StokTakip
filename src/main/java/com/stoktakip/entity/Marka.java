package com.stoktakip.entity;

import jakarta.persistence.Id;
import com.stoktakip.enums.KaynakAlani;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "MARKA")
public class Marka {

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

    @Column(name = "AD", length = 255, nullable = false)
    private String ad;

    @Column(name = "ACIKLAMA", length = 500)
    private String aciklama;

    @Enumerated(EnumType.STRING)
    @Column(name = "KAYNAK_ALANI", length = 32, nullable = false)
    private KaynakAlani kaynakAlani;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESAT_DEPOGUID", referencedColumnName = "GUID")
    private Depo depo;

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

    public KaynakAlani getKaynakAlani() {
        return kaynakAlani;
    }

    public void setKaynakAlani(KaynakAlani kaynakAlani) {
        this.kaynakAlani = kaynakAlani;
    }

    public Depo getDepo() {
        return depo;
    }

    public void setDepo(Depo depo) {
        this.depo = depo;
    }
}
