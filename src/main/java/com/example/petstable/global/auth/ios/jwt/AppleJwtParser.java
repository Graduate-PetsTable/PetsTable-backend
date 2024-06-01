package com.example.petstable.global.auth.ios.jwt;

import com.example.petstable.global.exception.PetsTableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import org.springframework.stereotype.Component;

import static com.example.petstable.global.exception.message.OAuthLoginMessage.EXPIRED_ID_TOKEN;
import static com.example.petstable.global.exception.message.OAuthLoginMessage.INVALID_ID_TOKEN;


/**
 * Identity Token 의 헤더에서 alg, kid 값 추출
 */
@Component
public class AppleJwtParser {

    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedHeader); // URL 및 파일 이름 안전한 Base64 디코딩
            String decodedHeader = new String(decodedBytes); // 디코딩된 바이트 배열을 문자열로 변환

            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }

    public Claims parsePublicKeyAndGetClaims(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new PetsTableException(EXPIRED_ID_TOKEN.getStatus(), EXPIRED_ID_TOKEN.getMessage(), 401);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }
}