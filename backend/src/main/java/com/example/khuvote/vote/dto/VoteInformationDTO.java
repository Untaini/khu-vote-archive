package com.example.khuvote.vote.dto;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.List;

public class VoteInformationDTO {

    @Builder
    public record VotePreviewRequest(

    ) {
        public VotePreviewCommand toCommand(String token) {
            return VotePreviewCommand.builder()
                    .token(token)
                    .build();
        }
    }

    @Builder
    public record VotePreviewCommand(
            String token
    ) {}

    @Builder
    public record VotePreviewResponse(
            List<VotePreviewInfo> votePreviewInfos
    ) {}

    @Builder
    public record VotePreviewInfo (
            Long voteId,
            String title,
            String turnoutPercent,
            Timestamp startTime,
            Timestamp endTime,
            Boolean isVotingPeriod,
            Boolean isVoted,
            String affiliation
    ) {}

    @Builder
    public record VoteDetailRequest (

    ) {
        public VoteDetailCommand toCommand(Long voteId, String token) {
            return VoteDetailCommand.builder()
                    .voteId(voteId)
                    .token(token)
                    .build();
        }

    }

    @Builder
    public record VoteDetailCommand (
            Long voteId,
            String token
    ) {}

    @Builder
    public record VoteDetailResponse (
            String title,
            String description,
            List<String> candidates,
            Timestamp startTime,
            Timestamp endTime,
            Boolean isVotingPeriod,
            String turnoutPercent,
            String affiliation
    ) {}

    @Builder
    public record VoteResultRequest (

    ) {
        public VoteResultCommand toCommand(Long voteId, String token) {
            return VoteResultCommand.builder()
                    .voteId(voteId)
                    .token(token)
                    .build();
        }
    }

    @Builder
    public record VoteResultCommand (
            Long voteId,
            String token
    ) {}

    @Builder
    public record VoteResultResponse (
            String title,
            List<CandidateTurnout> voteResult,
            String affiliation
    ) {}

    @Builder
    public record CandidateTurnout (
            String candidateOption,
            String turnoutPercent
    ) {}

}
