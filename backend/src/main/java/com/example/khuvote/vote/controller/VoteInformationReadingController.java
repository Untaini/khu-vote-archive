package com.example.khuvote.vote.controller;

import com.example.khuvote.vote.dto.VoteInformationDTO;
import com.example.khuvote.vote.service.VoteInformationReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteInformationReadingController {

    private final VoteInformationReadingService voteInformationReadingService;

    @GetMapping("/list")
    public ResponseEntity<VoteInformationDTO.VotePreviewResponse> getVotePreview(
            @RequestHeader("Authorization") String token) {

        VoteInformationDTO.VotePreviewRequest request = VoteInformationDTO.VotePreviewRequest.builder().build();

        return ResponseEntity.ok(voteInformationReadingService.getVotePreview(request.toCommand(token)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteInformationDTO.VoteDetailResponse> getVoteDetail(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        VoteInformationDTO.VoteDetailRequest request = VoteInformationDTO.VoteDetailRequest.builder().build();

        return ResponseEntity.ok(voteInformationReadingService.getVoteDetail(request.toCommand(id, token)));
    }
    
    @GetMapping("/{id}/result")
    public ResponseEntity<VoteInformationDTO.VoteResultResponse> getVoteResult(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        VoteInformationDTO.VoteResultRequest request = VoteInformationDTO.VoteResultRequest.builder().build();

        return ResponseEntity.ok(voteInformationReadingService.getVoteResult(request.toCommand(id, token)));
    }

}
