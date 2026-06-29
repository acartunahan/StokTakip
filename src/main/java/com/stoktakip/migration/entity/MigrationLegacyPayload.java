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
@Table(name = "VERI_GOC_ESKI_VERI_YUKU")
public class MigrationLegacyPayload {

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

    @Column(name = "HEDEF_VARLIK_ADI", length = 128, nullable = false)
    private String hedefVarlikAdi;

    @Column(name = "HEDEF_VARLIK_GUID", length = 128)
    private String hedefVarlikGuid;

    @Lob
    @Column(name = "VERI_YUKU_JSON", nullable = false)
    private String veriYukuJson;

    @Lob
    @Column(name = "ESLENEN_KOLONLAR_JSON")
    private String eslenenKolonlarJson;

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

    public void setHedefVarlikAdi(String hedefVarlikAdi) {
        this.hedefVarlikAdi = hedefVarlikAdi;
    }

    public void setHedefVarlikGuid(String hedefVarlikGuid) {
        this.hedefVarlikGuid = hedefVarlikGuid;
    }

    public void setVeriYukuJson(String veriYukuJson) {
        this.veriYukuJson = veriYukuJson;
    }

    public void setEslenenKolonlarJson(String eslenenKolonlarJson) {
        this.eslenenKolonlarJson = eslenenKolonlarJson;
    }

    public void setCreatedAt(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}
