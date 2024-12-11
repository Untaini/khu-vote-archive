package com.example.khuvote.auth.util;

import com.example.khuvote.auth.dto.GooglePublicKeyResponseDTO;
import com.example.khuvote.auth.dto.IdTokenClaimDTO;
import com.example.khuvote.util.RestClientUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ProtectedHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleJwtUtils {

    @Value("${google.certs-uri}")
    private String googleCertsUri;

    @Value("${google.issuer}")
    private String googleIssuer;

    @Value("${google.id}")
    private String googleId;

    private Map<String, RSAPublicKey> googlePublicKeyCache = new HashMap<>();

    private final RestClientUtil restClientUtil;

    public boolean verifyIdToken(String idToken) {
        return parseIdToken(idToken) != null;
    }

    public IdTokenClaimDTO parseIdToken(String idToken) {
        if (googlePublicKeyCache.isEmpty()) {
            loadGooglePublicKeys();
        }

        try {
            JwtParser parser = Jwts.parser()
                    .requireIssuer(googleIssuer)
                    .requireAudience(googleId)
                    .keyLocator(header -> {
                        String keyId = ((ProtectedHeader) header).getKeyId();

                        if (!googlePublicKeyCache.containsKey(keyId)) {
                            loadGooglePublicKeys();

                            if (!googlePublicKeyCache.containsKey(keyId)) {
                                throw new RuntimeException("Invalid Token");
                            }
                        }

                        return googlePublicKeyCache.get(keyId);
                    })
                    .build();
            Claims claims = parser.parseSignedClaims(idToken).getPayload();

            return IdTokenClaimDTO.builder()
                    .id(claims.getSubject())
                    .name(claims.get("given_name", String.class))
                    .email(claims.get("email", String.class))
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    private void loadGooglePublicKeys() {
        GooglePublicKeyResponseDTO keyResponse = restClientUtil.get(googleCertsUri, GooglePublicKeyResponseDTO.class);

        Map<String, RSAPublicKey> newPublicKeyCache = new HashMap<>();

        for (GooglePublicKeyResponseDTO.PublicKeyDTO keyDTO : keyResponse.getKeys()) {
            try {
                String keyId = keyDTO.getKid();
                String algorithm = keyDTO.getKty();
                byte[] modulus = Base64.getUrlDecoder().decode(keyDTO.getN());
                byte[] exponent = Base64.getUrlDecoder().decode(keyDTO.getE());
                newPublicKeyCache.put(keyId, createPublicKey(modulus, exponent, algorithm));
            } catch (Exception e) {}
        }

        this.googlePublicKeyCache = newPublicKeyCache;
    }

    private RSAPublicKey createPublicKey(byte[] modulus, byte[] exponent, String algorithm) throws Exception {
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent));
        KeyFactory factory = KeyFactory.getInstance(algorithm);
        return (RSAPublicKey) factory.generatePublic(spec);
    }

}
