package com.example.khuvote.vote.dto;

import com.example.khuvote.contract.dto.EndVotingDTO;
import com.example.khuvote.contract.dto.StartVotingDTO;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.Builder;

public class VotingProcessDTO {

    @Builder
    public record StartRequest(
            String deployKey,
            String contractAddress
    ) {
        public StartVotingDTO.Request toContractRequest() {
            SingleKeyring deployKeyring = KeyringFactory.createFromPrivateKey(deployKey);

            return StartVotingDTO.Request.builder()
                    .deployKey(deployKeyring)
                    .contractAddress(contractAddress)
                    .build();
        }
    }

    @Builder
    public record StartResponse() {}

    @Builder
    public record EndRequest(
            String deployKey,
            String contractAddress
    ) {
        public EndVotingDTO.Request toContactRequest() {
            SingleKeyring deployKeyring = KeyringFactory.createFromPrivateKey(deployKey);

            return EndVotingDTO.Request.builder()
                    .deployKey(deployKeyring)
                    .contractAddress(contractAddress)
                    .build();
        }
    }

    @Builder
    public record EndResponse() {}

}
