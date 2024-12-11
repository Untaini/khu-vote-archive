package com.example.khuvote.vote.dto;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.List;

public class VoteCreationDTO {

    @Builder
    public record Request(
            String title,
            String description,
            Long voterCount,
            Timestamp startTime,
            Timestamp endTime,
            List<String> candidates,
            String affiliationPassword
    ) {
        public Command toCommand(String affiliation) {
            return Command.builder()
                    .title(title)
                    .description(description)
                    .voterCount(voterCount)
                    .startTime(startTime)
                    .endTime(endTime)
                    .candidates(candidates)
                    .affiliation(affiliation)
                    .build();
        }
    }

    @Builder
    public record Command(
            String title,
            String description,
            Long voterCount,
            Timestamp startTime,
            Timestamp endTime,
            List<String> candidates,
            String affiliation
    ) {}

    @Builder
    public record Response() {}
}
