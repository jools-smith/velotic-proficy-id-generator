package com.revenera.gcs.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HandyBag extends LinkedHashMap<String, Object> {
  HandyBag parent;

  public HandyBag() {
    this.parent = this;
  }

  public HandyBag(final HandyBag parent) {
    this.parent = parent;
  }

  @JsonIgnore
  public HandyBag beginSection(final String key) {
    final HandyBag bag = new HandyBag(this);
    put(key, bag);
    return bag;
  }

  @JsonIgnore
  public HandyBag with(final String key, final Object value) {
    put(key, value);
    return this;
  }

  @JsonIgnore
  @SuppressWarnings("unused")
  public HandyBag with(final String key, final Object... values) {
    put(key, Arrays.asList(values));

    return this;
  }

  @JsonIgnore
  public HandyBag withJoined(final String key, final Object... values) {
    put(key, Stream.of(values)
        .map(Object::toString)
        .collect(Collectors.joining("|")));

    return this;
  }

  @JsonIgnore
  public HandyBag with(final String key, final HandyBag bag) {
    put(key, bag);
    bag.parent = this;
    return this;
  }

  @JsonIgnore
  public HandyBag endSection() {
    return this.parent;
  }

  @JsonIgnore
  public HandyBag endSectionAndBegin(final String key) {
    return endSection().beginSection(key);
  }

  @SuppressWarnings("unused")
  public Map<String, Object> getElements() {
    return this;
  }
}
