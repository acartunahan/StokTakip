package com.stoktakip.repository;

import com.stoktakip.entity.Aparat;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AparatRepository extends LegacyAwareRepository<Aparat> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aparat a where a.guid = :guid")
    Optional<Aparat> findByGuidForUpdate(@Param("guid") String guid);
}
