package com.example.khuvote.contract;

import com.example.khuvote.contract.dto.*;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.*;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.KlayLogs;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import io.reactivex.Single;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class VoteContractService {

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(100000000L);
    private static final String TOKEN_DISTRIBUTED_EVENT_SIGNATURE = Hash.sha3String("TokenDistributed(address)");

    private final VoteContractUtil voteContractUtil;

    public VoteDeployDTO.Response voteDeploy(VoteDeployDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        SingleKeyring deployKey = KeyringFactory.generate();
        caver.wallet.add(deployKey);

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall());
            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(deployKey.getAddress());
            sendOptions.setGas(GAS_LIMIT);

            Contract newContract = contract.deploy(sendOptions, voteContractUtil.getVoteBytecode(),
                    request.title(), "VOTE", request.voterCount(), request.description());

            return VoteDeployDTO.Response.builder()
                    .deployKey(deployKey)
                    .contractAddress(newContract.getContractAddress())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CandidateAdditionDTO.Response addCandidate(CandidateAdditionDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        SingleKeyring candidateKey = KeyringFactory.generate();
        caver.wallet.add(request.deployKey());

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(request.deployKey().getAddress());
            sendOptions.setGas(GAS_LIMIT);

            TransactionReceipt.TransactionReceiptData receiptData = contract.send(
                    sendOptions,"addCandidate", candidateKey.getAddress(), request.candidateInfo());

            return CandidateAdditionDTO.Response.builder()
                    .transactionHash(receiptData.getTransactionHash())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TokenDistributionDTO.Response distributeTokens(TokenDistributionDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        caver.wallet.add(request.deployKey());

        List<SingleKeyring> voters = new ArrayList<>();
        List<String> voterAddresses = new ArrayList<>();
        Map<String, SingleKeyring> voterMap = new HashMap<>();
        for (int count = 0; count < request.distributionCount(); count++) {
            SingleKeyring voter = KeyringFactory.generate();

            voters.add(voter);
            voterAddresses.add(voter.getAddress());
            voterMap.put(voter.getAddress(), voter);
        }

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(request.deployKey().getAddress());
            sendOptions.setGas(GAS_LIMIT);

            TransactionReceipt.TransactionReceiptData receiptData = contract.send(
                    sendOptions, "distributeTokens", voterAddresses);

            List<SingleKeyring> distributedKeys = receiptData.getLogs().stream()
                    .filter(log -> log.getTopics().get(0).equals(TOKEN_DISTRIBUTED_EVENT_SIGNATURE))
                    .map(KlayLogs.Log::getData)
                    .map(data -> "0x" + data.substring(data.length() - 40))
                    .map(address -> voterMap.get(address))
                    .toList();

            return TokenDistributionDTO.Response.builder()
                    .voterKeys(distributedKeys)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public StartVotingDTO.Response startVoting(StartVotingDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        caver.wallet.add(request.deployKey());

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(request.deployKey().getAddress());
            sendOptions.setGas(GAS_LIMIT);

            TransactionReceipt.TransactionReceiptData receiptData = contract.send(
                    sendOptions, "startVoting");

            return StartVotingDTO.Response.builder()
                    .transactionHash(receiptData.getTransactionHash())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public EndVotingDTO.Response endVoting(EndVotingDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        caver.wallet.add(request.deployKey());

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(request.deployKey().getAddress());
            sendOptions.setGas(GAS_LIMIT);

            TransactionReceipt.TransactionReceiptData receiptData = contract.send(
                    sendOptions, "endVoting");

            return EndVotingDTO.Response.builder()
                    .transactionHash(receiptData.getTransactionHash())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public VoteDTO.Response vote(VoteDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();
        SingleKeyring voterKey = KeyringFactory.createFromPrivateKey(request.voterKey());
        caver.wallet.add(voterKey);

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            SendOptions sendOptions = voteContractUtil.getDelegateSendOptions();
            sendOptions.setFrom(voterKey.getAddress());
            sendOptions.setGas(GAS_LIMIT);

            TransactionReceipt.TransactionReceiptData receiptData = contract.send(
                    sendOptions,"vote", request.candidate(), request.id());

            if (receiptData.getStatus().equals("0x0")) {
                throw new RuntimeException("투표 중 오류가 발생했습니다.");
            }

            return VoteDTO.Response.builder()
                    .transactionHash(receiptData.getTransactionHash())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CheckVotingUserDTO.Response checkVotingUser(CheckVotingUserDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            List<Type> callResponse = contract.call("isVoteUser", request.id());

            Boolean isVotingUser = ((Bool) callResponse.get(0)).getValue();

            return CheckVotingUserDTO.Response.builder()
                    .isVotingUser(isVotingUser)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public VoteDetailDTO.Response getVoteDetail(VoteDetailDTO.Request request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            List<Type> callResponse = contract.call("getVoteDetail");

            DynamicStruct voteDetail = (DynamicStruct) callResponse.get(0);

            String title = ((Utf8String) voteDetail.getValue().get(0)).getValue();
            String description = ((Utf8String) voteDetail.getValue().get(1)).getValue();
            List<Utf8String> candidatesRaw = ((DynamicArray<Utf8String>) voteDetail.getValue().get(2)).getValue();
            Boolean isVotingPeriod = ((Bool) voteDetail.getValue().get(3)).getValue();

            List<String> candidates = candidatesRaw.stream()
                    .map(candidateRaw -> candidateRaw.getValue())
                    .toList();

            return VoteDetailDTO.Response.builder()
                    .voteTitle(title)
                    .voteDescription(description)
                    .candidateOptions(candidates)
                    .isVotingPeriod(isVotingPeriod)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TurnoutRateDTO.OverallVoterResponse getOverallVoterTurnout(TurnoutRateDTO.OverallVoterRequest request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            List<Type> callResponse = contract.call("getOverallVoterTurnout");

            String turnoutPercent = ((Utf8String) callResponse.get(0)).getValue();

            return TurnoutRateDTO.OverallVoterResponse.builder()
                    .turnoutPercent(turnoutPercent)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TurnoutRateDTO.AllCandidateResponse getAllCandidateTurnout(TurnoutRateDTO.AllCandidateRequest request) {
        Caver caver = voteContractUtil.getCaverWithFeePayer();

        try {
            Contract contract = caver.contract.create(voteContractUtil.getVoteFunctionCall(), request.contractAddress());

            List<Type> callResponse = contract.call("getAllCandidatesTurnout");

            List<Type> candidateTurnoutsRaw = ((DynamicArray) callResponse.get(0)).getValue();

            List<TurnoutRateDTO.CandidateTurnout> candidateTurnouts = candidateTurnoutsRaw
                    .stream()
                    .map(elem -> (DynamicStruct) elem)
                    .map(struct -> TurnoutRateDTO.CandidateTurnout.builder()
                            .candidateOption(((Utf8String) struct.getValue().get(0)).getValue())
                            .turnoutPercent(((Utf8String) struct.getValue().get(1)).getValue())
                            .build())
                    .toList();

            return TurnoutRateDTO.AllCandidateResponse.builder()
                    .candidateTurnouts(candidateTurnouts)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
