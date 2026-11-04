package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing identifiers containing digits.
 */
public class InputIdentifierSpellingNumbers {

  // Valid identifiers with digits
  private String user1Name;
  private int maxRetry3Count;

  public void processSha256Hash(String param1) {
  }

  // Misspelled identifiers with digits
  private String user1Namme; // violation: "namme" is misspelled
  private int maxxRetry3Count; // violation: "maxx" is misspelled

  public void processSha256Hashe(String paramm1) { // violation: "hashe", "paramm"
  }
}
