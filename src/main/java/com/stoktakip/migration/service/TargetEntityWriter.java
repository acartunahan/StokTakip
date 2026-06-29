package com.stoktakip.migration.service;

import com.stoktakip.migration.core.LegacyFlagMapper;
import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.MigrationContext;
import com.stoktakip.migration.entity.MigrationIdMap;
import com.stoktakip.migration.repository.MigrationIdMapRepository;
import com.stoktakip.repository.LegacyAwareRepository;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class TargetEntityWriter {

    private final MigrationIdMapRepository idMapRepository;
    private final MigrationAuditService denetimServisi;

    public TargetEntityWriter(MigrationIdMapRepository idMapRepository, MigrationAuditService denetimServisi) {
        this.idMapRepository = idMapRepository;
        this.denetimServisi = denetimServisi;
    }

    public <T> Optional<T> yaz(
        MigrationContext context,
        LegacyRow row,
        String hedefVarlikAdi,
        LegacyAwareRepository<T> veriErisim,
        Supplier<T> factory,
        Consumer<T> esleyici,
        Set<String> mappedColumns
    ) {
        String anahtar = row.anahtar();
        Optional<MigrationIdMap> existingMap = idMapRepository
            .findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(row.tabloAdi(), anahtar, hedefVarlikAdi);
        if (existingMap.isPresent()) {
            context.stats(row.tabloAdi()).tekrar();
            context.stats(row.tabloAdi()).atlandi();
            return Optional.empty();
        }

        T entity = varOlanKaydiBul(row, veriErisim).orElseGet(factory);
        kaynakAlanlariUygula(row, entity);
        esleyici.accept(entity);

        if (context.dryRun()) {
            context.stats(row.tabloAdi()).aktarildi();
            denetimServisi.veriYuku(context, row, hedefVarlikAdi, null, mappedColumns);
            return Optional.of(entity);
        }

        T saved = veriErisim.save(entity);
        String hedefVarlikGuid = stringAlanDegeri(saved, "guid");
        idEslemeOlustur(context, row, anahtar, hedefVarlikAdi, hedefVarlikGuid);
        denetimServisi.veriYuku(context, row, hedefVarlikAdi, hedefVarlikGuid, mappedColumns);
        context.stats(row.tabloAdi()).aktarildi();
        return Optional.of(saved);
    }

    private <T> Optional<T> varOlanKaydiBul(LegacyRow row, LegacyAwareRepository<T> veriErisim) {
        return veriErisim.findByGuid(entityGuid(row));
    }

    private static void kaynakAlanlariUygula(LegacyRow row, Object entity) {
        alanDegeriYaz(entity, "id", row.id());
        alanDegeriYaz(entity, "guid", entityGuid(row));
        alanDegeriYaz(entity, "olusturmaTarihi", row.dateTime("K_TARIH"));
        if (row.string("DURUM") != null) {
            alanDegeriYaz(entity, "durum", LegacyFlagMapper.durum(row.string("DURUM")));
        } else if (row.string("AKTIF") != null) {
            alanDegeriYaz(entity, "durum", LegacyFlagMapper.durum(row.string("AKTIF")));
        } else if (alanDegeri(entity, "durum") == null) {
            alanDegeriYaz(entity, "durum", "BILINMIYOR");
        }
    }

    private static String entityGuid(LegacyRow row) {
        String guid = row.guid();
        if (guid != null && !guid.isBlank()) {
            return guid;
        }
        return row.anahtar();
    }

    private static String stringAlanDegeri(Object entity, String alanAdi) {
        Object value = alanDegeri(entity, alanAdi);
        return value == null ? null : String.valueOf(value);
    }

    private static Object alanDegeri(Object entity, String alanAdi) {
        try {
            Field field = alan(entity, alanAdi);
            return field.get(entity);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani okunamadi: " + alanAdi, exception);
        }
    }

    private static void alanDegeriYaz(Object entity, String alanAdi, Object value) {
        try {
            Field field = alan(entity, alanAdi);
            field.set(entity, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani yazilamadi: " + alanAdi, exception);
        }
    }

    private static Field alan(Object entity, String alanAdi) {
        Class<?> type = entity.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(alanAdi);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new IllegalStateException("Entity alani bulunamadi: " + alanAdi);
    }

    private void idEslemeOlustur(
        MigrationContext context,
        LegacyRow row,
        String anahtar,
        String hedefVarlikAdi,
        String hedefVarlikGuid
    ) {
        MigrationIdMap idMap = new MigrationIdMap();
        idMap.setRunId(context.calismaId());
        idMap.setEskiId(row.id());
        idMap.setEskiGuid(row.guid());
        idMap.setEskiTabloAdi(row.tabloAdi());
        idMap.setEskiAnahtar(anahtar);
        idMap.setHedefVarlikAdi(hedefVarlikAdi);
        idMap.setHedefVarlikGuid(hedefVarlikGuid);
        idMap.setMigratedAt(LocalDateTime.now());
        idMapRepository.save(idMap);
    }
}
