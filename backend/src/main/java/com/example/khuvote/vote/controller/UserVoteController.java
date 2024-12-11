package com.example.khuvote.vote.controller;

import com.example.khuvote.vote.dto.UserVoteDTO;
import com.example.khuvote.vote.service.UserVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class UserVoteController {

    private final UserVoteService userVoteService;

    @PostMapping("/{id}")
    public ResponseEntity<UserVoteDTO.Response> vote(@PathVariable Long id,
                                                     @RequestHeader("Authorization") String token,
                                                     @RequestBody UserVoteDTO.Request request) {

        return ResponseEntity.ok(userVoteService.vote(request.toCommand(id, token)));
    }
}
