package com.stoktakip.migration.repository;

import com.stoktakip.migration.entity.MigrationErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationErrorLogRepository extends JpaRepository<MigrationErrorLog, Long> {
}
