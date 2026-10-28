package com.revenera.gcs;

import java.util.List;

public abstract class ProficyConfiguration {
  String charset;
  String separator;
  int groupCount;
  int groupLength;
  List<String> productLines;


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
