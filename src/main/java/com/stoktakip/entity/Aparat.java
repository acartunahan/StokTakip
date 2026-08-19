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
@Table(name = "APARAT")
public class Aparat extends BaseEntity {

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @Column(name = "APARAT_ADI", length = 100, nullable = false)
    private String aparatAdi;

    @Column(name = "ACIKLAMA", length = 500)
    private String aciklama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKAGUID", referencedColumnName = "GUID")
    private Marka marka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODELGUID", referencedColumnName = "GUID")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANA_ENVANTER_GUID", referencedColumnName = "GUID")
    private Envanter anaEnvanter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPO_GUID", referencedColumnName = "GUID")
    private Depo depo;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAparatAdi() {
        return aparatAdi;
    }

    public void setAparatAdi(String aparatAdi) {
        this.aparatAdi = aparatAdi;
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

    public Envanter getAnaEnvanter() {
        return anaEnvanter;
    }

    public void setAnaEnvanter(Envanter anaEnvanter) {
        this.anaEnvanter = anaEnvanter;
    }

    public Depo getDepo() {
        return depo;
    }

    public void setDepo(Depo depo) {
        this.depo = depo;
    }

}
