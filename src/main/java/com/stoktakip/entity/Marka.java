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
public class Marka extends BaseEntity{

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @Column(name = "MARKA_ADI", length = 255, nullable = false)
    private String markaAdi;

    @Column(name = "ACIKLAMA", length = 500)
    private String aciklama;

}
