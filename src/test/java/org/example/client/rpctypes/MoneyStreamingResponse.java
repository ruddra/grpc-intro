package org.example.client.rpctypes;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.client.metadata.ClientConstants;
import org.example.models.Money;
import org.example.models.WithdrawalError;

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

        Metadata metadata = Status.trailersFromThrowable(throwable);
        WithdrawalError withdrawalError = metadata.get(ClientConstants.WITHDRAWAL_ERROR_KEY);

        System.out.println(
                "Error: "+ withdrawalError.getAmount() + ": " + withdrawalError.getErrorMessage()
        );
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server done!!");
        this.latch.countDown();
    }

}
