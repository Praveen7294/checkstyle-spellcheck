package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing UPPER_SNAKE_CASE constants and enums.
 */
public class InputIdentifierSpellingConstantAndEnum {

  // Valid constants
  public static final int MAX_BUFFER_SIZE = 1024;
  public static final String DEFAULT_NAME = "checkstyle";

  // Misspelled constants
  public static final int MAXX_BUFFER_SIZE = 2048; // violation: "maxx" is misspelled
  public static final String DEFAULLT_NAME = "bad"; // violation: "defaullt" is misspelled

  // Valid enum and enum constants
  enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
  }

  // Misspelled enum and enum constants
  enum Seazon { // violation: "seazon" is misspelled
    SPRINNG, // violation: "sprinng" is misspelled
    WINTER
  }
}
