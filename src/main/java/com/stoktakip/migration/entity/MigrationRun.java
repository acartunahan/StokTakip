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
@Table(name = "VERI_GOC_CALISMA")
public class MigrationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DRY_RUN", nullable = false)
    private boolean dryRun;

    @Column(name = "BASLAMA_TARIHI", nullable = false)
    private LocalDateTime baslamaTarihi;

    @Column(name = "TAMAMLANMA_TARIHI")
    private LocalDateTime tamamlanmaTarihi;

    @Column(name = "DURUM", length = 32, nullable = false)
    private String durum;

    @Lob
    @Column(name = "OZET_JSON")
    private String ozetJson;

    public Long getId() {
        return id;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public LocalDateTime getStartedAt() {
        return baslamaTarihi;
    }

    public void setStartedAt(LocalDateTime baslamaTarihi) {
        this.baslamaTarihi = baslamaTarihi;
    }

    public LocalDateTime getCompletedAt() {
        return tamamlanmaTarihi;
    }

    public void setCompletedAt(LocalDateTime tamamlanmaTarihi) {
        this.tamamlanmaTarihi = tamamlanmaTarihi;
    }

    public String getStatus() {
        return durum;
    }

    public void setStatus(String durum) {
        this.durum = durum;
    }

    public String getOzetJson() {
        return ozetJson;
    }

    public void setOzetJson(String ozetJson) {
        this.ozetJson = ozetJson;
    }
}
