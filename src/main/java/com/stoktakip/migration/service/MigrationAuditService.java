package com.stoktakip.migration.service;

import com.stoktakip.migration.core.LegacyPayloadJson;
import com.stoktakip.migration.core.LegacyRow;
import com.stoktakip.migration.core.MigrationContext;
import com.stoktakip.migration.entity.MigrationErrorLog;
import com.stoktakip.migration.entity.MigrationLegacyPayload;
import com.stoktakip.migration.entity.MigrationRun;
import com.stoktakip.migration.entity.MigrationValidationReport;
import com.stoktakip.migration.repository.MigrationErrorLogRepository;
import com.stoktakip.migration.repository.MigrationLegacyPayloadRepository;
import com.stoktakip.migration.repository.MigrationRunRepository;
import com.stoktakip.migration.repository.MigrationValidationReportRepository;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class MigrationAuditService {

    private final MigrationRunRepository migrationRunRepository;
    private final MigrationLegacyPayloadRepository payloadRepository;
    private final MigrationErrorLogRepository errorLogRepository;
    private final MigrationValidationReportRepository reportRepository;

    public MigrationAuditService(
        MigrationRunRepository migrationRunRepository,
        MigrationLegacyPayloadRepository payloadRepository,
        MigrationErrorLogRepository errorLogRepository,
        MigrationValidationReportRepository reportRepository
    ) {
        this.migrationRunRepository = migrationRunRepository;
        this.payloadRepository = payloadRepository;
        this.errorLogRepository = errorLogRepository;
        this.reportRepository = reportRepository;
    }

    public MigrationRun calismaBaslat(boolean dryRun) {
        MigrationRun calistir = new MigrationRun();
        calistir.setDryRun(dryRun);
        calistir.setStartedAt(LocalDateTime.now());
        calistir.setStatus("RUNNING");
        return dryRun ? calistir : migrationRunRepository.save(calistir);
    }

    public void calismaTamamla(MigrationRun calistir, String durum, String ozetJson) {
        calistir.setCompletedAt(LocalDateTime.now());
        calistir.setStatus(durum);
        calistir.setOzetJson(ozetJson);
        if (!calistir.isDryRun()) {
            migrationRunRepository.save(calistir);
        }
    }

    public void veriYuku(
        MigrationContext context,
        LegacyRow row,
        String hedefVarlikAdi,
        String hedefVarlikGuid,
        Set<String> mappedColumns
    ) {
        context.stats(row.tabloAdi()).veriYuku();
        if (context.dryRun()) {
            return;
        }
        MigrationLegacyPayload veriYuku = new MigrationLegacyPayload();
        veriYuku.setRunId(context.calismaId());
        veriYuku.setEskiId(row.id());
        veriYuku.setEskiGuid(row.guid());
        veriYuku.setEskiTabloAdi(row.tabloAdi());
        veriYuku.setHedefVarlikAdi(hedefVarlikAdi);
        veriYuku.setHedefVarlikGuid(hedefVarlikGuid);
        veriYuku.setVeriYukuJson(LegacyPayloadJson.object(row.values()));
        veriYuku.setEslenenKolonlarJson(LegacyPayloadJson.array(mappedColumns));
        veriYuku.setCreatedAt(LocalDateTime.now());
        payloadRepository.save(veriYuku);
    }

    public void hata(MigrationContext context, LegacyRow row, String kolonAdi, String message, Throwable throwable) {
        context.stats(row.tabloAdi()).hata();
        if (context.dryRun()) {
            return;
        }
        MigrationErrorLog errorLog = new MigrationErrorLog();
        errorLog.setRunId(context.calismaId());
        errorLog.setEskiId(row.id());
        errorLog.setEskiGuid(row.guid());
        errorLog.setEskiTabloAdi(row.tabloAdi());
        errorLog.setColumnName(kolonAdi);
        errorLog.setErrorMessage(message);
        errorLog.setStackTrace(yiginIzi(throwable));
        errorLog.setCreatedAt(LocalDateTime.now());
        errorLogRepository.save(errorLog);
    }

    public void raporla(MigrationContext context, String raporAdi, String raporJson) {
        if (context.dryRun()) {
            return;
        }
        MigrationValidationReport raporla = new MigrationValidationReport();
        raporla.setRunId(context.calismaId());
        raporla.setRaporAdi(raporAdi);
        raporla.setRaporJson(raporJson);
        raporla.setCreatedAt(LocalDateTime.now());
        reportRepository.save(raporla);
    }

    private static String yiginIzi(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter yazici = new StringWriter();
        throwable.printStackTrace(new PrintWriter(yazici));
        return yazici.toString();
    }
}
