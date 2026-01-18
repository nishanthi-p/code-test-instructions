package com.tpx.urlshortener.services;

import com.tpx.urlshortener.dtos.UrlShortenerRequest;
import com.tpx.urlshortener.dtos.UrlShortenerResponse;
import com.tpx.urlshortener.entities.UrlShortenerEntity;
import com.tpx.urlshortener.exceptions.AliasAlreadyExistsException;
import com.tpx.urlshortener.exceptions.UrlNotFoundException;
import com.tpx.urlshortener.repositories.UrlShortenerRepository;
import com.tpx.urlshortener.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

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
        String normalizedUrl = UrlUtils.normalizeAndValidate(request.originalUrl());

        String alias = request.customAlias();
        if (alias == null || alias.isBlank()) {
            alias = generateUniqueAlias();
        } else if (repository.existsByAlias(alias)) {
            throw new AliasAlreadyExistsException("Alias already exists: " + alias);
        }

        UrlShortenerEntity entity = UrlShortenerEntity.builder()
                .alias(alias)
                .originalUrl(normalizedUrl)
                .build();

        UrlShortenerEntity saved = repository.save(entity);
        return mapToResponse(saved);
    }

    public List<UrlShortenerResponse> getAllUrls() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public String getOriginalUrl(String alias) {
        return repository.findByAlias(alias)
                .map(UrlShortenerEntity::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for alias: " + alias));
    }

    @Transactional
    public void deleteByAlias(String alias) {
        if (!repository.existsByAlias(alias)) {
            throw new UrlNotFoundException("URL not found for alias: " + alias);
        }
        repository.deleteByAlias(alias);
    }

    private String generateUniqueAlias() {
        String alias;
        do {
            alias = generateRandomAlias();
        } while (repository.existsByAlias(alias));
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
        return new UrlShortenerResponse(
                entity.getId(),
                entity.getAlias(),
                baseUrl + "/" + entity.getAlias(),
                entity.getOriginalUrl(),
                entity.getCreatedAt()
        );
    }
}
