package com.stoktakip.migration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "VERI_GOC_DOGRULAMA_RAPORU")
public class MigrationValidationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CALISMA_ID")
    private Long calismaId;

    @Column(name = "RAPOR_ADI", length = 128, nullable = false)
    private String raporAdi;

    @Lob
    @Column(name = "RAPOR_JSON", nullable = false)
    private String raporJson;

    @Column(name = "OLUSTURMA_TARIHI", nullable = false)
    private LocalDateTime olusturmaTarihi;

    public void setRunId(Long calismaId) {
        this.calismaId = calismaId;
    }

    public void setRaporAdi(String raporAdi) {
        this.raporAdi = raporAdi;
    }

    public void setRaporJson(String raporJson) {
        this.raporJson = raporJson;
    }

    public void setCreatedAt(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}
