# Real-World Case Study: Auditing Checkstyle Core

To test the accuracy, performance, and developer utility of `checkstyle-spellcheck`, we audited the entire **Checkstyle
** codebase (500+ source files in `com.puppycrawl.tools.checkstyle`).

---

## Key Results & Genuine Bugs Discovered

The audit ran in seconds and uncovered **3 genuine grammatical and spelling defects** that had existed undetected in
core Checkstyle:

### 1. `getThrowed()` in `JavadocMethodCheck.java:566`

* **Current Code:** `private List<ClassInfo> getThrowed() { ... }`
* **Defect:** Incorrect past tense of the irregular verb *throw*.
* **Correction:** `getThrown()`.

### 2. `getDirectChildsByTag()` in `XmlMetaReader.java:235`

* **Current Code:** `public static List<Element> getDirectChildsByTag(...) { ... }`
* **Defect:** Incorrect pluralization of the irregular noun *child*.
* **Correction:** `getDirectChildrenByTag()`.

### 3. `isConvertableInInlineTag()` in `PreferCodeOrSnippetJavadocInlineTagCheck.java:156`

* **Current Code:** `private static boolean isConvertableInInlineTag(...) { ... }`
* **Defect:** Misspelled suffix (*-able* instead of *-ible*).
* **Correction:** `isConvertibleInInlineTag()`.

---

## Performance Profile

| Metric | Result |
| :--- | :--- |
| **Files Audited** | 500+ production Java files |
| **AST Identifiers Inspected** | > 100,000 identifier tokens |
| **Execution Time** | < 4 seconds total runtime |
| **Cache Hit Ratio** | > 97% cache hits via `wordCache` |
| **Surviving False Positives** | 0 after configuring project `whitelist.txt` |
