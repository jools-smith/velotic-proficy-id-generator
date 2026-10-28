package com.revenera.gcs;


import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.logging.LoggingManager;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.webservices.IStringGenerator;
import com.revenera.gcs.webservices.SimpleStringGenerator;
import org.apache.commons.lang3.time.StopWatch;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Beans {
  private static final LoggingFactory logger = LoggingFactory.create(Beans.class);

  final static StopWatch stopwatch = new StopWatch();
  final static ApplicationProperties applicationProperties;
  final static DiagnosticsFactory diagnosticsFactory;
  final static LoggingManager loggingManager;
  final static IStringGenerator stringGenerator;

  static String web_inf;

  static {
    try {
      loggingManager = new LoggingManager();
      applicationProperties = new ApplicationProperties();
      diagnosticsFactory = new DiagnosticsFactory();
      stringGenerator = new SimpleStringGenerator();

      stopwatch.start();
    }
    finally {
      logger.get().info("Beans initialized...");
    }
  }

  public static void setResourcesRoot(final String value) {
    web_inf = value;
    logger.get().info("resources root set ", getResourcePath().toAbsolutePath().toString());
  }

  public static Path getResourcePath(final String... parts) {
    return Paths.get(web_inf, parts);
  }

  public static Path getLogPath() {
    return getResourcePath("logs");
  }

}
