package com.stoktakip.migration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stoktakip.entity.Depo;
import com.stoktakip.entity.DepoZimmetHareket;
import com.stoktakip.entity.Envanter;
import com.stoktakip.entity.EnvanterDetay;
import com.stoktakip.entity.Firma;
import com.stoktakip.entity.Model;
import com.stoktakip.enums.EnvanterTuru;
import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.LegacyTableSpec;
import com.stoktakip.migration.core.MigrationContext;
import com.stoktakip.migration.entity.MigrationIdMap;
import com.stoktakip.migration.repository.MigrationIdMapRepository;
import com.stoktakip.repository.DepoZimmetHareketRepository;
import com.stoktakip.repository.EnvanterRepository;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class LegacyTuketimMigrationServiceTest {

    @Mock TargetEntityWriter yazici;
    @Mock LegacyReferenceResolver cozucu;
    @Mock MigrationAuditService denetimServisi;
    @Mock TransactionTemplate transactionTemplate;
    @Mock MigrationIdMapRepository idMapRepository;
    @Mock EnvanterRepository envanterRepository;
    @Mock DepoZimmetHareketRepository depoZimmetHareketRepository;

    @InjectMocks LegacyMigrationService service;

    @Test
    void tuketimCinsiSarfEnvanterineDonusturulur() throws Exception {
        MigrationContext context = new MigrationContext(1L, true);
        LegacyRow row = row(LegacyTableSpec.R_TUKETIMCINSLERI, Map.of(
            "ID", 10L,
            "GUID", "TUK-00000001",
            "ADI", "Toner",
            "MODEL", "MODEL-000001",
            "FIRMA", "FIRMA-000001",
            "SERINO", "SERI-123",
            "ALANDEPO", "DEPO-0000001",
            "ACIKLAMA", "HP Toner"
        ));
        Model model = new Model();
        Firma firma = new Firma();
        Depo depo = new Depo();
        depo.setGuid("DEPO-0000001");
        when(cozucu.sarfModel(context, row, "MODEL")).thenReturn(model);
        when(cozucu.sarfCompany(context, row, "FIRMA")).thenReturn(firma);
        when(cozucu.depot(context, row, "ALANDEPO")).thenReturn(depo);

        Envanter envanter = new Envanter();
        doAnswer(invocation -> {
            envanter.setGuid(row.guid());
            @SuppressWarnings("unchecked")
            Consumer<Envanter> mapper = invocation.getArgument(5);
            mapper.accept(envanter);
            return Optional.of(envanter);
        }).when(yazici).yaz(
            eq(context),
            eq(row),
            eq("Envanter"),
            eq(envanterRepository),
            any(),
            any(),
            anySet()
        );

        invoke("tuketimCinsiniEnvantereAktar", context, row);

        assertThat(envanter.getEnvanterTuru()).isEqualTo(EnvanterTuru.SARF);
        assertThat(envanter.getEnvanterAdi()).isEqualTo("Toner");
        assertThat(alanOku(envanter, "seriNo")).isEqualTo("SERI-123");
        assertThat(alanOku(envanter, "aciklama")).isEqualTo("HP Toner");
        assertThat(alanOku(envanter, "model")).isSameAs(model);
        assertThat(envanter.getDepo()).isSameAs(depo);
        EnvanterDetay detay = (EnvanterDetay) alanOku(envanter, "detay");
        assertThat(alanOku(detay, "firma")).isSameAs(firma);
    }

    @Test
    void sarfSatiriNegatifAdediyleAyriDepoHareketiOlur() throws Exception {
        MigrationContext context = new MigrationContext(2L, false);
        LegacyRow row = row(LegacyTableSpec.R_SARFDEMIRBAS, Map.of(
            "ID", 20L,
            "GUID", "HRK-00000001",
            "ADI", "TUK-00000001",
            "ADET", "-1",
            "TUR", "giden",
            "ACIKLAMA", "Toner cikisi",
            "DURUM", "1",
            "K_TARIH", Date.valueOf("2026-01-10")
        ));
        Depo depo = new Depo();
        depo.setGuid("DEPO-0000001");
        Envanter envanter = new Envanter();
        envanter.setGuid("TUK-00000001");
        envanter.setDepo(depo);
        when(idMapRepository.findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(
            "R_SARFDEMIRBAS", "GUID:HRK-00000001", "DepoZimmetHareket"
        )).thenReturn(Optional.empty());
        when(cozucu.sarfInventory(context, row, "ADI")).thenReturn(envanter);
        when(depoZimmetHareketRepository.nextId()).thenReturn(1_000_000_000_100L);
        when(depoZimmetHareketRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(idMapRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        invoke("sarfHareketiniAktar", context, row);

        ArgumentCaptor<DepoZimmetHareket> captor = ArgumentCaptor.forClass(DepoZimmetHareket.class);
        verify(depoZimmetHareketRepository).save(captor.capture());
        DepoZimmetHareket hareket = captor.getValue();
        assertThat(hareket.getId()).isEqualTo(1_000_000_000_100L);
        assertThat(hareket.getEnvanter()).isSameAs(envanter);
        assertThat(hareket.getAdet()).isEqualTo(-1L);
        assertThat(hareket.getTur()).isEqualTo("giden");
        assertThat(hareket.getMevcutDepo()).isSameAs(depo);
        assertThat(hareket.getGidecegiDepo()).isNull();
        assertThat(hareket.getAciklama()).isEqualTo("Toner cikisi");
        ArgumentCaptor<MigrationIdMap> idMapCaptor = ArgumentCaptor.forClass(MigrationIdMap.class);
        verify(idMapRepository).save(idMapCaptor.capture());
        assertThat(idMapCaptor.getValue().getEskiGuid()).isEqualTo("HRK-00000001");
        assertThat(idMapCaptor.getValue().getHedefVarlikGuid()).isEqualTo("1000000000100");
    }

    @Test
    void dahaOnceAktarilanSarfHareketiTekrarOlusturulmaz() throws Exception {
        MigrationContext context = new MigrationContext(3L, false);
        LegacyRow row = row(LegacyTableSpec.R_SARFDEMIRBAS, Map.of(
            "ID", 21L,
            "GUID", "HRK-00000002",
            "ADI", "TUK-00000001",
            "ADET", "1",
            "TUR", "gelen"
        ));
        when(idMapRepository.findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(
            "R_SARFDEMIRBAS", "GUID:HRK-00000002", "DepoZimmetHareket"
        )).thenReturn(Optional.of(new MigrationIdMap()));

        invoke("sarfHareketiniAktar", context, row);

        verify(cozucu, never()).sarfInventory(any(), any(), any());
        verify(depoZimmetHareketRepository, never()).save(any());
    }

    private void invoke(String methodName, MigrationContext context, LegacyRow row) throws Exception {
        Method method = LegacyMigrationService.class
            .getDeclaredMethod(methodName, MigrationContext.class, LegacyRow.class);
        method.setAccessible(true);
        method.invoke(service, context, row);
    }

    private static LegacyRow row(LegacyTableSpec spec, Map<String, Object> values) {
        return new LegacyRow(spec, new LinkedHashMap<>(values));
    }

    private static Object alanOku(Object target, String fieldName) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
