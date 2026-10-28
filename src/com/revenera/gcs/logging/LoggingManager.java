package com.revenera.gcs.logging;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class LoggingManager implements LoggingManagement, LoggingQueue {

  final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  final AtomicReference<Level> loggingLevel = new AtomicReference<>(Level.TRACE);
  final AtomicBoolean loggingEcho = new AtomicBoolean(false);

  // LoggingManagement
  @Override
  public boolean willLog(final Level level) {
    return loggingLevel.get().compare(level) >= 0;
  }

  @Override
  public void setLevel(final Level level) {
    loggingLevel.getAndSet(level);
  }

  @Override
  public Level getLevel() {
    return loggingLevel.get();
  }

  @Override
  public void setEcho(boolean echo) {
    loggingEcho.set(echo);
  }

  @Override
  public boolean willEcho() {
    return loggingEcho.get();
  }

  // LoggingQueue
  @Override
  public boolean isEmpty() {
    return messageQueue.isEmpty();
  }

  @Override
  public String peekMessage() {
    return messageQueue.peek();
  }

  @Override
  public String popMessage() {
    return messageQueue.poll();
  }

  @Override
  public String postMessage(String message) {
    messageQueue.offer(message);
    return message;
  }
}
