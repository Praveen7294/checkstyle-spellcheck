package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing annotations and record components.
 */
public class InputIdentifierSpellingAnnotationsAndRecords {

  // Valid annotation and annotation fields
  @interface ServiceAuditor {
    String filePath() default "";

    int retryCount() default 3;
  }

  // Misspelled annotation and annotation fields
  @interface ServviceAuditor { // violation: "servvice" is misspelled
    String fileePath() default ""; // violation: "filee" is misspelled

    int retryyCount() default 3; // violation: "retryy" is misspelled
  }

  // Valid record components
  record ValidAccount(String userName, int userAge) {
  }

  // Misspelled record components
  record AccountRecord(String userNamme, int userAgge) { // violation: "namme", "agge"
  }
}
