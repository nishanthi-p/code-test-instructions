package com.tpx.urlshortener.services;

import com.tpx.urlshortener.dtos.UrlShortenerRequest;
import com.tpx.urlshortener.dtos.UrlShortenerResponse;
import com.tpx.urlshortener.entities.UrlShortenerEntity;
import com.tpx.urlshortener.exceptions.AliasAlreadyExistsException;
import com.tpx.urlshortener.exceptions.UrlNotFoundException;
import com.tpx.urlshortener.repositories.UrlShortenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlShortenerRepository repository;

    @InjectMocks
    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");
    }

    @Test
    @DisplayName("Should shorten URL with auto-generated alias")
    void shouldShortenUrlWithGeneratedAlias() {
        UrlShortenerRequest request =
                new UrlShortenerRequest("https://google.com", null);

        when(repository.existsByAlias(anyString())).thenReturn(false);

        UrlShortenerEntity savedEntity = UrlShortenerEntity.builder()
                .id(1L)
                .alias("abc123")
                .originalUrl("https://google.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.save(any())).thenReturn(savedEntity);

        UrlShortenerResponse response = service.shorten(request);

        assertNotNull(response);
        assertEquals("abc123", response.alias());
        assertEquals("https://google.com", response.originalUrl());
        assertEquals("http://localhost:8080/abc123", response.shortUrl());

        verify(repository).save(any(UrlShortenerEntity.class));
    }

    @Test
    @DisplayName("Should shorten URL with custom alias")
    void shouldShortenUrlWithCustomAlias() {
        UrlShortenerRequest request =
                new UrlShortenerRequest("https://example.com", "custom123");

        when(repository.existsByAlias("custom123")).thenReturn(false);

        UrlShortenerEntity savedEntity = UrlShortenerEntity.builder()
                .id(2L)
                .alias("custom123")
                .originalUrl("https://example.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.save(any())).thenReturn(savedEntity);

        UrlShortenerResponse response = service.shorten(request);

        assertEquals("custom123", response.alias());
        assertEquals("http://localhost:8080/custom123", response.shortUrl());
    }

    @Test
    @DisplayName("Should throw exception when custom alias already exists")
    void shouldThrowWhenAliasAlreadyExists() {
        UrlShortenerRequest request =
                new UrlShortenerRequest("https://example.com", "existing-alias");

        when(repository.existsByAlias("existing-alias")).thenReturn(true);

        AliasAlreadyExistsException exception = assertThrows(
                AliasAlreadyExistsException.class,
                () -> service.shorten(request)
        );

        assertTrue(exception.getMessage().contains("Alias already exists"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all shortened URLs")
    void shouldGetAllUrls() {
        UrlShortenerEntity entity1 = UrlShortenerEntity.builder()
                .id(1L)
                .alias("alias1")
                .originalUrl("https://original_url1.com")
                .createdAt(LocalDateTime.now())
                .build();

        UrlShortenerEntity entity2 = UrlShortenerEntity.builder()
                .id(2L)
                .alias("alias2")
                .originalUrl("https://original_url2.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        List<UrlShortenerResponse> responses = service.getAllUrls();

        assertEquals(2, responses.size());
        assertEquals("alias1", responses.get(0).alias());
        assertEquals("alias2", responses.get(1).alias());
    }

    @Test
    @DisplayName("Should return original URL by alias")
    void shouldReturnOriginalUrlByAlias() {
        when(repository.findByAlias("validAlias"))
                .thenReturn(Optional.of(
                        UrlShortenerEntity.builder()
                                .originalUrl("https://google.com")
                                .build()
                ));

        String result = service.getOriginalUrl("validAlias");

        assertEquals("https://google.com", result);
    }

    @Test
    @DisplayName("Should throw exception when alias not found")
    void shouldThrowWhenAliasNotFound() {
        when(repository.findByAlias("missingAlias")).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> service.getOriginalUrl("missingAlias")
        );

        assertTrue(exception.getMessage().contains("URL not found"));
    }

    @Test
    @DisplayName("Should delete URL by alias")
    void shouldDeleteByAlias() {
        when(repository.existsByAlias("aliasToDelete")).thenReturn(true);

        service.deleteByAlias("aliasToDelete");

        verify(repository).deleteByAlias("aliasToDelete");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing alias")
    void shouldThrowWhenDeletingNonExistingAlias() {
        when(repository.existsByAlias("missingAlias")).thenReturn(false);

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> service.deleteByAlias("missingAlias")
        );

        assertTrue(exception.getMessage().contains("URL not found"));
        verify(repository, never()).deleteByAlias(any());
    }
}
