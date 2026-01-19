package com.tpx.urlshortener.services;

import com.tpx.urlshortener.dtos.UrlShortenerRequest;
import com.tpx.urlshortener.dtos.UrlShortenerResponse;
import com.tpx.urlshortener.entities.UrlShortenerEntity;
import com.tpx.urlshortener.exceptions.AliasAlreadyExistsException;
import com.tpx.urlshortener.exceptions.UrlNotFoundException;
import com.tpx.urlshortener.repositories.UrlShortenerRepository;
import com.tpx.urlshortener.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ALIAS_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlShortenerRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public UrlShortenerResponse shorten(UrlShortenerRequest request) {
        log.info("Shorten request received");

        String normalizedUrl = UrlUtils.normalizeAndValidate(request.originalUrl());
        log.debug("URL normalized successfully");

        String alias = request.customAlias();
        if (alias == null || alias.isBlank()) {
            alias = generateUniqueAlias();
            log.debug("Generated random alias: {}", alias);
        } else if (repository.existsByAlias(alias)) {
            log.warn("Alias already exists: {}", alias);
            throw new AliasAlreadyExistsException("Alias already exists: " + alias);
        } else {
            log.debug("Using custom alias: {}", alias);
        }

        UrlShortenerEntity entity = UrlShortenerEntity.builder()
                .alias(alias)
                .originalUrl(normalizedUrl)
                .build();

        UrlShortenerEntity saved = repository.save(entity);
        log.info("URL shortened successfully. alias={}, id={}", saved.getAlias(), saved.getId());

        return mapToResponse(saved);
    }

    public List<UrlShortenerResponse> getAllUrls() {
        log.debug("Fetching all shortened URLs");
        List<UrlShortenerResponse> urls = repository.findAll().stream()
                .map(this::mapToResponse)
                .toList();

        log.info("Fetched {} shortened URLs", urls.size());
        return urls;
    }

    public String getOriginalUrl(String alias) {
        log.debug("Resolving original URL for alias={}", alias);

        return repository.findByAlias(alias)
                .map(UrlShortenerEntity::getOriginalUrl)
                .orElseThrow(() -> {
                    log.warn("URL not found for alias={}", alias);
                    return new UrlNotFoundException("URL not found for alias: " + alias);
                });
    }

    @Transactional
    public void deleteByAlias(String alias) {
        log.info("Delete request for alias={}", alias);

        if (!repository.existsByAlias(alias)) {
            log.warn("Delete failed. URL not found for alias={}", alias);
            throw new UrlNotFoundException("URL not found for alias: " + alias);
        }

        repository.deleteByAlias(alias);
        log.info("URL deleted successfully for alias={}", alias);
    }

    private String generateUniqueAlias() {
        String alias;
        int attempts = 0;

        do {
            alias = generateRandomAlias();
            attempts++;
        } while (repository.existsByAlias(alias));

        log.debug("Generated unique alias={} after {} attempt(s)", alias, attempts);
        return alias;
    }

    private String generateRandomAlias() {
        StringBuilder sb = new StringBuilder(ALIAS_LENGTH);
        for (int i = 0; i < ALIAS_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private UrlShortenerResponse mapToResponse(UrlShortenerEntity entity) {
        log.debug("Mapping entity to response. alias={}", entity.getAlias());

        return new UrlShortenerResponse(
                entity.getId(),
                entity.getAlias(),
                baseUrl + "/" + entity.getAlias(),
                entity.getOriginalUrl(),
                entity.getCreatedAt()
        );
    }
}
