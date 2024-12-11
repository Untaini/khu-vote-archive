package com.example.khuvote.vote.event;

import com.example.khuvote.contract.VoteContractService;
import com.example.khuvote.contract.dto.TokenDistributionDTO;
import com.example.khuvote.vote.entity.VoterKeyStore;
import com.example.khuvote.vote.repository.VoterKeyStoreRepository;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributeTokenEventListener {

    private final DistributeTokenEventPublisher distributeTokenEventPublisher;
    private final VoteContractService voteContractService;
    private final VoterKeyStoreRepository voterKeyRepository;

    @Async
    @EventListener
    @Transactional
    public void handleDistributeTokenEvent(DistributeTokenEvent event) {
        Long reservedVoterCount = event.getVoterCount();
        SingleKeyring deployKey = KeyringFactory.createFromPrivateKey(event.getVoteInfo().getManagedKey());
        String contractAddress = event.getVoteInfo().getContractAddress();

        try {
            while (reservedVoterCount > 0) {
                TokenDistributionDTO.Request request = TokenDistributionDTO.Request.builder()
                        .deployKey(deployKey)
                        .contractAddress(contractAddress)
                        .distributionCount(Math.min(reservedVoterCount.intValue(), 50))
                        .build();

                TokenDistributionDTO.Response response = voteContractService.distributeTokens(request);

                reservedVoterCount -= response.voterKeys().size();

                for (SingleKeyring voterKey : response.voterKeys()) {
                    VoterKeyStore keyEntity = VoterKeyStore.builder()
                            .info(event.getVoteInfo())
                            .voterKey(voterKey.getKey().getPrivateKey())
                            .build();

                    voterKeyRepository.save(keyEntity); //TO-DO: batch save 코드로 변경

                    log.info(String.format("[Contract: %s] distributedTokenAddress: %s", contractAddress, voterKey.getAddress()));
                }
            }
        } catch (Exception e) {
            distributeTokenEventPublisher.publishEvent(event.getVoteInfo(), reservedVoterCount);
        }
    }
}
