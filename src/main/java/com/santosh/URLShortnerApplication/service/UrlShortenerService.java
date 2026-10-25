package com.santosh.URLShortnerApplication.service;

import com.santosh.URLShortnerApplication.entity.Shorturl;
import com.santosh.URLShortnerApplication.repository.ShortUrlRepository;
import com.santosh.URLShortnerApplication.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    /**
     * Creates a shortened URL or returns the existing record if already present.
     *
     * @param originalUrl the original URL to shorten
     * @return the saved or existing Shorturl entity
     */
    public Shorturl createShortUrl(String originalUrl) {
        return createShortUrl(originalUrl, null);
    }

    /**
     * Alias for createShortUrl to support createShortUrI.
     */
    public Shorturl createShortUrI(String originalUrl) {
        return createShortUrl(originalUrl);
    }

    /**
     * Creates a shortened URL with optional expiration date.
     *
     * @param originalUrl the original URL to shorten
     * @param expiresAt   optional expiration timestamp
     * @return the saved or existing Shorturl entity
     */
    public Shorturl createShortUrl(String originalUrl, LocalDateTime expiresAt) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Original URL cannot be null or empty");
        }

        String trimmedUrl = originalUrl.trim();

        // 1. Check if URL already exists (return existing)
        Optional<Shorturl> existing = shortUrlRepository.findByOriginalUrl(trimmedUrl);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2. Generate unique code (handle collisions)
        String shortCode;
        int attempts = 0;
        do {
            if (attempts++ > 100) {
                throw new IllegalStateException("Failed to generate a unique short code after multiple attempts");
            }
            shortCode = shortCodeGenerator.generateCode();
        } while (shortUrlRepository.findByShortCode(shortCode).isPresent());

        // 3. Save to database
        Shorturl shorturl = Shorturl.builder()
                .originalUrl(trimmedUrl)
                .shortCode(shortCode)
                .expiresAt(expiresAt)
                .build();

        return shortUrlRepository.save(shorturl);
    }

    /**
     * Alias for createShortUrl with expiration.
     */
    public Shorturl createShortUrI(String originalUrl, LocalDateTime expiresAt) {
        return createShortUrl(originalUrl, expiresAt);
    }

    /**
     * Lookup original URL by short code.
     *
     * @param shortCode the 6-character short code
     * @return the original URL string, or null if not found
     */
    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .map(Shorturl::getOriginalUrl)
                .orElse(null);
    }

    /**
     * Lookup Shorturl entity by short code.
     *
     * @param shortCode the 6-character short code
     * @return Optional containing the Shorturl entity if found
     */
    @Transactional(readOnly = true)
    public Optional<Shorturl> getShortUrlByCode(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode);
    }
}
