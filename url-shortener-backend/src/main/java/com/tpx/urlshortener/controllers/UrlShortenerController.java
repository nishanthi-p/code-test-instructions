package com.tpx.urlshortener.controllers;

import com.tpx.urlshortener.dtos.UrlShortenerRequest;
import com.tpx.urlshortener.dtos.UrlShortenerResponse;
import com.tpx.urlshortener.services.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService service;

    @PostMapping("/api/v1/shorten")
    public ResponseEntity<UrlShortenerResponse> shorten(@RequestBody UrlShortenerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.shorten(request));
    }

    @GetMapping("/api/v1/urls")
    public ResponseEntity<List<UrlShortenerResponse>> getAllUrls() {
        return ResponseEntity.ok(service.getAllUrls());
    }

    @DeleteMapping("/api/v1/{alias}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String alias) {
        service.deleteByAlias(alias);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        String originalUrl = service.getOriginalUrl(alias);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
