package com.stoktakip.migration.repository;

import com.stoktakip.migration.entity.MigrationIdMap;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationIdMapRepository extends JpaRepository<MigrationIdMap, Long> {

    Optional<MigrationIdMap> findByEskiTabloAdiAndEskiAnahtarAndHedefVarlikAdi(
        String eskiTabloAdi,
        String eskiAnahtar,
        String hedefVarlikAdi
    );
}
