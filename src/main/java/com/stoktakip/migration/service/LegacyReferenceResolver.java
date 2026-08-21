package com.stoktakip.migration.service;

import com.stoktakip.entity.Marka;
import com.stoktakip.entity.Kategori;
import com.stoktakip.entity.Firma;
import com.stoktakip.entity.Depo;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.Model;
import com.stoktakip.entity.Sayim;
import com.stoktakip.entity.Ihale;
import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.MigrationContext;
import com.stoktakip.repository.MarkaRepository;
import com.stoktakip.repository.FirmaRepository;
import com.stoktakip.repository.DepoRepository;
import com.stoktakip.repository.EnvanterRepository;
import com.stoktakip.repository.LegacyAwareRepository;
import com.stoktakip.repository.ModelRepository;
import com.stoktakip.repository.KategoriRepository;
import com.stoktakip.repository.SayimRepository;
import com.stoktakip.repository.IhaleRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class LegacyReferenceResolver {

    private final DepoRepository depoRepository;
    private final MarkaRepository markaRepository;
    private final ModelRepository modelRepository;
    private final FirmaRepository firmaRepository;
    private final IhaleRepository ihaleRepository;
    private final KategoriRepository kategoriRepository;
    private final EnvanterRepository envanterRepository;
    private final SayimRepository sayimRepository;
    private final MigrationAuditService denetimServisi;

    public LegacyReferenceResolver(
        DepoRepository depoRepository,
        MarkaRepository markaRepository,
        ModelRepository modelRepository,
        FirmaRepository firmaRepository,
        IhaleRepository ihaleRepository,
        KategoriRepository kategoriRepository,
        EnvanterRepository envanterRepository,
        SayimRepository sayimRepository,
        MigrationAuditService denetimServisi
    ) {
        this.depoRepository = depoRepository;
        this.markaRepository = markaRepository;
        this.modelRepository = modelRepository;
        this.firmaRepository = firmaRepository;
        this.ihaleRepository = ihaleRepository;
        this.kategoriRepository = kategoriRepository;
        this.envanterRepository = envanterRepository;
        this.sayimRepository = sayimRepository;
        this.denetimServisi = denetimServisi;
    }

    public Depo depot(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_DEPO", row.string(column), depoRepository);
    }

    public Marka resatBrand(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_MARKA", row.string(column), markaRepository);
    }

    public Marka sarfBrand(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "SARF_MARKA", row.string(column), markaRepository);
    }

    public Model resatModel(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_MODEL", row.string(column), modelRepository);
    }

    public Model sarfModel(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "SARF_MODEL", row.string(column), modelRepository);
    }

    public Firma resatCompany(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_FIRMA", row.string(column), firmaRepository);
    }

    public Firma sarfCompany(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "SARF_FIRMA", row.string(column), firmaRepository);
    }

    public Ihale tender(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_IHALE", row.string(column), ihaleRepository);
    }

    public Kategori sabitKod(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "RESAT_SABIT_KOD", row.string(column), kategoriRepository);
    }

    public Kategori category(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "SARF_KATEGORI", row.string(column), kategoriRepository);
    }

    public Envanter inventory(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "R_DEMIRBAS", row.string(column), envanterRepository);
    }

    public Sayim stockCount(MigrationContext context, LegacyRow row, String column) {
        return coz(context, row, column, "R_SAYIM", row.string(column), sayimRepository);
    }

    private <T> T coz(
        MigrationContext context,
        LegacyRow row,
        String column,
        String hedefTabloAdi,
        String guid,
        LegacyAwareRepository<T> veriErisim
    ) {
        if (guid == null || guid.isBlank()) {
            context.stats(row.tabloAdi()).nullGuid();
            return null;
        }
        if (guid.length() < 8) {
            context.stats(row.tabloAdi()).bozukGuid();
            denetimServisi.hata(context, row, column, "Eski GUID bicimi bozuk: " + guid, null);
            return null;
        }
        return veriErisim.findByGuid(guid)
            .orElseGet(() -> {
                context.stats(row.tabloAdi()).eksikIliski();
                denetimServisi.hata(
                    context,
                    row,
                    column,
                    "Relation not found: " + hedefTabloAdi + ".GUID=" + guid,
                    null
                );
                return null;
            });
    }
}
