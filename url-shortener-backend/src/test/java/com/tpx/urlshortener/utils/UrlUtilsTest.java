package com.tpx.urlshortener.utils;

import com.tpx.urlshortener.exceptions.InvalidUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlUtilsTest {

    @Test
    @DisplayName("Should accept valid HTTPS URL as-is")
    void shouldAcceptValidHttpsUrl() {
        String url = "https://www.google.com";

        String result = UrlUtils.normalizeAndValidate(url);

        assertEquals("https://www.google.com", result);
    }

    @Test
    @DisplayName("Should accept valid HTTP URL as-is")
    void shouldAcceptValidHttpUrl() {
        String url = "http://example.co.uk";

        String result = UrlUtils.normalizeAndValidate(url);

        assertEquals("http://example.co.uk", result);
    }

    @Test
    @DisplayName("Should prepend https when scheme is missing")
    void shouldPrependHttpsWhenMissingScheme() {
        String url = "www.google.com";

        String result = UrlUtils.normalizeAndValidate(url);

        assertEquals("https://www.google.com", result);
    }

    @Test
    @DisplayName("Should throw exception when URL is null")
    void shouldThrowWhenUrlIsNull() {
        InvalidUrlException exception = assertThrows(
                InvalidUrlException.class,
                () -> UrlUtils.normalizeAndValidate(null)
        );

        assertEquals("URL cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when URL is blank")
    void shouldThrowWhenUrlIsBlank() {
        InvalidUrlException exception = assertThrows(
                InvalidUrlException.class,
                () -> UrlUtils.normalizeAndValidate("   ")
        );

        assertEquals("URL cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when URL exceeds max length")
    void shouldThrowWhenUrlExceedsMaxLength() {
        String longUrl = "https://" + "a".repeat(2050);

        InvalidUrlException exception = assertThrows(
                InvalidUrlException.class,
                () -> UrlUtils.normalizeAndValidate(longUrl)
        );

        assertTrue(exception.getMessage().contains("URL exceeds maximum length"));
    }

    @Test
    @DisplayName("Should throw exception for invalid URL format")
    void shouldThrowWhenInvalidUrlFormat() {
        String invalidUrl = "http://invalid_url";

        InvalidUrlException exception = assertThrows(
                InvalidUrlException.class,
                () -> UrlUtils.normalizeAndValidate(invalidUrl)
        );

        assertEquals("Invalid URL format", exception.getMessage());
    }

    @Test
    @DisplayName("isValid should return true for valid URLs")
    void isValidShouldReturnTrueForValidUrls() {
        assertTrue(UrlUtils.isValid("https://google.com"));
        assertTrue(UrlUtils.isValid("http://example.co.uk"));
    }

    @Test
    @DisplayName("isValid should return false for invalid URLs")
    void isValidShouldReturnFalseForInvalidUrls() {
        assertFalse(UrlUtils.isValid("invalid-url"));
        assertFalse(UrlUtils.isValid("http://invalid_url"));
        assertFalse(UrlUtils.isValid(null));
    }
}
