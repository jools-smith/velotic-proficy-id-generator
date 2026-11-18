package com.revenera.gcs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProficyConfiguration {
  @JsonProperty
  String version;

  @JsonProperty
  String charset;

  @JsonProperty
  String separator;

  @JsonProperty
  int groupCount;

  @JsonProperty
  int groupLength;

  @JsonProperty
  List<String> productLines;

  public String getVersion() {
    return version;
  }

  public String getCharset() {
    return charset;
  }

  public String getSeparator() {
    return separator;
  }

  public int getGroupCount() {
    return groupCount;
  }

  public int getGroupLength() {
    return groupLength;
  }

  public List<String> getProductLines() {
    return productLines;
  }
}
