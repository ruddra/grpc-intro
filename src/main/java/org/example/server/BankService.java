package org.example.server;

import io.grpc.stub.StreamObserver;
import org.example.models.Balance;
import org.example.models.BalanceCheckRequest;
import org.example.models.BankServiceGrpc;

public class BankService  extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                        .setAmount(AccountDatabase.getBalance(accountNumber))
                                .build();
        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }
}
