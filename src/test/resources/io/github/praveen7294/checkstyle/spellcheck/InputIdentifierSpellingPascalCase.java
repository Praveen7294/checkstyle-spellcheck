package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing PascalCase classes, interfaces, and records.
 */
public class InputIdentifierSpellingPascalCase {

  // Valid classes and interfaces
  class UserAccount {
  }

  interface NameAuditor {
  }

  record LineRecord(int number) {
  }

  // Misspelled classes, interfaces, and records
  class UserAcount { // violation: "acount" is misspelled
  }

  interface NammeAuditor { // violation: "namme" is misspelled
  }

  record LineRecrd(int number) { // violation: "recrd" is misspelled
  }
}
