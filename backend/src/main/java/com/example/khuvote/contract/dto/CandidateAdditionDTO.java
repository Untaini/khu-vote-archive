package com.example.khuvote.contract.dto;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.Builder;

public class CandidateAdditionDTO {

    @Builder
    public record Request(
            String contractAddress,
            SingleKeyring deployKey,
            String candidateInfo
    ) {}

    @Builder
    public record Response(
            String transactionHash
    ) {}
}
