package com.stoktakip.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ENVANTER_RESIM")
public class EnvanterResim {

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @Column(name = "AD", length = 100)
    private String ad;

    @Column(name = "RESIM_YOLU", length = 500)
    private String resimYolu;

    @Column(name = "ACIKLAMA", length = 500)
    private String aciklama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKAGUID", referencedColumnName = "GUID")
    private Marka marka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODELGUID", referencedColumnName = "GUID")
    private Model model;

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getResimYolu() {
        return resimYolu;
    }

    public void setResimYolu(String resimYolu) {
        this.resimYolu = resimYolu;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
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
}
