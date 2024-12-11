package com.example.khuvote.vote.repository;

import com.example.khuvote.vote.entity.VoteInfo;
import com.example.khuvote.vote.entity.VoterKeyStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterKeyStoreRepository extends JpaRepository<VoterKeyStore, Long> {

    Optional<VoterKeyStore> findFirstByInfo(VoteInfo info);

}
