# Checkstyle Spellcheck Extension

Welcome to the documentation for **`checkstyle-spellcheck`**, a high-performance Checkstyle extension that validates the
spelling of Java identifiers across all declaration AST nodes.

Powered by the **Apache Lucene Hunspell** morphology engine, this extension brings industrial-grade spellchecking to
your Java builds with zero external configuration required out of the box.

---

## Why Checkstyle Spellcheck?

* **Catch typos before production:** Typos in public APIs, method names, database entities, and record components stay
  in codebases forever due to backwards compatibility.
* **Zero-Config English Default:** Ships with embedded `en_US` Hunspell dictionary (`en_US.aff` and `en_US.dic`) as
  classpath resources.
* **Morphology-Aware:** Understands prefixes, suffixes, compound words, and inflections using Hunspell's linguistic
  production rules.
* **Blazing Fast:** Employs an in-memory lock-free word cache (`ConcurrentHashMap`) to achieve $O(1)$ amortized
  validation speed across massive enterprise codebases.
* **Clean Single Whitelist:** Project-controlled whitelist for domain jargon, abbreviations, and technical acronyms.

---

## Quick Example

```java
public class UserService {
    // Checkstyle flags: 'adress' -> Misspelled word
    private String userAdress;

    // Checkstyle flags: 'namme' -> Misspelled word
    public void printNamme() {
        System.out.println(userAdress);
    }
}
```

```text
[WARN] UserService.java:3:20: Identifier 'userAdress' contains misspelled word 'adress'. [IdentifierSpellingCheck]
[WARN] UserService.java:6:17: Identifier 'printNamme' contains misspelled word 'namme'. [IdentifierSpellingCheck]
```

---

## Explore the Documentation

* [**Getting Started**](getting-started.md): Add the extension to your Maven, Gradle, or CLI build in 2 minutes.
* [**Configuration**](configuration.md): Learn how to configure project whitelists and custom foreign-language
  dictionaries.
* [**Tokens & Splitting**](token-coverage.md): Deep dive into the 11 audited AST declaration tokens and identifier
  splitting pipeline.
* [**Architecture & Internals**](architecture.md): Detailed architectural specification and 3-Tier validation design.
* [**Checkstyle Case Study**](case-study.md): Real-world audit results across 500+ files in Checkstyle core.
