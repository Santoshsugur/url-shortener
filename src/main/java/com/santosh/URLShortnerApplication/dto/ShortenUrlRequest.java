package com.santosh.URLShortnerApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    @URL(message = "Invalid URL format")
    private String url;

    private LocalDateTime expiresAt;

    public ShortenUrlRequest(String url) {
        this.url = url;
    }
}
