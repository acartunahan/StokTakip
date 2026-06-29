package com.stoktakip.migration.repository;

import com.stoktakip.migration.entity.MigrationValidationReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationValidationReportRepository extends JpaRepository<MigrationValidationReport, Long> {
}
