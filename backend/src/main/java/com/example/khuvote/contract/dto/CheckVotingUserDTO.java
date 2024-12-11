package com.example.khuvote.contract.dto;

import lombok.Builder;

public class CheckVotingUserDTO {

    @Builder
    public record Request(
            String contractAddress,
            String id
    ) {}

    @Builder
    public record Response(
            Boolean isVotingUser
    ) {}
}
