package com.example.khuvote.contract.dto;

import lombok.Builder;

import java.util.List;

public class TurnoutRateDTO {

    @Builder
    public record OverallVoterRequest (
            String contractAddress
    ) {}

    @Builder
    public record OverallVoterResponse (
            String turnoutPercent
    ) {}

    @Builder
    public record AllCandidateRequest (
            String contractAddress
    ) {}

    @Builder
    public record AllCandidateResponse (
            List<CandidateTurnout> candidateTurnouts
    ) {}

    @Builder
    public record CandidateTurnout (
            String candidateOption,
            String turnoutPercent
    ) {}
}
