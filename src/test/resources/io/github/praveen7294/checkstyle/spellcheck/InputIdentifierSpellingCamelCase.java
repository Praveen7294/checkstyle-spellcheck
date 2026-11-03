package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing camelCase identifiers.
 */
public class InputIdentifierSpellingCamelCase {

  private String userName;   // valid: "user", "name"
  private String userAdress; // violation: "adress" is misspelled
  private String userDto;    // valid: "user" in dic, "dto" in whitelist

  public void setLine(int lineNumber) { // valid: "set", "line", "number"
  }

  public void printNamme(String validName) { // violation: "namme" is misspelled
  }
}
