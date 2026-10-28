package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unused")
@WebListener
public class RequestAttributeListener extends Loggable implements ServletRequestAttributeListener {

  public RequestAttributeListener() {
    logger.me(this);
  }

  /*
   * ServletRequestAttributeListener
   */
  @Override
  public void attributeAdded(ServletRequestAttributeEvent event) {
    HttpServletRequest req = (HttpServletRequest) event.getServletRequest();

    logger.get().debug("attributeAdded {0} {1} {2} {3} {4} {5} {6}",
        req.getMethod(),
        req.getRequestURI(),
        req.getQueryString(),
        req.getRemoteAddr(),
        req.getHeader("User-Agent"),
        req.getContextPath(),
        req.getServletPath());

    logger.get().debug(event.getName(), event.getValue());
  }

  @Override
  public void attributeRemoved(ServletRequestAttributeEvent event) {
    logger.get().debug("{0} {1} {2}", event.getName(), event.getValue());
  }

  @Override
  public void attributeReplaced(ServletRequestAttributeEvent event) {
    logger.get().debug("{0} {1}",event.getName(), event.getValue());
  }
}
