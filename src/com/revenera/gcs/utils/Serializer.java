package com.revenera.gcs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

public class Serializer {
  private static final ObjectMapper json_mapper_indented = new ObjectMapper()
      .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
      .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  private static final ObjectMapper json_mapper = new ObjectMapper()
      .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
      .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  private static final ObjectMapper yaml_mapper = new ObjectMapper(new YAMLFactory())
      .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
      .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  private static String serialize(final ObjectMapper mapper, final Object payload) {
    try {
      return mapper.writeValueAsString(payload);
    }
    catch (final Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public static String safeSerializeJson(final Object payload) {
    return serialize(json_mapper, payload);
  }

  public static void serializeJsonIndented(final PrintWriter writer, final Object payload) throws IOException {
    json_mapper_indented.writeValue(writer, payload);
  }

  public static String safeSerializeJsonIndented(final Object payload) {
    return serialize(json_mapper_indented, payload);
  }

  public static String safeSerializeYaml(final Object payload) {
    return serialize(yaml_mapper, payload);
  }

  @SuppressWarnings("unused")
  public static String jsonToBase64(final Object payload) {
    return Base64.getEncoder().encodeToString(safeSerializeJsonIndented(payload).getBytes());
  }

  @SuppressWarnings("unused")
  public static String yamlToBase64(final Object payload) {
    return Base64.getEncoder().encodeToString(safeSerializeYaml(payload).getBytes());
  }
}
