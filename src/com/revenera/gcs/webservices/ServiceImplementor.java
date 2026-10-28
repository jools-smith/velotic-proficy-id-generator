package com.revenera.gcs.webservices;

public interface ServiceImplementor {

  void set(String serviceName, String localName, String endpointName);

  String getServiceName();

  String getLocalName();

  String getEndpointName();
}
