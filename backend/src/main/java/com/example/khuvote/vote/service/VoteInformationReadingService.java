package com.example.khuvote.vote.service;

import com.example.khuvote.auth.dto.IdTokenClaimDTO;
import com.example.khuvote.auth.util.GoogleJwtUtils;
import com.example.khuvote.contract.VoteContractService;
import com.example.khuvote.contract.dto.CheckVotingUserDTO;
import com.example.khuvote.contract.dto.TurnoutRateDTO;
import com.example.khuvote.contract.dto.VoteDetailDTO;
import com.example.khuvote.university.AffiliationInfoParser;
import com.example.khuvote.util.HashUtil;
import com.example.khuvote.vote.dto.VoteInformationDTO;
import com.example.khuvote.vote.entity.VoteInfo;
import com.example.khuvote.vote.repository.VoteInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteInformationReadingService {

    private final GoogleJwtUtils googleJwtUtils;
    private final AffiliationInfoParser affiliationInfoParser;
    private final VoteContractService voteContractService;
    private final VoteInformationRepository voteInformationRepository;
    private final HashUtil hashUtil;

    private static final String NOT_PERMISSION_BY_VIEW_VOTE_DATA = "투표 정보를 볼 권한이 없습니다.";
    private static final String VOTE_NOT_ENDED = "투표가 종료되지 않았습니다.";
    private static final String TOKEN_EXPIRED = "로그인이 만료되었습니다.";

    @Transactional
    public VoteInformationDTO.VotePreviewResponse getVotePreview(VoteInformationDTO.VotePreviewCommand command) {

        IdTokenClaimDTO tokenClaim = parseToken(command.token());
        AffiliationInfoParser.AffiliationInfo affiliationInfo
                = affiliationInfoParser.parseAffiliationInfo(tokenClaim.getName());
        List<String> affiliationNames = getAffiliationNames(affiliationInfo);

        Timestamp sixMonthAgoSinceNow = Timestamp.valueOf(LocalDateTime.now().minusMonths(6));
        List<VoteInfo> voteInfos
                = voteInformationRepository.findAllByAffiliationIsInAndEndTimeAfter(affiliationNames, sixMonthAgoSinceNow);

        List<VoteInformationDTO.VotePreviewInfo> previewInfos = voteInfos.stream()
                .sorted(Comparator.comparing(VoteInfo::getEndTime).reversed())
                .map(voteInfo -> {
                    VoteDetailDTO.Request voteDetailRequest = VoteDetailDTO.Request.builder()
                            .contractAddress(voteInfo.getContractAddress())
                            .build();

                    VoteDetailDTO.Response voteDetailResponse = voteContractService.getVoteDetail(voteDetailRequest);

                    TurnoutRateDTO.OverallVoterRequest overallVoterRequest = TurnoutRateDTO.OverallVoterRequest.builder()
                            .contractAddress(voteInfo.getContractAddress())
                            .build();

                    TurnoutRateDTO.OverallVoterResponse overallVoterResponse
                            = voteContractService.getOverallVoterTurnout(overallVoterRequest);

                    CheckVotingUserDTO.Request votingUserRequest = CheckVotingUserDTO.Request.builder()
                            .contractAddress(voteInfo.getContractAddress())
                            .id(hashUtil.hashSHA256(tokenClaim.getId()))
                            .build();

                    CheckVotingUserDTO.Response votingUserResponse
                            = voteContractService.checkVotingUser(votingUserRequest);

                    return VoteInformationDTO.VotePreviewInfo.builder()
                            .voteId(voteInfo.getId())
                            .title(voteDetailResponse.voteTitle())
                            .startTime(voteInfo.getStartTime())
                            .endTime(voteInfo.getEndTime())
                            .isVotingPeriod(voteDetailResponse.isVotingPeriod())
                            .turnoutPercent(overallVoterResponse.turnoutPercent())
                            .isVoted(votingUserResponse.isVotingUser())
                            .affiliation(voteInfo.getAffiliation())
                            .build();
                })
                .toList();

        return VoteInformationDTO.VotePreviewResponse.builder()
                .votePreviewInfos(previewInfos)
                .build();
    }

    @Transactional
    public VoteInformationDTO.VoteDetailResponse getVoteDetail(VoteInformationDTO.VoteDetailCommand command) {
        VoteInfo voteInfo = voteInformationRepository.getReferenceById(command.voteId());

        if (!checkUserHasAffiliation(command.token(), voteInfo)) {
            throw new RuntimeException(NOT_PERMISSION_BY_VIEW_VOTE_DATA);
        }

        VoteDetailDTO.Request voteDetailRequest = VoteDetailDTO.Request.builder()
                .contractAddress(voteInfo.getContractAddress())
                .build();

        VoteDetailDTO.Response voteDetailResponse = voteContractService.getVoteDetail(voteDetailRequest);

        TurnoutRateDTO.OverallVoterRequest overallVoterRequest = TurnoutRateDTO.OverallVoterRequest.builder()
                .contractAddress(voteInfo.getContractAddress())
                .build();

        TurnoutRateDTO.OverallVoterResponse overallVoterResponse
                = voteContractService.getOverallVoterTurnout(overallVoterRequest);

        return VoteInformationDTO.VoteDetailResponse.builder()
                .title(voteDetailResponse.voteTitle())
                .description(voteDetailResponse.voteDescription())
                .candidates(voteDetailResponse.candidateOptions())
                .startTime(voteInfo.getStartTime())
                .endTime(voteInfo.getEndTime())
                .isVotingPeriod(voteDetailResponse.isVotingPeriod())
                .turnoutPercent(overallVoterResponse.turnoutPercent())
                .affiliation(voteInfo.getAffiliation())
                .build();
    }

    @Transactional
    public VoteInformationDTO.VoteResultResponse getVoteResult(VoteInformationDTO.VoteResultCommand command) {
        VoteInfo voteInfo = voteInformationRepository.getReferenceById(command.voteId());

        if (!checkUserHasAffiliation(command.token(), voteInfo)) {
            throw new RuntimeException(NOT_PERMISSION_BY_VIEW_VOTE_DATA);
        }

        if (!voteInfo.getEndTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new RuntimeException(VOTE_NOT_ENDED);
        }

        VoteDetailDTO.Request voteDetailRequest = VoteDetailDTO.Request.builder()
                .contractAddress(voteInfo.getContractAddress())
                .build();

        VoteDetailDTO.Response voteDetailResponse = voteContractService.getVoteDetail(voteDetailRequest);

        TurnoutRateDTO.AllCandidateRequest allCandidateRequest = TurnoutRateDTO.AllCandidateRequest.builder()
                .contractAddress(voteInfo.getContractAddress())
                .build();

        TurnoutRateDTO.AllCandidateResponse allCandidateResponse
                = voteContractService.getAllCandidateTurnout(allCandidateRequest);

        List<VoteInformationDTO.CandidateTurnout> candidateTurnouts = allCandidateResponse.candidateTurnouts().stream()
                .map(candidateTurnout -> VoteInformationDTO.CandidateTurnout.builder()
                        .candidateOption(candidateTurnout.candidateOption())
                        .turnoutPercent(candidateTurnout.turnoutPercent())
                        .build())
                .toList();

        return VoteInformationDTO.VoteResultResponse.builder()
                .title(voteDetailResponse.voteTitle())
                .voteResult(candidateTurnouts)
                .affiliation(voteInfo.getAffiliation())
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

    private Boolean checkUserHasAffiliation(String token, VoteInfo voteInfo) {
        IdTokenClaimDTO tokenClaim = parseToken(token);

        if (tokenClaim == null) {
            throw new RuntimeException(TOKEN_EXPIRED);
        }

        AffiliationInfoParser.AffiliationInfo affiliationInfo
                = affiliationInfoParser.parseAffiliationInfo(tokenClaim.getName());
        List<String> affiliationNames = getAffiliationNames(affiliationInfo);

        return affiliationNames.stream()
                .anyMatch(affiliationName -> affiliationName.equalsIgnoreCase(voteInfo.getAffiliation()));
    }
}
