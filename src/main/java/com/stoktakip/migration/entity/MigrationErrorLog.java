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
@Table(name = "VERI_GOC_HATA_KAYDI")
public class MigrationErrorLog {

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

    @Column(name = "KOLON_ADI", length = 128)
    private String kolonAdi;

    @Column(name = "HATA_MESAJI", length = 1000, nullable = false)
    private String hataMesaji;

    @Lob
    @Column(name = "YIGIN_IZI")
    private String yiginIzi;

    @Column(name = "OLUSTURMA_TARIHI", nullable = false)
    private LocalDateTime olusturmaTarihi;

    public void setRunId(Long calismaId) {
        this.calismaId = calismaId;
    }

    public void setEskiId(Long eskiId) {
        this.eskiId = eskiId;
    }

    public void setEskiGuid(String eskiGuid) {
        this.eskiGuid = eskiGuid;
    }

    public void setEskiTabloAdi(String eskiTabloAdi) {
        this.eskiTabloAdi = eskiTabloAdi;
    }

    public void setColumnName(String kolonAdi) {
        this.kolonAdi = kolonAdi;
    }

    public void setErrorMessage(String hataMesaji) {
        this.hataMesaji = hataMesaji;
    }

    public void setStackTrace(String yiginIzi) {
        this.yiginIzi = yiginIzi;
    }

    public void setCreatedAt(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}
