package com.revenera.gcs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.logging.Loggable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonPropertyOrder({
    "version",
    "release",
    "timestamp",
    "date",
    "time",
    "loggingLevel",
    "loggingEcho",
    "housekeepingFrequency",
    "housekeepingRetention",
    "user",
    "versionDetails",
    "proficyConfiguration",
    "hash"
})
public final class ApplicationProperties extends Loggable {

  public enum Items {
    TIMESTAMP("build.timestamp"),
    VERSION_MAJOR("build.version.major"),
    VERSION_MINOR("build.version.minor"),
    BUILD_NUMBER("build.number"),
    DATE("build.date"),
    TIME("build.time"),

    USER("build.username"),
    RELEASE("build.category"),

    LOGGING_THRESHOLD("app.logging.level"),
    LOGGING_ECHO("app.logging.echo"),
    HOUSEKEEPING_INTERVAL("app.housekeeping"),
    HOUSEKEEPING_RETENTION("app.housekeeping.retention"),
    LANDING_HEADER("app.landing.header"),
    LANDING_TITLE("app.landing.title"),
    PROFICY_SEPARATOR("app.proficy.separator"),
    PROFICY_CHARSET("app.proficy.charset"),
    PROFICY_GROUP_COUNT("app.proficy.group-count"),
    PROFICY_GROUP_LENGTH("app.proficy.group-length"),
    PROFICY_PRODUCT_LINES("app.proficy.product-lines");

    public final String name;
    Items(String name) {
      this.name = name;
    }
  }

  private final Map<Items,Object> properties = new LinkedHashMap<>();

  public ApplicationProperties() {
    logger.me(this);

    try {
      final Properties props = new Properties();
      props.load(Beans.class.getResourceAsStream("/revenera.properties"));

      Arrays.stream(Items.values()).forEach(item -> properties.put(item, props.getProperty(item.name)));
    }
    catch (final IOException ignored) {
      //TODO: just ignore this nothing can be done
    }
  }

  public String getItem(final Items item) {
    return properties.get(item).toString();
  }

  public String getTimestamp() {
    return this.properties.get(Items.TIMESTAMP).toString();
  }

  public String getVersion() {
    return Stream.of(this.properties.get(Items.VERSION_MAJOR),
        this.properties.get(Items.VERSION_MINOR),
        this.properties.get(Items.BUILD_NUMBER))
        .map(Object::toString)
        .collect(Collectors.joining("."));
  }

  public String getVersionMajor() {
    return this.properties.get(Items.VERSION_MAJOR).toString();
  }

  public String getVersionMinor() {
    return this.properties.get(Items.VERSION_MINOR).toString();
  }

  public String getBuildNumber() {
    return this.properties.get(Items.BUILD_NUMBER).toString();
  }

  public String getReleaseDate() {
    return this.properties.get(Items.DATE).toString();
  }

  public String getReleaseTime() {
    return this.properties.get(Items.TIME).toString();
  }

  public int getHousekeepingFrequency() {
    return Integer.parseInt(this.properties.get(Items.HOUSEKEEPING_INTERVAL).toString());
  }

  public int getHousekeepingRetention() {
    return Integer.parseInt(this.properties.get(Items.HOUSEKEEPING_RETENTION).toString());
  }

  public String getLoggingLevel() {
    return this.properties.get(Items.LOGGING_THRESHOLD).toString();
  }

  @SuppressWarnings("unused")
  public boolean getLoggingEcho() {
    return Arrays.asList("true","on")
        .contains(this.properties.get(Items.LOGGING_ECHO).toString().toLowerCase());
  }

  public String getUser() {
    return this.properties.get(Items.USER).toString();
  }

  public String getRelease() {
    return this.properties.get(Items.RELEASE).toString();
  }

  public String getVersionDetails() {

    return Stream.of(this.properties.get(Items.VERSION_MAJOR),
            this.properties.get(Items.VERSION_MINOR),
            this.properties.get(Items.BUILD_NUMBER),
            this.properties.get(Items.RELEASE),
            this.properties.get(Items.DATE),
            this.properties.get(Items.TIME),
            this.properties.get(Items.USER))
        .map(Object::toString)
        .collect(Collectors.joining(" | "));
  }

  public ProficyConfiguration getProficyConfiguration() {
    return new ProficyConfiguration() {
      {
        this.charset = getItem(Items.PROFICY_CHARSET);
        this.separator = getItem(Items.PROFICY_SEPARATOR);
        this.groupCount = Integer.parseInt(getItem(Items.PROFICY_GROUP_COUNT));
        this.groupLength = Integer.parseInt(getItem(Items.PROFICY_GROUP_LENGTH));
        this.productLines = Arrays.asList(
            properties.get(Items.PROFICY_PRODUCT_LINES)
                .toString()
                .split(",")
        );
      }
    };
  }

  @SuppressWarnings("unused")
  public String getHash() {
    return String.format("%08X", hashCode());
  }
}