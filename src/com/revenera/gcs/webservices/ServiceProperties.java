package com.revenera.gcs.webservices;

import com.revenera.gcs.logging.Loggable;

public class ServiceProperties extends Loggable implements ServiceImplementor {
  String serviceName;
  String localName;
  String endpointName;

  @Override
  public void set(String serviceName, String localName, String endpointName) {
    this.serviceName = serviceName;
    this.localName = localName;
    this.endpointName = endpointName;
  }

  @Override
  public String getServiceName() {
    return this.serviceName;
  }

  @Override
  public String getLocalName() {
    return this.localName;
  }

  @Override
  public String getEndpointName() {
    return this.endpointName;
  }
}
