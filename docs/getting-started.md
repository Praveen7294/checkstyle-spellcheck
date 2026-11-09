# Getting Started

Adding `checkstyle-spellcheck` to your Java project takes just a few lines of configuration.

---

## 1. Build Tool Integration

=== "Maven (`pom.xml`)"

    Add `checkstyle-spellcheck` as a dependency inside your `maven-checkstyle-plugin`:

    ```xml
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-checkstyle-plugin</artifactId>
      <version>3.6.0</version>
      <dependencies>
        <dependency>
          <groupId>io.github.praveen7294</groupId>
          <artifactId>checkstyle-spellcheck</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
      <configuration>
        <configLocation>config/checkstyle.xml</configLocation>
      </configuration>
    </plugin>
    ```

=== "Gradle (`build.gradle`)"

    Add the extension to your `checkstyle` configuration:

    ```groovy
    plugins {
        id 'checkstyle'
    }

    dependencies {
        checkstyle 'io.github.praveen7294:checkstyle-spellcheck:0.1.0'
    }
    ```

=== "Checkstyle CLI"

    Include the extension JAR alongside the Checkstyle all-in-one JAR on the Java classpath:

    ```bash
    java -cp "checkstyle-14.4.0-all.jar;checkstyle-spellcheck-0.1.0.jar" \
      com.puppycrawl.tools.checkstyle.Main -c checkstyle.xml src/
    ```

---

## 2. Minimal `checkstyle.xml`

Add `IdentifierSpellingCheck` under your `TreeWalker`:

```xml
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
  <module name="TreeWalker">
    <!-- Zero-config embedded English dictionary -->
    <module name="IdentifierSpellingCheck"/>
  </module>
</module>
```

---

## 3. Running the Audit

Execute your standard build command:

=== "Maven"

```bash
mvn checkstyle:check
```

=== "Gradle"

```bash
./gradlew checkstyleMain
```
