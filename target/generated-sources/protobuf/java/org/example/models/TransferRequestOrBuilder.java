// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: transfer-server.proto

package org.example.models;

public interface TransferRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:TransferRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 from_account = 1;</code>
   * @return The fromAccount.
   */
  int getFromAccount();

  /**
   * <code>int32 to_account = 2;</code>
   * @return The toAccount.
   */
  int getToAccount();

  /**
   * <code>int32 amount = 3;</code>
   * @return The amount.
   */
  int getAmount();
}
