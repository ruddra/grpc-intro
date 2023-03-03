package org.example.server.ssl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.example.server.rpctypes.TransferService;

import java.io.File;
import java.io.IOException;

public class GrpcServer {


    public static void main(String[] args) throws IOException, InterruptedException {
        var sslContextBuilder = GrpcSslContexts.configure(
                SslContextBuilder.forServer(
                        new File("C:\\Users\\arnab.shil\\IdeaProjects\\grpc-into\\src\\main\\ssl\\localhost.crt"),
                        new File("C:\\Users\\arnab.shil\\IdeaProjects\\grpc-into\\src\\main\\ssl\\localhost.pem")
                )
        ).build();
        Server server = NettyServerBuilder.forPort(6565)
                .sslContext(sslContextBuilder)
                .addService(new BankService())
                .addService(new TransferService())
                .build();

        server.start();

        server.awaitTermination();
    }
}
