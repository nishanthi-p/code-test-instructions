package com.tpx.urlshortener.repositories;

import com.tpx.urlshortener.entities.UrlShortenerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlShortenerEntity, Long> {

    Optional<UrlShortenerEntity> findByAlias(String alias);

    boolean existsByAlias(String alias);

    void deleteByAlias(String alias);
}
