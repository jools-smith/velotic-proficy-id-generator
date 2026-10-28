package com.revenera.gcs.webservices;

import java.security.SecureRandom;
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
  public String generate(
      final String charset,
      final String separator,
      final int groupCount,
      final int groupLength) {

    return IntStream.range(0, groupCount)
        .mapToObj(i -> generate(charset, groupLength))
        .collect(Collectors.joining(separator));
  }
}
