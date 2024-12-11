package com.example.khuvote.auth.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GooglePublicKeyResponseDTO {

    private List<PublicKeyDTO> keys = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PublicKeyDTO {
        private String kid;
        private String kty;
        private String alg;
        private String n;
        private String e;
        private String use;
    }
}
