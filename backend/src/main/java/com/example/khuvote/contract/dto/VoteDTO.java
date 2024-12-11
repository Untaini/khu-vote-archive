package com.example.khuvote.contract.dto;

import lombok.Builder;

public class VoteDTO {

    @Builder
    public record Request(
            String contractAddress,
            String voterKey,
            String candidate,
            String id
    ) {}

    @Builder
    public record Response(
            String transactionHash
    ) {}
}
