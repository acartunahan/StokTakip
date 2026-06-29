package com.stoktakip.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LegacyAwareRepository<T> extends JpaRepository<T, String> {

    Optional<T> findByGuid(String guid);
}
