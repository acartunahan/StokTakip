package com.stoktakip.migration.repository;

import com.stoktakip.migration.entity.MigrationLegacyPayload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationLegacyPayloadRepository extends JpaRepository<MigrationLegacyPayload, Long> {
}
