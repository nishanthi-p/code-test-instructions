package com.tpx.urlshortener.controllers;

import com.tpx.urlshortener.dtos.UrlShortenerRequest;
import com.tpx.urlshortener.dtos.UrlShortenerResponse;
import com.tpx.urlshortener.services.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {

    private final UrlShortenerService service;

    @PostMapping("/api/v1/shorten")
    public ResponseEntity<UrlShortenerResponse> shorten(@RequestBody UrlShortenerRequest request) {
        log.info("Request to shorten URL: {}", request.originalUrl());
        UrlShortenerResponse response = service.shorten(request);
        log.info("URL shortened successfully. Alias: {}", response.alias());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/urls")
    public ResponseEntity<List<UrlShortenerResponse>> getAllUrls() {
        log.debug("Fetching all shortened URLs");
        return ResponseEntity.ok(service.getAllUrls());
    }

    @DeleteMapping("/api/v1/{alias}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String alias) {
        log.info("Deleting URL with alias: {}", alias);
        service.deleteByAlias(alias);
        log.info("URL deleted successfully for alias: {}", alias);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        log.debug("Redirect request received for alias: {}", alias);
        String originalUrl = service.getOriginalUrl(alias);
        log.info("Redirecting alias '{}' to '{}'", alias, originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
