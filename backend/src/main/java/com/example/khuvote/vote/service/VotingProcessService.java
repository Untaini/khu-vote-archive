package com.example.khuvote.vote.service;

import com.example.khuvote.contract.VoteContractService;
import com.example.khuvote.contract.dto.EndVotingDTO;
import com.example.khuvote.contract.dto.StartVotingDTO;
import com.example.khuvote.vote.dto.VotingProcessDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingProcessService {

    private final VoteContractService voteContractService;

    public VotingProcessDTO.StartResponse startVoting(VotingProcessDTO.StartRequest request) {
        StartVotingDTO.Response response = voteContractService.startVoting(request.toContractRequest());

        log.info(String.format("[Contract: %s] startVoting: %s", request.contractAddress(), response.transactionHash()));

        return VotingProcessDTO.StartResponse.builder()
                .build();
    }

    public VotingProcessDTO.EndResponse endVoting(VotingProcessDTO.EndRequest request) {
        EndVotingDTO.Response response = voteContractService.endVoting(request.toContactRequest());

        log.info(String.format("[Contract: %s] endVoting: %s", request.contractAddress(), response.transactionHash()));

        return VotingProcessDTO.EndResponse.builder()
                .build();
    }
}
