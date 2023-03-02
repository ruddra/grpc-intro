package org.example.client.metadata;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import org.example.client.rpctypes.BalanceStreamObserver;
import org.example.client.rpctypes.MoneyStreamingResponse;
import org.example.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataClientTest {
    BankServiceGrpc.BankServiceBlockingStub blockingStub;
    BankServiceGrpc.BankServiceStub bankServiceStub;
    @BeforeAll
    public void setup(){
       ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
               .intercept(new MetadataInterceptors())
               .intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getClientToken()))
               .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest() {
       BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        for (int i = 0; i < 20; i++) {
           try {
               int random = ThreadLocalRandom.current().nextInt(1,4);
               System.out.println("Random" + random);
               Balance balance = this.blockingStub
                       .withCallCredentials(new UserSessionToken("user-secret-" + random + ":std"))
                       .getBalance(balanceCheckRequest);

               System.out.println(
                       "Recieved: " + balance.getAmount()
               );
           } catch (StatusRuntimeException e){
               System.out.println("Error"+ e.toString());
           }
        }
    }

    @Test
    public void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(7).setAmount(40).build();
        try {
            this.blockingStub.withdraw(withdrawRequest).forEachRemaining(money -> System.out.println("Received" + money.getValue()));

        } catch (StatusRuntimeException e){
            System.out.println("Error");
        }
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
            DepositRequest depositRequest = DepositRequest.newBuilder().setAmount(8).setAmount(10).build();
            streamObserver.onNext(depositRequest);
        }
        streamObserver.onCompleted();
        latch.await();
    }


}
