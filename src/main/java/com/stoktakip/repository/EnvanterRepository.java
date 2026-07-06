package com.stoktakip.repository;

import com.stoktakip.entity.Envanter;

public interface EnvanterRepository extends LegacyAwareRepository<Envanter> {

    long countByBarkodIsNotNull();
}
