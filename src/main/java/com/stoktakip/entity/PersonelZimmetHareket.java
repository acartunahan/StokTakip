package com.stoktakip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PERSONEL_ZIMMET_HAREKET")
public class PersonelZimmetHareket extends BaseEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENVANTER_GUID", referencedColumnName = "GUID")
    private Envanter envanter;

    @Column(name = "ZIMMET_TURU", length = 100)
    private String zimmetTuru;

    @Column(name = "ACIKLAMA", length = 1000)
    private String aciklama;

    @Column(name = "ISLEM_YAPAN_PERSONEL", length = 100)
    private String islemYapanPersonel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Envanter getEnvanter() {
        return envanter;
    }

    public void setEnvanter(Envanter envanter) {
        this.envanter = envanter;
    }

    public String getZimmetTuru() {
        return zimmetTuru;
    }

    public void setZimmetTuru(String zimmetTuru) {
        this.zimmetTuru = zimmetTuru;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getIslemYapanPersonel() {
        return islemYapanPersonel;
    }

    public void setIslemYapanPersonel(String islemYapanPersonel) {
        this.islemYapanPersonel = islemYapanPersonel;
    }
}
