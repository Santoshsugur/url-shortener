package com.santosh.URLShortnerApplication.repository;

import com.santosh.URLShortnerApplication.entity.Shorturl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<Shorturl, Long> {

    Optional<Shorturl> findByShortCode(String shortCode);

    Optional<Shorturl> findByOriginalUrl(String originalUrl);
}
