package com.example.khuvote.vote.service;

import com.example.khuvote.contract.VoteContractService;
import com.example.khuvote.contract.dto.CandidateAdditionDTO;
import com.example.khuvote.contract.dto.VoteDeployDTO;
import com.example.khuvote.vote.dto.ScheduleVoteDTO;
import com.example.khuvote.vote.dto.VoteCreationDTO;
import com.example.khuvote.vote.entity.VoteInfo;
import com.example.khuvote.vote.event.DistributeTokenEventPublisher;
import com.example.khuvote.vote.repository.VoteCreationRepository;
import com.example.khuvote.vote.schedule.VotingProcessScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteCreationService {

    private final VoteContractService voteContractService;
    private final VoteCreationRepository voteCreationRepository;
    private final DistributeTokenEventPublisher distributeTokenEventPublisher;
    private final VotingProcessScheduler votingProcessScheduler;

    @Transactional
    public VoteCreationDTO.Response createVote(VoteCreationDTO.Command command) {
        VoteDeployDTO.Request deployRequest = VoteDeployDTO.Request.builder()
                .title(command.title())
                .description(command.description())
                .voterCount(command.voterCount())
                .build();

        VoteDeployDTO.Response deployResponse = voteContractService.voteDeploy(deployRequest);

        getCandidatesWithAbstentionOption(command.candidates()).forEach((candidate) -> {
            CandidateAdditionDTO.Request addRequest = CandidateAdditionDTO.Request.builder()
                    .candidateInfo(candidate)
                    .contractAddress(deployResponse.contractAddress())
                    .deployKey(deployResponse.deployKey())
                    .build();

            CandidateAdditionDTO.Response addResponse = voteContractService.addCandidate(addRequest);

            log.info(String.format("[Contract: %s] addCandidate: %s", deployResponse.contractAddress(), addResponse.transactionHash()));
        });

        VoteInfo voteInfo = VoteInfo.builder()
                .affiliation(command.affiliation())
                .contractAddress(deployResponse.contractAddress())
                .managedKey(deployResponse.deployKey().getKey().getPrivateKey())
                .startTime(command.startTime())
                .endTime(command.endTime())
                .build();

        voteCreationRepository.save(voteInfo);

        distributeTokenEventPublisher.publishEvent(voteInfo, deployRequest.voterCount());

        ScheduleVoteDTO.Request scheduleRequest = ScheduleVoteDTO.Request.builder()
                .deployKey(deployResponse.deployKey().getKey().getPrivateKey())
                .contractAddress(deployResponse.contractAddress())
                .startTime(command.startTime())
                .endTime(command.endTime())
                .build();

        votingProcessScheduler.scheduleVote(scheduleRequest);

        return VoteCreationDTO.Response.builder()
                .build();
    }

    private List<String> getCandidatesWithAbstentionOption(List<String> candidates) {
        List<String> newList = new ArrayList(candidates);
        newList.add("기권하기");
        return Collections.unmodifiableList(newList);
    }
}
