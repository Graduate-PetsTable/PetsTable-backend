package com.example.petstable.global.auth.apple;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com/auth")
public interface AppleClient {

    @Cacheable(value = "oauthPublicKeyCache", cacheManager = "oauthPublicKeyCacheManager")
    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AppleTokenResponse getAppleToken(@RequestParam("client_secret") String clientSecret,
                                @RequestParam("code") String authCode,
                                @RequestParam("grant_type") String grantType,
                                @RequestParam("client_id") String clientId);

    @PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void revokeMember(@RequestParam("client_id") String clientId,
                    @RequestParam("client_secret") String clientSecret,
                    @RequestParam("token") String accessToken,
                    @RequestParam("token_type_hint") String tokenTypeHint);
}