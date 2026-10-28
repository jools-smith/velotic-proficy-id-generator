package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SuppressWarnings("unused")
@WebListener
public class RequestListener extends Loggable implements ServletRequestListener {

  public RequestListener() {
    logger.me(this);
  }

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void requestDestroyed(ServletRequestEvent event) {
    //noinspection CodeBlock2Expr
    Optional.ofNullable((HttpServletRequest) event.getServletRequest()).ifPresent(req -> {
      logger.get().debug("destroyed {0} {1} {2} ",
          req.getMethod(),
          req.getScheme(),
          req.getRequestURI());
    });
  }

  @Override
  public void requestInitialized(ServletRequestEvent event) {
    //noinspection CodeBlock2Expr
    Optional.ofNullable((HttpServletRequest) event.getServletRequest()).ifPresent(req -> {
      logger.get().debug("initialized {0} {1} {2} {3} {4} {5} {6}",
          req.getMethod(),
          req.getScheme(),
          req.getRequestURI(),
          req.getQueryString(),
          req.getHeader("User-Agent"),
          req.getContentType(),
          req.getProtocol());
    });
  }
}

