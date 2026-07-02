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
@Table(name = "MODEL")
public class Model extends BaseEntity{

    @Id
    @Column(name = "GUID", length = 128)
    private String guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKA_GUID", referencedColumnName = "GUID")
    private Marka marka;

    @Column(name = "MODEL_ADI", length = 255, nullable = false)
    private String modelAdi;

    @Column(name = "ACIKLAMA", length = 500)
    private String aciklama;

    @ManyToOne
    @JoinColumn(name = "KATEGORI_GUID", referencedColumnName = "GUID")
    private Kategori kategori;

}
