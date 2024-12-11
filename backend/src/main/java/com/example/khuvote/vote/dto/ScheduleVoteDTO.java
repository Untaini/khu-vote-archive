package com.example.khuvote.vote.dto;

import lombok.Builder;

import java.sql.Timestamp;

public class ScheduleVoteDTO {

    @Builder
    public record Request(
            String deployKey,
            String contractAddress,
            Timestamp startTime,
            Timestamp endTime
    ) {}

    @Builder
    public record Response() {}
}
