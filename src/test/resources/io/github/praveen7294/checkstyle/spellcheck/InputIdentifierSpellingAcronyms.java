package io.github.praveen7294.checkstyle.spellcheck;

/**
 * Test input for IdentifierSpellingCheck testing acronyms and consecutive uppercase identifiers.
 */
public class InputIdentifierSpellingAcronyms {

  // Valid acronym identifiers
  class HttpServer {
  }

  class HTTPServer {
  }

  class URLClassLoader {
  }

  private String userDTO;

  public void parseJSONResponse() {
  }

  // Misspelled acronym identifiers
  class HTPPServer { // violation: "htpp" is misspelled
  }

  class HTTPServver { // violation: "servver" is misspelled
  }

  public void parseJSONResponsse() { // violation: "responsse" is misspelled
  }
}
