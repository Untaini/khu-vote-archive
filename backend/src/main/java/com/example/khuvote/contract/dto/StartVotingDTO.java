package com.example.khuvote.contract.dto;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.Builder;

public class StartVotingDTO {

    @Builder
    public record Request(
            String contractAddress,
            SingleKeyring deployKey
    ) {}

    @Builder
    public record Response(
            String transactionHash
    ) {}
}
