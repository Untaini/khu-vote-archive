package com.example.khuvote.university.auth;

import com.example.khuvote.university.dto.PasswordAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/affiliation")
@RequiredArgsConstructor
public class PasswordAuthController {

    private final PasswordAuthService authService;

    @PostMapping("/verify")
    public ResponseEntity<PasswordAuthDTO.Response> verifyAffiliationPassword(
            @RequestBody PasswordAuthDTO.Request request) {

        PasswordAuthDTO.Response response = authService.getAffiliation(request);

        return ResponseEntity.ok(response);
    }
}
