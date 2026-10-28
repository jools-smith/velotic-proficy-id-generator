package com.revenera.gcs.logging;

public interface LoggingManagement {
  boolean willLog(Level level);

  void setLevel(Level level);

  Level getLevel();

  void setEcho(boolean echo);

  boolean willEcho();
}
