package com.example.khuvote.contract;

import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class VoteContractUtil {

    private String rpcUrl;
    private String voteBytecode;
    private String voteFunctionCall;
    private SingleKeyring feePayer;

    public VoteContractUtil(
            @Value("${kaia.rpc-url}") String rpcUrl,
            @Value("file:./voteBytecode") Resource bytecodeResource,
            @Value("file:./voteFunctionCall.json") Resource functionCallResource,
            @Value("file:./feePayerKey") Resource feepayerKeyResource
    ) throws IOException {
        this.rpcUrl = rpcUrl;
        this.voteBytecode = Files.readString(bytecodeResource.getFile().toPath());
        this.voteFunctionCall = Files.readString(functionCallResource.getFile().toPath());

        String privateKey = Files.readString(feepayerKeyResource.getFile().toPath());
        this.feePayer = KeyringFactory.createFromPrivateKey(privateKey);
    }

    public Caver getCaverWithFeePayer() {
        Caver caver = new Caver(rpcUrl);
        caver.wallet.add(feePayer);

        return caver;
    }

    public SendOptions getDelegateSendOptions() {
        SendOptions sendOptions = new SendOptions();
        sendOptions.setFeeDelegation(true);
        sendOptions.setFeePayer(feePayer.getAddress());

        return sendOptions;
    }

    public String getVoteBytecode() {
        return voteBytecode;
    }

    public String getVoteFunctionCall() {
        return voteFunctionCall;
    }
}
