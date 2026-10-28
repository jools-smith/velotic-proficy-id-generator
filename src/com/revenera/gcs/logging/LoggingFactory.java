package com.revenera.gcs.logging;

import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.Serializer;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.stream.Collectors;

public class LoggingFactory {
  static final DateTimeFormatter tomcat_formatter = DateTimeFormatter
      .ofPattern("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH)
      .withZone(ZoneId.systemDefault());

  @SuppressWarnings("unused")
  static final DateTimeFormatter zulu_formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
          .withZone(ZoneId.systemDefault());

  static final Object lock = new Object();

  // genuinely is an inner class, cannot be static
  private class Logger implements Logging {
    final Instant time = Instant.now();
    final Frame frame;

    private Logger() {
      this.frame = new Frame(LoggingFactory.this.type);
    }

    @SuppressWarnings("SameParameterValue")
    private String formatLogMessage(final Level level, final DateTimeFormatter timeFormatter, final String message) {

      final Appendable appendable = new StringBuilder();

      try (final Formatter formatter = new Formatter(appendable)) {

        formatter.format("%s %5s [%s] {%s} %s(%d) %s",
            timeFormatter.format(this.time),
            level,
            Thread.currentThread().getName(),
            this.frame.getSimpleClassName(),
            this.frame.getMethodName(),
            this.frame.getLineNumber(),
            message);

        formatter.flush();

        return appendable.toString();
      }
    }

    private void post(final Level level, final String message) {
      synchronized (lock) {
        if (ExecutionContext.getLoggingManager().willLog(level)) {
          final String logMessage = formatLogMessage(level, tomcat_formatter, message);

          System.out.println(logMessage);

          ExecutionContext.getLoggingManager().postMessage(logMessage);
        }
      }
    }

    @Override
    public void log(final Level level, final String format, final Object... params) {
      try {
        post(level, MessageFormat.format(format, params));
      }
      catch (final IllegalArgumentException e) {
        post(level,format + "|" + Arrays.stream(params)
            .map(Object::toString)
            .collect(Collectors.joining("|")));
      }
    }

    @Override
    public void exception(final Throwable t) {
      final StackTraceElement frame = t.getStackTrace()[0];

      error("{0} | {1} | {2}.{3}({4})",
          t.getClass().getName(),
          t.getLocalizedMessage(),
          frame.getClassName(),
          frame.getMethodName(),
          frame.getLineNumber());
    }
  }

  final Class<?> type;

  LoggingFactory(final Class<?> type) {
    this.type = type;
  }

  // TODO: Factory Methods not part of the interface
  public Logging get() {
    return new Logger();
  }

  //TRACE
  public void in() {
    new Logger().log(Level.TRACE, "-->");
  }
  //TRACE
  public void out() {
    new Logger().log(Level.TRACE,"<--");
  }
  //TRACE
  public void me(final Object obj) {
    new Logger().log(Level.TRACE, String.format("%08X", obj.hashCode()));
  }

  @SuppressWarnings("unused")
  public void json(final Level level, final Object obj) {
    new Logger().log(level, "{0} {1}", obj.getClass().getName(), Serializer.safeSerializeJsonIndented(obj));
  }

  public void yaml(final Level level, final Object obj) {
    new Logger().log(level, "{0} {1}", obj.getClass().getName(), Serializer.safeSerializeYaml(obj));
  }

  //ERROR
  public void exception(final Throwable t) {
    new Logger().exception(t);
  }

  public Class<?> getType() {
    return this.type;
  }

  public static LoggingFactory create(final Class<?> type) {
    return new LoggingFactory(type);
  }
}
