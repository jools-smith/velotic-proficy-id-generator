package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@SuppressWarnings("unused")
@WebListener
public class SessionListener extends Loggable implements HttpSessionListener {

  public SessionListener() {
    logger.me(this);
  }

  /*
   * HttpSessionListener
   */
  @Override
  public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    logger.in();
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    logger.in();
  }
}
