package com.stoktakip.repository;

import com.stoktakip.entity.PersonelZimmetHareket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonelZimmetHareketRepository extends JpaRepository<PersonelZimmetHareket, Long> {

    Optional<PersonelZimmetHareket> findFirstByEnvanterGuidAndAktifTrueOrderByIdDesc(String guid);

    List<PersonelZimmetHareket> findByEnvanterGuidOrderByIdDesc(String guid);

    @Query(value = "SELECT PERSONEL_ZIMMET_HAREKET_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long nextId();
}
