package org.example.server.rpctypes;

import io.grpc.stub.StreamObserver;
import org.example.models.Account;
import org.example.models.TransferRequest;
import org.example.models.TransferResponse;
import org.example.models.TransferStatus;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    private StreamObserver<TransferResponse> transferRequestStreamObserver;

    public TransferStreamingRequest(StreamObserver<TransferResponse> transferRequestStreamObserver) {
        this.transferRequestStreamObserver = transferRequestStreamObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        int fromAccount = transferRequest.getFromAccount();
        int toAccount = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();
        int balance = AccountDatabase.getBalance(fromAccount);

        TransferStatus status = TransferStatus.FAILED;
        if ((balance >= amount ) && (fromAccount != toAccount)){
            AccountDatabase.deductBalance(fromAccount, amount);
            AccountDatabase.addBalance(toAccount, amount);
            status = TransferStatus.SUCCESS;
        }
        Account fromAccountNum = Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDatabase.getBalance(fromAccount)).build();
        Account toAccountNum = Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDatabase.getBalance(toAccount)).build();
        TransferResponse transferResponse = TransferResponse.newBuilder().addAccounts(fromAccountNum).addAccounts(toAccountNum).build();

        this.transferRequestStreamObserver.onNext(transferResponse);



    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.transferRequestStreamObserver.onCompleted();
        AccountDatabase.printAccountDetails();

    }
}
