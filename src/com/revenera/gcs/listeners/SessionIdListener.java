package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;

@SuppressWarnings("unused")
@WebListener
public class SessionIdListener extends Loggable implements HttpSessionIdListener {

  public SessionIdListener() {
    logger.me(this);
  }

  /*
   * HttpSessionIdListener
   */
  @Override
  public void sessionIdChanged(HttpSessionEvent httpSessionEvent, String s) {
    logger.in();
  }
}
