package org.example.client.rpctypes;

import io.grpc.stub.StreamObserver;
import org.example.models.Money;

import java.util.concurrent.CountDownLatch;

public class MoneyStreamingResponse implements StreamObserver<Money> {
    private CountDownLatch latch;

    public MoneyStreamingResponse(CountDownLatch latch){
        this.latch = latch;
    }
    @Override
    public void onNext(Money money) {
        System.out.println(
                "Received async: "+ money.getValue()
        );
//        this.latch.countDown();
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(
                "Error: "+ throwable.getMessage()
        );
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server done!!");
        this.latch.countDown();
    }

}
