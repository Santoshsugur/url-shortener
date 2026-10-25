package com.santosh.URLShortnerApplication.controller;

import com.santosh.URLShortnerApplication.dto.ShortenUrlRequest;
import com.santosh.URLShortnerApplication.dto.ShortenUrlResponse;
import com.santosh.URLShortnerApplication.entity.Shorturl;
import com.santosh.URLShortnerApplication.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UrlShortenerService urlShortenerService;

    /**
     * GET /: Shows the URL shortener form page.
     */
    @GetMapping("/")
    public String showFormPage(Model model) {
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ShortenUrlRequest());
        }
        return "index";
    }

    /**
     * POST /shorten: Processes the URL shortening submission and displays result.
     */
    @PostMapping("/shorten")
    public String shortenUrl(@Valid @ModelAttribute("request") ShortenUrlRequest request,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest servletRequest) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        try {
            Shorturl shorturl = urlShortenerService.createShortUrl(request.getUrl(), request.getExpiresAt());

            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(servletRequest)
                    .replacePath(null)
                    .build()
                    .toUriString();

            String fullShortUrl = baseUrl + "/" + shorturl.getShortCode();

            ShortenUrlResponse response = ShortenUrlResponse.builder()
                    .shortCode(shorturl.getShortCode())
                    .shortUrl(fullShortUrl)
                    .originalUrl(shorturl.getOriginalUrl())
                    .createdAt(shorturl.getCreatedAt())
                    .expiresAt(shorturl.getExpiresAt())
                    .build();

            model.addAttribute("result", response);
            model.addAttribute("shortUrl", fullShortUrl);
            model.addAttribute("shortCode", shorturl.getShortCode());
            model.addAttribute("originalUrl", shorturl.getOriginalUrl());
            model.addAttribute("successMessage", "URL shortened successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error shortening URL: " + e.getMessage());
        }

        return "index";
    }

    /**
     * GET /{shortCode}: Redirects to the original URL.
     */
    @GetMapping("/{shortCode}")
    public String redirectToOriginalUrl(@PathVariable("shortCode") String shortCode, Model model) {
        String originalUrl = urlShortenerService.getOriginalUrl(shortCode);
        if (originalUrl != null && !originalUrl.isBlank()) {
            return "redirect:" + originalUrl;
        }

        model.addAttribute("errorMessage", "Short URL not found or has expired for code: " + shortCode);
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ShortenUrlRequest());
        }
        return "index";
    }
}
