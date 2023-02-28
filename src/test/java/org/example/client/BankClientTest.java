package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.models.Balance;
import org.example.models.BalanceCheckRequest;
import org.example.models.BankServiceGrpc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
    BankServiceGrpc.BankServiceBlockingStub blockingStub;
    @BeforeAll
    public void setup(){
       ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    public void balanceTest() {
       BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(123)
                .build();
        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

        System.out.println(
                "Recieved: " + balance.getAmount()
        );
    }
}
