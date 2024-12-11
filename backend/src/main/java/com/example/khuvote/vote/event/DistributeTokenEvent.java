package com.example.khuvote.vote.event;

import com.example.khuvote.vote.entity.VoteInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistributeTokenEvent {

    private VoteInfo voteInfo;
    private Long voterCount;

}
