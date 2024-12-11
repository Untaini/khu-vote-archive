package com.example.khuvote.contract.dto;

import lombok.Builder;

import java.util.List;

public class VoteDetailDTO {

    @Builder
    public record Request(
            String contractAddress
    ) {}

    @Builder
    public record Response(
            String voteTitle,
            String voteDescription,
            List<String> candidateOptions,
            Boolean isVotingPeriod
    ) {}
}
