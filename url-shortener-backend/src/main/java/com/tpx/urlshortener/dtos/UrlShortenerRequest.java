package com.tpx.urlshortener.dtos;

public record UrlShortenerRequest(
    String originalUrl,
    String customAlias
){}
