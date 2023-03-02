package org.example.client.loadbalancing;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.client.rpctypes.BalanceStreamObserver;
import org.example.client.rpctypes.MoneyStreamingResponse;
import org.example.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxTestClient {

    BankServiceGrpc.BankServiceBlockingStub blockingStub;
    BankServiceGrpc.BankServiceStub bankServiceStub;
    @BeforeAll
    public void setup(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8585)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest() {
        for (int i = 1; i < 11; i++) {

            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();
            Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

            System.out.println(
                    "Recieved: " + balance.getAmount()
            );
        }

    }

    @Test
    public void withdrawTest(){
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(7).setAmount(40).build();
        this.blockingStub.withdraw(withdrawRequest).forEachRemaining(money -> System.out.println("Received" + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(8).setAmount(40).build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(countDownLatch));
        countDownLatch.await();
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest>  streamObserver = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
            streamObserver.onNext(depositRequest);
        }
        streamObserver.onCompleted();
        latch.await();
    }
}
