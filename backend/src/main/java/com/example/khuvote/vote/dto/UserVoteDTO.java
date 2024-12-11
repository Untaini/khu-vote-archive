package com.example.khuvote.vote.dto;

import lombok.Builder;

public class UserVoteDTO {

    @Builder
    public record Request(
            String candidateOption
    ) {
        public Command toCommand(Long voteId, String token) {
            return Command.builder()
                    .voteId(voteId)
                    .token(token)
                    .candidateOption(candidateOption)
                    .build();
        }
    }

    @Builder
    public record Command(
            Long voteId,
            String token,
            String candidateOption
    ) {}

    @Builder
    public record Response(

    ) {}
}
