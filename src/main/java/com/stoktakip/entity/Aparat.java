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

}
