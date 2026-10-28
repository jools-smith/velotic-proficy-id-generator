package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@SuppressWarnings("unused")
@WebListener
public class SessionAttributeListener extends Loggable implements HttpSessionAttributeListener {

  public SessionAttributeListener() {
    logger.me(this);
  }

  /*
   * HttpSessionAttributeListener
   */
  @Override
  public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }
}
