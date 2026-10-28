package com.revenera.gcs.logging;

public interface LoggingQueue {

  boolean isEmpty();

  String peekMessage();

  String popMessage();
  
  String postMessage(String message);
}
