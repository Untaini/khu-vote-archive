package com.example.khuvote.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GoogleTokenVerifyResponseDTO {
    private Boolean isVerified;
}