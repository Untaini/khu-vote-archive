package com.example.khuvote.auth;

import com.example.khuvote.auth.dto.GoogleLoginResponseDTO;
import com.example.khuvote.auth.dto.GoogleTokenVerifyResponseDTO;
import com.example.khuvote.auth.util.GoogleJwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService authService;
    private final GoogleJwtUtils jwtUtils;

    @GetMapping()
    public ResponseEntity<String> redirectAuthorizeUrl() {
        String redirectUri = authService.getAuthorizeCodeUri();

        return ResponseEntity.ok(redirectUri);
    }

    @GetMapping("/token")
    public ResponseEntity<GoogleLoginResponseDTO> getOAuthToken(@RequestParam("code") String code) {
        GoogleLoginResponseDTO tokenResponse = authService.getOAuthToken(code);

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<GoogleTokenVerifyResponseDTO> verifyToken(@RequestHeader("Authorization") String token) {
        final String idToken;
        if (token.startsWith("Bearer")) {
            idToken = token.substring(7);
        } else {
            idToken = token;
        }

        return ResponseEntity.ok(GoogleTokenVerifyResponseDTO.builder()
                .isVerified(jwtUtils.verifyIdToken(idToken))
                .build());
    }
}
