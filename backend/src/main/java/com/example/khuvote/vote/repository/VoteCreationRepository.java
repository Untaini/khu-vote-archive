package com.example.khuvote.vote.repository;

import com.example.khuvote.vote.entity.VoteInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteCreationRepository extends JpaRepository<VoteInfo, Long> {

}
