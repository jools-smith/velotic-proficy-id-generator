package com.revenera.gcs.logging;

public enum Level {
  NONE(0), ERROR(1), WARN(2), INFO(4), FULL(8), TRACE(16), DEBUG(32), ALL(64);

  Level(int value) {
    this.value = value;
  }
  private final int value;

  @SuppressWarnings("unused")
  public int getValue() {
    return value;
  }

  public int compare(final Level level) {
    return this.value - level.value;
  }
}
