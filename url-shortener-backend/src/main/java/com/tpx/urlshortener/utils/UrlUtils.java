package com.tpx.urlshortener.utils;

import com.tpx.urlshortener.exceptions.InvalidUrlException;
import org.apache.commons.validator.routines.UrlValidator;

public final class UrlUtils {

    private static final String[] ALLOWED_SCHEMES = {"http", "https"};
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(ALLOWED_SCHEMES);
    private static final int MAX_URL_LENGTH = 2048;

    private UrlUtils() {
    }

    public static String normalizeAndValidate(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL cannot be empty");
        }

        String normalized = url.trim();

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://" + normalized;
        }

        if (normalized.length() > MAX_URL_LENGTH) {
            throw new InvalidUrlException("URL exceeds maximum length of " + MAX_URL_LENGTH);
        }

        if (!isValid(normalized)) {
            throw new InvalidUrlException("Invalid URL format");
        }

        return normalized;
    }

    public static boolean isValid(String url) {
        return url != null && URL_VALIDATOR.isValid(url);
    }
}
