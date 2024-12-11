package com.example.khuvote.university.dto;

import lombok.Builder;

public class PasswordAuthDTO {

    @Builder
    public record Request(
            String password
    ) {}

    @Builder
    public record Response(
            String affiliation
    ) {}

}
