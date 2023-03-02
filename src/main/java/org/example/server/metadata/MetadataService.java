package org.example.server.metadata;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.*;
import org.example.server.loadbalancing.CashStreamingRequest;
import org.example.server.rpctypes.AccountDatabase;

import java.util.concurrent.TimeUnit;

public class MetadataService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = AccountDatabase.getBalance(accountNumber);
        UserRole role = ServerConstants.CTX_USER_ROLE.get();
        UserRole role1 = ServerConstants.CTX_USER_ROLE1.get();
        amount = UserRole.PRIME.equals(role) ? amount : amount - 10;
        Balance balance = Balance.newBuilder()
                        .setAmount(amount)
                .build();
        System.out.println(
                role + " <> " + role1
        );
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
            if (!Context.current().isCancelled()){
                responseObserver.onNext(money);
                System.out.println("Delivered " + (i+1)*10 + "dollars");
                AccountDatabase.deductBalance(accountNumber, 10);
            } else{
                break;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }
}
