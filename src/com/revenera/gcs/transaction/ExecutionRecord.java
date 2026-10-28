package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.utils.Frame;

import java.time.Duration;
import java.time.Instant;

@JsonPropertyOrder({
    "method",
    "totalDuration",
    "count",
    "meanLatency",
    "timeSinceUpdate"
})
public class ExecutionRecord {
  private final String method;
  private final int line;
  private Duration duration;
  private long count;
  private Instant updated;

  private static String makeKey(final Frame element) {
    return String.format("%s.%s",
        element.getSimpleClassName(),
        element.getMethodName());
  }

  ExecutionRecord(final Frame element) {
    this.method = makeKey(element);
    this.line = element.getLineNumber();
    this.duration = Duration.ZERO;
    this.count = 0;
    this.updated = Instant.now();
  }

  @JsonIgnore
  public boolean matches(final Frame element) {
    return this.method.equals(makeKey(element));
  }

  @JsonIgnore
  ExecutionRecord touch(final Duration duration) {
    this.duration = this.duration.plus(duration);
    this.count++;
    this.updated = Instant.now();
    return this;
  }

  @JsonIgnore
  public Instant getUpdated() {
    return this.updated;
  }

  public String getMethod() {
    return this.method;
  }

  public int getLine() {
    return this.line;
  }

  public double getTotalDuration() {
    return this.duration.toNanos() / 1_000_000_000.0;
  }

  @SuppressWarnings("unused")
  public long getCount() {
    return this.count;
  }
  @SuppressWarnings("unused")
  public double getMeanLatency() {
    return getTotalDuration() / this.count;
  }

  @SuppressWarnings("unused")
  public String getTimeSinceUpdate() {
    return Duration.between(this.updated, Instant.now()).toString();
  }
}
