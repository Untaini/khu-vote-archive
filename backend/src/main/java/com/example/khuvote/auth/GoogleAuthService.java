package com.example.khuvote.auth;

import com.example.khuvote.auth.dto.GoogleJwtResponseDTO;
import com.example.khuvote.auth.dto.GoogleLoginResponseDTO;
import com.example.khuvote.util.RestClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${google.id}")
    private String googleId;

    @Value("${google.secret}")
    private String googleSecret;

    @Value("${google.token-api-uri}")
    private String tokenApiUri;

    @Value("${google.scope}")
    private List<String> scope;

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final RestClientUtil restClientUtil;

    public String getAuthorizeCodeUri() {
        String uri = AUTH_URL
                + "?client_id=" + googleId
                + "&redirect_uri=" + tokenApiUri //URLEncoder.encode(tokenApiUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode(String.join(" ", scope), StandardCharsets.UTF_8);

        return uri;
    }

    public GoogleLoginResponseDTO getOAuthToken(String code) {
        String requestBody = "code=" + code //URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + googleId
                + "&client_secret=" + googleSecret
                + "&redirect_uri=" + tokenApiUri //URLEncoder.encode(tokenApiUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        GoogleJwtResponseDTO jwtResponseDTO;
        try {
            jwtResponseDTO = restClientUtil.post(TOKEN_URL, MediaType.APPLICATION_FORM_URLENCODED,
                    requestBody, GoogleJwtResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return GoogleLoginResponseDTO.builder()
                .token(jwtResponseDTO.getIdToken())
                .build();
    }
}
