package com.example.khuvote.contract.dto;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.Builder;

import java.util.List;

public class TokenDistributionDTO {

    @Builder
    public record Request(
            SingleKeyring deployKey,
            String contractAddress,
            Integer distributionCount
    ) {}

    @Builder
    public record Response(
            List<SingleKeyring> voterKeys
    ) {}
}
