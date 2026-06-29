package com.stoktakip.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "VERI_GOC_ID_ESLEME",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_MIG_ID_MAP_TARGET_KEY",
            columnNames = {"ESKI_TABLO_ADI", "ESKI_ANAHTAR", "HEDEF_VARLIK_ADI"}
        )
    }
)
public class MigrationIdMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CALISMA_ID")
    private Long calismaId;

    @Column(name = "ESKI_ID")
    private Long eskiId;

    @Column(name = "ESKI_GUID", length = 128)
    private String eskiGuid;

    @Column(name = "ESKI_TABLO_ADI", length = 128, nullable = false)
    private String eskiTabloAdi;

    @Column(name = "ESKI_ANAHTAR", length = 256, nullable = false)
    private String eskiAnahtar;

    @Column(name = "HEDEF_VARLIK_ADI", length = 128, nullable = false)
    private String hedefVarlikAdi;

    @Column(name = "HEDEF_VARLIK_GUID", length = 128)
    private String hedefVarlikGuid;

    @Column(name = "AKTARIM_TARIHI", nullable = false)
    private LocalDateTime aktarimTarihi;

    public Long getId() {
        return id;
    }

    public Long getRunId() {
        return calismaId;
    }

    public void setRunId(Long calismaId) {
        this.calismaId = calismaId;
    }

    public Long getEskiId() {
        return eskiId;
    }

    public void setEskiId(Long eskiId) {
        this.eskiId = eskiId;
    }

    public String getEskiGuid() {
        return eskiGuid;
    }

    public void setEskiGuid(String eskiGuid) {
        this.eskiGuid = eskiGuid;
    }

    public String getEskiTabloAdi() {
        return eskiTabloAdi;
    }

    public void setEskiTabloAdi(String eskiTabloAdi) {
        this.eskiTabloAdi = eskiTabloAdi;
    }

    public String getEskiAnahtar() {
        return eskiAnahtar;
    }

    public void setEskiAnahtar(String eskiAnahtar) {
        this.eskiAnahtar = eskiAnahtar;
    }

    public String getHedefVarlikAdi() {
        return hedefVarlikAdi;
    }

    public void setHedefVarlikAdi(String hedefVarlikAdi) {
        this.hedefVarlikAdi = hedefVarlikAdi;
    }

    public String getHedefVarlikGuid() {
        return hedefVarlikGuid;
    }

    public void setHedefVarlikGuid(String hedefVarlikGuid) {
        this.hedefVarlikGuid = hedefVarlikGuid;
    }

    public LocalDateTime getMigratedAt() {
        return aktarimTarihi;
    }

    public void setMigratedAt(LocalDateTime aktarimTarihi) {
        this.aktarimTarihi = aktarimTarihi;
    }
}
