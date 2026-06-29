package com.stoktakip.entity;

import jakarta.persistence.Id;
import com.stoktakip.enums.EvetHayirDegeri;
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
@Table(name = "ENVANTER_BARKOD")
public class EnvanterBarkod {

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

    @Column(name = "BARKOD", length = 100, nullable = false)
    private String barkod;

    @Enumerated(EnumType.STRING)
    @Column(name = "SERI_MI", length = 16)
    private EvetHayirDegeri seriMi;

    public Envanter getEnvanter() {
        return envanter;
    }

    public void setEnvanter(Envanter envanter) {
        this.envanter = envanter;
    }

    public String getBarkod() {
        return barkod;
    }

    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    public EvetHayirDegeri getSeriMi() {
        return seriMi;
    }

    public void setSeriMi(EvetHayirDegeri seriMi) {
        this.seriMi = seriMi;
    }
}
