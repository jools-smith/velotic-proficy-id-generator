package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

@SuppressWarnings("unused")
@WebListener
public class ContextAttributeListener extends Loggable implements ServletContextAttributeListener {


  public ContextAttributeListener() {
    logger.me(this);
  }

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void attributeAdded(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }


}
