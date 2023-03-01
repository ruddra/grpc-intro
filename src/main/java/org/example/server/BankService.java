package org.example.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.*;

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

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) throws InterruptedException {
//        super.withdraw(request, responseObserver);
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount();
        int balance = AccountDatabase.getBalance(accountNumber);
        if (balance < amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Not enough money");
            responseObserver.onError(status.asRuntimeException());
            return;
        }
        for (int i=0; i<(amount/10);i++){
            Money money = Money.newBuilder().setValue(10).build();
            responseObserver.onNext(money);
            AccountDatabase.deductBalance(accountNumber, 10);
            Thread.sleep(1000);
        }
        responseObserver.onCompleted();
    }

}
