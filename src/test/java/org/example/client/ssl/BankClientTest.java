package org.example.client.ssl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.StreamObserver;
import org.example.client.rpctypes.BalanceStreamObserver;
import org.example.client.rpctypes.MoneyStreamingResponse;
import org.example.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
    BankServiceGrpc.BankServiceBlockingStub blockingStub;
    BankServiceGrpc.BankServiceStub bankServiceStub;
    @BeforeAll
    public void setup() throws SSLException {
        File certFile  =  new File("C:\\Users\\arnab.shil\\IdeaProjects\\grpc-into\\src\\main\\ssl\\ca.cert.pem");
        System.out.println(
                certFile.toString()
        );
        SslContext sslContext = GrpcSslContexts.forClient()
                .trustManager(certFile).build();
       ManagedChannel managedChannel = NettyChannelBuilder.forAddress("localhost", 6565)
               .sslContext(sslContext)
//                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
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

    @Test
    public void withdrawTest(){
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(7).setAmount(37).build();
        this.blockingStub.withdraw(withdrawRequest).forEachRemaining(money -> System.out.println("Received" + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(8).setAmount(50).build();
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
