package com.revenera.gcs.logging;

public abstract class Loggable {
  protected final LoggingFactory logger = LoggingFactory.create(this.getClass());

}
