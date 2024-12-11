package com.example.khuvote.contract.dto;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.Builder;

public class VoteDeployDTO {

    @Builder
    public record Request(
            String title,
            String description,
            Long voterCount
    ) {}

    @Builder
    public record Response(
            SingleKeyring deployKey,
            String contractAddress
    ) {}
}
