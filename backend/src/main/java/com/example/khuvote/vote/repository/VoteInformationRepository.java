package com.example.khuvote.vote.repository;

import com.example.khuvote.vote.entity.VoteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VoteInformationRepository extends JpaRepository<VoteInfo, Long> {

    List<VoteInfo> findAllByAffiliationIsInAndEndTimeAfter(List<String> affiliation, Timestamp time);

}
