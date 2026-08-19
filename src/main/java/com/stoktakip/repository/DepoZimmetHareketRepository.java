package com.stoktakip.repository;

import com.stoktakip.entity.DepoZimmetHareket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepoZimmetHareketRepository extends JpaRepository<DepoZimmetHareket, Long> {

    List<DepoZimmetHareket> findByEnvanterGuidOrderByIdDesc(String guid);

    List<DepoZimmetHareket> findByAparatGuidOrderByIdDesc(String guid);

    @Query(value = "SELECT DEPO_ZIMMET_HAREKET_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long nextId();
}
