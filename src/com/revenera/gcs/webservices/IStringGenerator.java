package com.revenera.gcs.webservices;

public interface IStringGenerator {
  enum Type {
    Entitlement, Activation, Maintenance, Fulfilment, Consolidated, Web, Custom
  }
  String generate(String charset, int length);

  String generateGroups(String charset, String separator, int groupCount, int groupLength);

  String generateStandard(Type type);
}
