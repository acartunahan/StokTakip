package com.stoktakip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "KATEGORI")
public class Kategori extends BaseEntity{

    @Id
    @Column(name = "GUID", length = 36, nullable = false)
    private String guid;

    @Column(name = "PARENT", length = 36)
    private String parent;

    @Column(name = "BASLIK", length = 100, nullable = false)
    private String baslik;

}
