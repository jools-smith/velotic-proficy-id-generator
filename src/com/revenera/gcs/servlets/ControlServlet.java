package com.revenera.gcs.servlets;

import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Serializer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

@WebServlet("/control/*")
public class ControlServlet extends HttpServlet {
  private static final LoggingFactory logger = LoggingFactory.create(ControlServlet.class);

  enum Resources {
    STATUS("/status"), HEALTH("/health"), PROPERTIES("/properties"), LOGGING_LEVEL("/logging/level"), LOGGING_ECHO("/logging/echo"), INVALID("");
    private final String path;

    Resources(final String path) {
      this.path = path;
    }

    static Resources fromPath(final String path) {
      for (final Resources r : Resources.values()) {
        if (r.path.equals(path)) {
          return r;
        }
      }

      return INVALID;
    }
  }

  public ControlServlet() {
    super();
    logger.me(this);
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        logger.get().debug("Received GET request {0} {1}", req.getRequestURI(), req.getPathInfo());

        resp.setContentType("application/json");

        switch (Resources.fromPath(req.getPathInfo())) {
          case STATUS:
            getStatus(req, resp);
            break;
          case HEALTH:
            getHealth(req, resp);
            break;
          case PROPERTIES:
            getProperties(req, resp);
            break;
          case LOGGING_ECHO:
            getLoggingEcho(req, resp);
            break;
          case LOGGING_LEVEL:
            getLoggingLevel(req, resp);
            break;
          default:
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
      }
      catch (Exception e) {
        logger.exception(e);
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        logger.get().debug("Received POST request {0} {1}", req.getRequestURI(), req.getPathInfo());

        resp.setContentType("application/json");

        switch (Resources.fromPath(req.getPathInfo())) {
          case LOGGING_ECHO:
            postLoggingEcho(req, resp);
            break;
          case LOGGING_LEVEL:
            postLoggingLevel(req, resp);
            break;
          default:
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
      }
      catch (Exception e) {
        logger.exception(e);
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
  }

  @Override
  public String getServletName() {
    return "Revenera GCS Control Servlet";
  }

  @Override
  public void destroy() {
    logger.in();
    super.destroy();
  }

  @Override
  public String getServletInfo() {
    return "Revenera GCS Control Servlet v1.0";
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    logger.in();
    super.init(config);
  }

  @Override
  public void init() throws ServletException {
    logger.in();
    super.init();
  }

  /**
   * IMPLEMENTOR METHODS
   **/

  private void getHealth(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    Serializer.serializeJsonIndented(resp.getWriter(), ExecutionContext.getApplicationData());
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void getProperties(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    Serializer.serializeJsonIndented(resp.getWriter(), ExecutionContext.getApplicationProperties());
    resp.setStatus(HttpServletResponse.SC_OK);
  }


  private void getStatus(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    final HandyBag bag =  new HandyBag()
        .beginSection("status")
        .with("level", ExecutionContext.getLoggingManager().getLevel())
        .with("echo", ExecutionContext.getLoggingManager().willEcho());

    Serializer.serializeJsonIndented(resp.getWriter(), bag);
    resp.setStatus(HttpServletResponse.SC_OK);
  }


  private void getLoggingLevel(final HttpServletRequest request, final HttpServletResponse resp) throws IOException {
    Serializer.serializeJsonIndented(resp.getWriter(),
        new AbstractMap.SimpleEntry<>("level", ExecutionContext.getLoggingManager().getLevel()));
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void getLoggingEcho(final HttpServletRequest request, final HttpServletResponse resp) throws IOException {
    Serializer.serializeJsonIndented(resp.getWriter(),
        new AbstractMap.SimpleEntry<>("echo", ExecutionContext.getLoggingManager().getLevel()));
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void postLoggingLevel(final HttpServletRequest request, final HttpServletResponse resp) throws IOException {

    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    Stream.of(request.getParameterMap().get("level")).findFirst().ifPresent(level -> {
      ExecutionContext.getLoggingManager().setLevel(Level.valueOf(level));

      resp.setStatus(HttpServletResponse.SC_OK);
    });

    getLoggingLevel(request, resp);
  }

  private void postLoggingEcho(final HttpServletRequest request, final HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    Stream.of(request.getParameterMap().get("echo")).findFirst().ifPresent(echo -> {
      ExecutionContext.getLoggingManager().setEcho(Boolean.parseBoolean(echo));

      resp.setStatus(HttpServletResponse.SC_OK);
    });

    getLoggingEcho(request, resp);
  }
}