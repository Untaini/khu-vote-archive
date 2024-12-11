package com.example.khuvote.vote.event;

import com.example.khuvote.vote.entity.VoteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributeTokenEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(VoteInfo voteInfo, Long voterCount) {
        DistributeTokenEvent event = new DistributeTokenEvent(voteInfo, voterCount);
        eventPublisher.publishEvent(event);
    }
}
