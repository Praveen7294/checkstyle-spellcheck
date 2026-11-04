package io.github.praveen7294.checkstyle.spellcheck.util;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Utility class to split Java identifiers into individual normalized words.
 */
public final class IdentifierWordSplitter {

  /**
   * Pattern to split snake_case tokens by underscores.
   */
  private static final Pattern SNAKE_CASE_PATTERN = Pattern.compile("_+");

  /**
   * Pattern to split camelCase and PascalCase boundaries (lowercase followed by uppercase).
   */
  private static final Pattern CAMEL_CASE_PATTERN =
      Pattern.compile("(?<=[a-z])(?=[A-Z])");

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private IdentifierWordSplitter() {
    // Utility class
  }

  /**
   * Splits a Java identifier into individual lowercase words.
   *
   * @param identifier the identifier to split
   * @return an unmodifiable list of lowercase words, or an empty list if null or blank
   */
  public static List<String> split(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      return List.of();
    }
    return SNAKE_CASE_PATTERN.splitAsStream(identifier)
      .filter(Predicate.not(String::isEmpty))
      .flatMap(CAMEL_CASE_PATTERN::splitAsStream)
      .map(word -> word.toLowerCase(Locale.ROOT))
      .toList();
  }
}
