package com.revenera.gcs.transaction;

import com.revenera.gcs.utils.Frame;

import java.time.Instant;
import java.util.Map;

public interface TransactionManagement {
  boolean hasTransactions();
  Map.Entry<Object, Object> pollTransactions();
  void submitTransaction(
      final Frame frame,
      final Instant start,
      final Object request,
      final Object response,
      final Object payload);
}
