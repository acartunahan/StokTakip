package com.stoktakip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "KATEGORI")
public class Kategori extends BaseEntity{

    @Id
    @Column(name = "GUID", length = 128, nullable = false)
    private String guid;

    @Column(name = "PARENT", length = 128)
    private String parent;

    @Column(name = "BASLIK", length = 120, nullable = false)
    private String baslik;

    @OneToMany(mappedBy = "kategori", fetch = FetchType.LAZY)
    private List<Model> modeller = new ArrayList<>();

}
