package com.revenera.gcs.webservices;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SimpleStringGenerator implements IStringGenerator {

  private static final SecureRandom random = new SecureRandom();

  @Override
  public String generate(final String charset, final int length) {
    return IntStream.range(0, length)
        .mapToObj(i ->
            String.valueOf(
                charset.charAt(random.nextInt(charset.length()))
            )
        )
        .collect(Collectors.joining());
  }

  @Override
  public String generateGroups(
      final String charset,
      final String separator,
      final int groupCount,
      final int groupLength) {

    return IntStream.range(0, groupCount)
        .mapToObj(i -> generate(charset, groupLength))
        .collect(Collectors.joining(separator));
  }

  private static String makeStandardId() {
    return String.join("-",
        UUID.randomUUID()
            .toString().replace("-", "").split("(?<=\\G.{4})"));
  }

  private static String makeStandardId(final String prefix) {
    return prefix + UUID.randomUUID()
        .toString().replace("-", "_");
  }

  @Override
  public String generateStandard(final Type type) {
    switch (type) {
      case Fulfilment:
        return makeStandardId("FID_");
      case Consolidated:
        return makeStandardId("CID_");
      case Entitlement:
      case Activation:
      case Maintenance:
      case Web:
        return makeStandardId();
      default:
        throw new IllegalArgumentException("Invalid type " + type);
    }
  }
}
