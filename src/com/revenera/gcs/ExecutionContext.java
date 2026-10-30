package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingManager;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.transaction.TransactionManagement;
import com.revenera.gcs.utils.AutoClose;
import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Utils;
import com.revenera.gcs.webservices.IStringGenerator;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

class DataObject {
  public final Frame frame;
  public final Class<?> type;
  public final Object data;

  DataObject(final Frame frame, final Object data, final Class<?> type) {
    this.frame = frame;
    this.data = data;
    this.type = type;
  }

  DataObject(final Frame frame, final Object data) {
    this(frame, data, data.getClass());
  }
}

class Transaction {
  final Frame frame;
  final Instant timestamp = Instant.now();

  DataObject request;
  DataObject response;
  final List<DataObject> payload = new LinkedList<>();

  Transaction(final Frame frame) {
    this.frame = frame;
  }

  boolean hasData() {
    return this.request != null || this.response != null || !this.payload.isEmpty();
  }
}

@AutoClose
public class ExecutionContext implements AutoCloseable {

  static final ThreadLocal<LinkedList<Transaction>> context = new ThreadLocal<>();

  private final boolean serialize;

  private ExecutionContext(final Frame frame, final boolean serialize) {
    this.serialize = serialize;

    if (context.get() == null) {
      context.set(new LinkedList<>());
    }
    context.get().add(new Transaction(frame));
  }

  public ExecutionContext() {
    this(new Frame(Frame.Depth.ONE), true);
  }

//  private ExecutionContext(final boolean serialize) {
//    this(new Frame(Frame.Depth.ONE), serialize);
//  }

  @Override
  public void close() {
    final Transaction transaction = context.get().removeLast();

    if (this.serialize) {
      Beans.diagnosticsFactory.submitExecutionDetails(transaction.timestamp, transaction.frame);

      if (transaction.hasData()) {
        Beans.diagnosticsFactory.submitTransaction(
            transaction.frame,
            transaction.timestamp,
            transaction.request,
            transaction.response,
            transaction.payload);
      }
    }

    if (context.get().isEmpty()) {
      // clear down thread data
      context.remove();
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T injectRequest(final T data) {
    context.get().getLast().request = new DataObject(new Frame(Frame.Depth.ONE), data);

    return data;
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T injectPayload(final T data) {
    final Transaction ctx = context.get().getLast();

    context.get().getLast().payload.add(
        new DataObject(
            new Frame(Frame.Depth.ONE),
            data)
    );

    return data;
  }

  public static <T> T injectResponse(final T data) {

    context.get().getLast().response = new DataObject(new Frame(Frame.Depth.ONE), data);

    return data;
  }

  public static Duration getApplicationDuration() {
    return Beans.stopwatch.getDuration();
  }

  public static ApplicationProperties getApplicationProperties() {
    return Beans.applicationProperties;
  }

  @SuppressWarnings("unused")
  public static ExecutionManagement getExecutionManager() {
    return Beans.diagnosticsFactory;
  }

  public static TransactionManagement getTransactionManager() {
    return Beans.diagnosticsFactory;
  }

  public static LoggingManager getLoggingManager() {
    return Beans.loggingManager;
  }

  public static IStringGenerator getStringGenerator() {
    return Beans.stringGenerator;
  }

//  public static ServiceManager getServiceManager() {
//    return Beans.serviceManager;
//  }

  public static Path getLogPath() {
    return Beans.getLogPath();
  }

  public static HandyBag getRecordsBag() {
    final HandyBag bag = new HandyBag();
    Beans.diagnosticsFactory.getRecords().stream()
        .sorted(Comparator.comparing(ExecutionRecord::getUpdated).reversed())
        .forEach(x ->
            bag.beginSection(x.getMethod())
                .with("visits", x.getCount())
                .with("mean latency", x.getMeanLatency())
                .with("total sojourn", x.getTotalDuration()));

    return bag;
  }

  final static Function<Long, String> toMegaBytes = v -> MessageFormat.format("{0}MB", (v / 1024 / 1024));

  public static HandyBag getApplicationData() {
    final ApplicationProperties props = getApplicationProperties();

    final Runtime runtime = Runtime.getRuntime();

    return new HandyBag()
        .beginSection("build")
        .with("version", MessageFormat.format("{0} {1}",
            props.getVersion(),
            props.getRelease()))
        .with("date", MessageFormat.format("{0} {1}",
            props.getReleaseDate(),
            props.getReleaseTime()))
        .with("user", MessageFormat.format("{0} at:{1}",
            props.getUser(),
            props.getTimestamp()))

        .endSectionAndBegin("logging")
        .with("level", MessageFormat.format("{0} ({1})",
            ExecutionContext.getLoggingManager().getLevel(),
            props.getLoggingLevel()))
        .with("echo", MessageFormat.format("{0} ({1})",
            ExecutionContext.getLoggingManager().willEcho(),
            props.getLoggingEcho()))
        .with("frequency", props.getHousekeepingFrequency())

        .endSectionAndBegin("system")
        .with("times", MessageFormat.format("{0} for:{1}",
            Instant.now().toString(),
            Utils.prettyPrintDuration(Beans.stopwatch.getDuration())))
        .with("where", MessageFormat.format("{0} on:{1}",
            SystemProperties.getUserName("unknown"),
            SystemUtils.getHostName()))
        .with("resource", Beans.getResourcePath().toAbsolutePath().toString())

        .endSectionAndBegin("memory")
        .with("processors", runtime.availableProcessors())
        .with("free", toMegaBytes.apply(runtime.freeMemory()))
        .with("total", toMegaBytes.apply(runtime.totalMemory()))
        .with("max", toMegaBytes.apply(runtime.maxMemory()))

        .endSectionAndBegin("operating-system")
        .with("name", SystemUtils.OS_NAME)
        .with("version", SystemUtils.OS_VERSION)
        .with("architecture", SystemUtils.OS_ARCH)

        .endSectionAndBegin("java")
        .with("version", SystemUtils.JAVA_VERSION)
        .with("vendor", SystemUtils.JAVA_VENDOR)
        .with("class-version", SystemUtils.JAVA_CLASS_VERSION)
        .with("vm-name", SystemUtils.JAVA_VM_NAME)
        .with("vm-info", SystemUtils.JAVA_VM_INFO)
        .endSection()

        .with("diagnostics", getRecordsBag());
  }
}
