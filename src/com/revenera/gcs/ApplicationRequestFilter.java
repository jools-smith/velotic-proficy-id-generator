package com.revenera.gcs;

import com.revenera.gcs.logging.Loggable;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@SuppressWarnings("unused")
@WebFilter(urlPatterns = "/services/*")
public class ApplicationRequestFilter extends Loggable implements Filter {
//  private static final LoggingFactory logger = LoggingFactory.create(ApplicationRequestFilter.class);
  
  public ApplicationRequestFilter() {
    logger.me(this);
  }

  @Override
  public void init(final FilterConfig config) {
    logger.get().debug(config.getFilterName(), config.getServletContext().getMajorVersion(), config.getServletContext().getMinorVersion());

    final Enumeration<String> itt = config.getInitParameterNames();
    while (itt.hasMoreElements()) {
      final String key = itt.nextElement();

      logger.get().info(key, config.getInitParameter(key));
    }
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {

    final HttpServletRequest req = (HttpServletRequest) request;

    final HttpServletResponse resp = (HttpServletResponse) response;

    logger.get().info("request {0} {1} {2} {3} {4}",
        req.getMethod(),
        req.getRequestURI(),
        req.getContentType(),
        req.getQueryString(),
        resp.getStatus());

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    logger.in();
  }
}
