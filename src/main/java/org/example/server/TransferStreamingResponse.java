package org.example.server;

import io.grpc.stub.StreamObserver;
import org.example.models.TransferResponse;

import java.util.concurrent.CountDownLatch;

public class TransferStreamingResponse implements StreamObserver<TransferResponse> {

    private CountDownLatch latch;

    public TransferStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse transferResponse) {
        System.out.println(transferResponse.getStatus());
        transferResponse.getAccountsList()
                .stream()
                .map(account -> account.getAccountNumber() + ": " + account.getAmount())
                .forEach(System.out::println);
        System.out.println("------------------");
    }

    @Override
    public void onError(Throwable throwable) {
    this.latch.countDown();
    }

    @Override
    public void onCompleted() {
    this.latch.countDown();
    System.out.println("Done!!");
    }
}
