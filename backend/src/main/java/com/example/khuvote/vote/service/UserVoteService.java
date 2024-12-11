package com.example.khuvote.vote.service;

import com.example.khuvote.auth.dto.IdTokenClaimDTO;
import com.example.khuvote.auth.util.GoogleJwtUtils;
import com.example.khuvote.contract.VoteContractService;
import com.example.khuvote.contract.dto.VoteDTO;
import com.example.khuvote.university.AffiliationInfoParser;
import com.example.khuvote.util.HashUtil;
import com.example.khuvote.vote.dto.UserVoteDTO;
import com.example.khuvote.vote.entity.VoteInfo;
import com.example.khuvote.vote.entity.VoterKeyStore;
import com.example.khuvote.vote.repository.UserVoteRepository;
import com.example.khuvote.vote.repository.VoterKeyStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVoteService {

    private final GoogleJwtUtils googleJwtUtils;
    private final AffiliationInfoParser affiliationInfoParser;
    private final HashUtil hashUtil;
    private final VoteContractService voteContractService;
    private final UserVoteRepository userVoteRepository;
    private final VoterKeyStoreRepository voterKeyStoreRepository;

    private static final String NOT_PERMISSION_BY_VOTE = "소속이 달라 투표를 할 수 없습니다.";
    private static final String NOT_READY_TO_VOTE = "투표 준비가 되지 않았습니다. 나중에 다시 시도해주세요.";

    @Transactional
    public UserVoteDTO.Response vote(UserVoteDTO.Command command) {
        IdTokenClaimDTO tokenClaim = parseToken(command.token());

        VoteInfo voteInfo = userVoteRepository.getReferenceById(command.voteId());

        if (!checkUserHasAffiliation(tokenClaim.getName(), voteInfo)) {
            throw new RuntimeException(NOT_PERMISSION_BY_VOTE);
        }

        VoterKeyStore voterKeyStore = voterKeyStoreRepository.findFirstByInfo(voteInfo)
                .orElseThrow(() -> new RuntimeException(NOT_READY_TO_VOTE));

        VoteDTO.Request voteRequest = VoteDTO.Request.builder()
                .contractAddress(voteInfo.getContractAddress())
                .id(hashUtil.hashSHA256(tokenClaim.getId()))
                .candidate(command.candidateOption())
                .voterKey(voterKeyStore.getVoterKey())
                .build();

        VoteDTO.Response voteResponse = voteContractService.vote(voteRequest);



        log.info(String.format("[Contract: %s] vote: %s", voteInfo.getContractAddress(), voteResponse.transactionHash()));

        voterKeyStoreRepository.delete(voterKeyStore);

        return UserVoteDTO.Response.builder()
                .build();
    }


    private IdTokenClaimDTO parseToken(String token) {
        final String convertedToken;
        if (token.startsWith("Bearer ")) {
            convertedToken = token.substring(7);
        } else {
            convertedToken = token;
        }

        return googleJwtUtils.parseIdToken(convertedToken);
    }

    private List<String> getAffiliationNames(AffiliationInfoParser.AffiliationInfo affiliationInfo) {
        List<String> affiliationNames = new ArrayList<>();
        affiliationNames.add(affiliationInfo.collegeType().getCampusType().getName());
        affiliationNames.add(affiliationInfo.collegeType().getName());
        affiliationNames.add(affiliationInfo.departmentType().getName());

        return affiliationNames;
    }

    private Boolean checkUserHasAffiliation(String userName, VoteInfo voteInfo) {
        AffiliationInfoParser.AffiliationInfo affiliationInfo
                = affiliationInfoParser.parseAffiliationInfo(userName);
        List<String> affiliationNames = getAffiliationNames(affiliationInfo);

        return affiliationNames.stream()
                .anyMatch(affiliationName -> affiliationName.equalsIgnoreCase(voteInfo.getAffiliation()));
    }
}
