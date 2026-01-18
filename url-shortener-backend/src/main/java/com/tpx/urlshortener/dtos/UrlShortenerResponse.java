package com.tpx.urlshortener.dtos;

import java.time.LocalDateTime;

public record UrlShortenerResponse(
        Long id,
        String alias,
        String shortUrl,
        String originalUrl,
        LocalDateTime createdAt
) {}

