package com.revenera.gcs.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;

public class Utils {

  public static String prettyPrintDuration(final Duration duration) {
    final long millis = duration.toMillis();

    final String format = millis < 60000 ? "s.SSS" : millis < 3600000 ? "m:ss.SSS" : "H:mm:ss.SSS";

    return DurationFormatUtils.formatDuration(millis, format);
  }
}
