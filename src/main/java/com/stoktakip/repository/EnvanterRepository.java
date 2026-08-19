package com.stoktakip.repository;

import com.stoktakip.entity.Envanter;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvanterRepository extends LegacyAwareRepository<Envanter> {

    long countByBarkodIsNotNull();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Envanter e where e.guid = :guid")
    Optional<Envanter> findByGuidForUpdate(@Param("guid") String guid);
}
