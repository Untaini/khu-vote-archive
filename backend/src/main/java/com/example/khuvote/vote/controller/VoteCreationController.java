package com.example.khuvote.vote.controller;

import com.example.khuvote.university.auth.PasswordAuthService;
import com.example.khuvote.university.dto.PasswordAuthDTO;
import com.example.khuvote.vote.dto.VoteCreationDTO;
import com.example.khuvote.vote.service.VoteCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteCreationController {

    private final VoteCreationService voteCreationService;
    private final PasswordAuthService passwordAuthService;

    @PostMapping("/create")
    public ResponseEntity<VoteCreationDTO.Response> createVote(@RequestBody VoteCreationDTO.Request request) {

        PasswordAuthDTO.Request authRequest = PasswordAuthDTO.Request.builder()
                .password(request.affiliationPassword())
                .build();

        PasswordAuthDTO.Response authResponse = passwordAuthService.getAffiliation(authRequest);

        VoteCreationDTO.Response response
                = voteCreationService.createVote(request.toCommand(authResponse.affiliation()));

        return ResponseEntity.ok(response);
    }
}
