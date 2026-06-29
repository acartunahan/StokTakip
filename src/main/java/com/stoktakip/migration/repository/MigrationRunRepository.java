package com.stoktakip.migration.repository;

import com.stoktakip.migration.entity.MigrationRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationRunRepository extends JpaRepository<MigrationRun, Long> {
}
