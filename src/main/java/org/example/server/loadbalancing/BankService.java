package org.example.server.loadbalancing;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.*;
import org.example.server.rpctypes.AccountDatabase;
import org.example.server.loadbalancing.CashStreamingRequest;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        System.out.println(
                "Received request for account: " + accountNumber
        );
        Balance balance = Balance.newBuilder()
                        .setAmount(AccountDatabase.getBalance(accountNumber))
                                .build();
        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
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
//            Thread.sleep(1000);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }
}
