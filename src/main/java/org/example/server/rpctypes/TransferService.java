package org.example.server.rpctypes;

import io.grpc.stub.StreamObserver;
import org.example.models.TransferRequest;
import org.example.models.TransferResponse;
import org.example.models.TransferServiceGrpc;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {
    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamingRequest(responseObserver);
    }
}
