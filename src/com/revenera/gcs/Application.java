package com.revenera.gcs;


import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.Loggable;
import com.revenera.gcs.utils.Serializer;
import com.revenera.gcs.webservices.ServiceProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * The root of the service, registered as a listener will set stuff up when the context is initialized
 */
@WebListener
public class Application extends Loggable implements ServletContextListener {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  static {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      ExecutionContext.getLoggingManager().setLevel(Level.valueOf(ExecutionContext
          .getApplicationProperties()
          .getLoggingLevel()
          .toUpperCase()));

      ExecutionContext.getLoggingManager().setEcho(ExecutionContext
          .getApplicationProperties()
          .getLoggingEcho());
    }
  }

  private final Housekeeper housekeeper = new Housekeeper();

  public Application() {

    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      logger.me(this);

      try {
        final ApplicationProperties props = ExecutionContext.getApplicationProperties();

        logger.get().info("version {0} ({1}) {2} log-level {3}",
            props.getVersion(),
            props.getRelease(),
            props.getTimestamp(),
            ExecutionContext.getLoggingManager().getLevel());
      }
      catch (final Throwable t) {
        logger.exception(t);
      }
    }
  }

  private void serializeToLogPath(final String filename, final List<String> content) throws IOException {
    //noinspection unused

    if (ExecutionContext.getLoggingManager().willEcho()) {
      final String root = ExecutionContext.getLogPath().toRealPath().toString();

      if (Files.exists(Paths.get(root))) {
        FileUtils.writeLines(
            Paths.get(root, filename).toAbsolutePath().toFile().getAbsoluteFile(),
            content,
            true);
      }
    }

  }

  private void polling() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        while (ExecutionContext.getTransactionManager().hasTransactions()) {
          //need to depopulate the queue even if not serializing
          final Map.Entry<Object, Object> content = ExecutionContext.getTransactionManager().pollTransactions();

          if (content != null) {
            serializeToLogPath(content.getKey() + ".yaml",
                Collections.singletonList(
                    Serializer.safeSerializeYaml(content.getValue())));
          }
        }
      }
      catch (final Throwable t) {
        logger.exception(t);
      }
    }
  }

  private void logging() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        final List<String> messages = new ArrayList<>();

        while (!ExecutionContext.getLoggingManager().isEmpty()) {
          messages.add(ExecutionContext.getLoggingManager().popMessage());
        }

        if (!messages.isEmpty()) {
          serializeToLogPath(LocalDate.now() + ".revenera.log", messages);
        }
      }
      catch (final Throwable t) {
        System.out.printf("EXCEPTION %s %s%n", t.getClass().getName(), t.getMessage());
        logger.exception(t);
      }
    }
  }

  private void housekeeping() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        logger.yaml(Level.DEBUG, ExecutionContext.getRecordsBag());
      }
      catch (final Throwable t) {
        logger.exception(t);
      }
    }
  }

  private void listAttributes(final ServletContextEvent event) {
    final Enumeration<String> itt = event.getServletContext().getAttributeNames();
    while (itt.hasMoreElements()) {
      final String name = itt.nextElement();
      //kludge
      if (!name.equals("org.apache.catalina.jsp_classpath")) {
        final Object value = event.getServletContext().getAttribute(name);

        logger.get().debug("{0} {1}", name, value != null ? value.toString() : "null");
      }
    }
  }

//  private void processServiceImplementor(final Class<?> type) throws InstantiationException, IllegalAccessException {
//
//    final WebService annotation = type.getAnnotation(WebService.class);
//
//    if (ServiceProperties.class.isAssignableFrom(type)) {
//      final ServiceProperties serviceImplementor = (ServiceProperties) type.newInstance();
//
//      serviceImplementor.set(
//          annotation.serviceName(),
//          annotation.name(),
//          annotation.endpointInterface());
//
//      ExecutionContext.getServiceManager().addService(serviceImplementor);
//    }
//  }


  private void configureHousekeeping() {
    logger.in();

    this.housekeeper.initialize();

    this.housekeeper.start(Timers.housekeeping, this::housekeeping, 1, ExecutionContext.getApplicationProperties().getHousekeepingFrequency(), TimeUnit.MINUTES);
    this.housekeeper.start(Timers.logging, this::logging, 1, 1, TimeUnit.SECONDS);
    this.housekeeper.start(Timers.polling, this::polling, 500, 500, TimeUnit.MILLISECONDS);
  }
  /*
   * ServletContextListener
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    logger.in();
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        Beans.setResourcesRoot(event.getServletContext().getRealPath("/WEB-INF"));

        listAttributes(event);

        configureHousekeeping();
      }
      catch (final Throwable t) {
        logger.exception(t);
      }
      finally {
        logger.yaml(Level.DEBUG, ExecutionContext.getApplicationData());
        logger.out();
      }
    }
  }

  private enum Timers {
    housekeeping, logging, polling
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    logger.in();
    try {
      this.housekeeper.cancel(Timers.polling);
      this.housekeeper.cancel(Timers.logging);
      this.housekeeper.cancel(Timers.housekeeping);

      this.housekeeper.shutdown();
    }
    finally {
      logger.out();
    }
  }
}
