package com.revenera.gcs.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.logging.Loggable;

@JsonPropertyOrder({
    "className",
    "methodName",
    "lineNumber"
})
public class Frame extends Loggable {
  public enum Depth {
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),;

    final short depth;
    Depth(final int value) {
      this.depth = (short) value;
    }
  }

//  private static final LoggingFactory logger = LoggingFactory.create(Frame.class);

  private final StackTraceElement frame;

  @SuppressWarnings("unused")
  public Frame() {
    this.frame = Thread.currentThread().getStackTrace()[2];
  }

  public Frame(final Depth depth) {
    this.frame = Thread.currentThread().getStackTrace()[depth.depth + 2];
  }

  public Frame(final Class<?> type) {
    this.frame = getFrame(type.getCanonicalName());
  }

  private static StackTraceElement getFrame(final String FQDN) {

    for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
      if (stackTraceElement.getClassName().equals(FQDN)) {
        return stackTraceElement;
      }
    }
    throw new IllegalArgumentException("No frame found for " + FQDN);
  }

  @JsonIgnore
  @SuppressWarnings("unused")
  public String getClassName() {
    return frame.getClassName();
  }


  public String getMethodName() {
    return frame.getMethodName();
  }

  @JsonIgnore
  @SuppressWarnings("unused")
  public String getFileName() {
    return frame.getFileName();
  }

  @SuppressWarnings("unused")
  public int getLineNumber() {
    return frame.getLineNumber();
  }

  @JsonIgnore
  @SuppressWarnings("unused")
  public String getLocation() {
    return getSimpleClassName() + "." + getMethodName() + "." + getLineNumber();
  }

  @JsonProperty("className")
  public String getSimpleClassName() {
    String classname = frame.getClassName();
    try {
      classname = Class.forName(frame.getClassName()).getSimpleName();
    }
    catch (final ClassNotFoundException e) {
      logger.exception(e);
    }
    return classname;
  }
}
