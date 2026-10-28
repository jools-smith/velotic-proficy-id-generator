package com.revenera.gcs.webservices;

public interface IStringGenerator {
  String generate(String charset, int length);
  String generate(String charset, String separator, int groupCount, int groupLength);
}
