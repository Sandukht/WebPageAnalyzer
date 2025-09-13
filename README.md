# 🌐 Web Page Word Analyzer

A Java console application that:

* Takes a web page URL as input.
* Downloads the page content using `java.net.http.HttpClient`.
* Analyzes the text to find the **top 10 most used words**, their **occurrence count**, and the **sentences they appear in**.
* Stores the results in a MySQL database (`webpage_analyzer.words_statistics`) with metadata like the page URL, language, and timestamp.
* **Prevents duplicates** by skipping analysis if the same URL was already processed.

---

## 📂 Project Structure

```

WebPageAnalyzer/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/
│ │ │ │ ├── com/example/
│ │ │ │ │ ├── App.java                              # Main entry point
│ │ │ │ │ ├── Input.java                            # Reads URL from user input
│ │ │ │ │ ├── fetcher/WebFetcher.java               # Fetches webpage content
│ │ │ │ │ ├── db/DBManager.java                     # Handles database inserts
│ │ │ │ │ ├── db/RunSQL.java                        # Creates DB + table on startup
│ │ │ ├── resources/
│ │ │ │ ├── stopwords/
│ │ │ │ │ ├── stopwords\_en.txt
│ │ │ │ │ ├── stopwords\_ru.txt
│ │ │ │ │ └── stopwords\_hy.txt
│ ├── build.gradle                                  # Gradle configuration
│ └── settings.gradle
├── up                                               # Script to run the project
└── README.md

````

---

## ⚙️ Setup

### 1. Install Dependencies

Make sure you have installed:

* Java 17+
* Gradle
* MySQL server

**build.gradle dependencies:**

```gradle
dependencies {
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // Libraries for app
    implementation 'com.google.guava:guava:32.1.2-jre'                  // utilities
    implementation 'org.jsoup:jsoup:1.16.1'                              // HTML parsing
    implementation 'org.apache.opennlp:opennlp-tools:2.2.0'              // NLP
    implementation 'com.optimaize.languagedetector:language-detector:0.6' // language detection
    implementation 'mysql:mysql-connector-java:8.0.33'                   // JDBC
    implementation 'org.slf4j:slf4j-api:2.0.9'                            // logging API
    runtimeOnly 'ch.qos.logback:logback-classic:1.4.11'                   // logging implementation
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.16.2'   // JSON optional
}
````

---

## 🛢️ Database Setup

Before running the project, create the database and table.
You can do this in **two ways**:

### Option 1 — Run `schema.sql` in MySQL CLI

```bash
mysql -u root -p < app/src/main/java/com/example/db/schema.sql
```

**schema.sql**

```sql
CREATE DATABASE IF NOT EXISTS webpage_analyzer;

USE webpage_analyzer;

CREATE TABLE IF NOT EXISTS words_statistics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    word VARCHAR(100),
    count INT,
    lang VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### Option 2 — Run `schema.sql` from Java

```java
try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
     Statement stmt = conn.createStatement()) {

    String sql = Files.readString(Paths.get("app/src/main/java/com/example/db/schema.sql"));
    stmt.execute(sql);

} catch (SQLException | IOException e) {
    e.printStackTrace();
}
```

> ✅ This will automatically create the DB and table on the first run.

---

## 🚀 Running the Project

This project uses Gradle’s `application` plugin.
You can run it in **two ways**:

### Option 1 — Using Gradle directly

```bash
./gradlew run
```

---

### Option 2 — Using the `up` script

A shortcut script is included:

**up**

```bash
#!/usr/bin/env bash
set -e
./gradlew run
```

Make it executable and run:

```bash
chmod +x up
./up
```

---

## 📋 Usage

After starting, the app will:

1. Ask you to enter a web page URL.
2. Fetch and analyze the page content.
3. Display the top 10 most frequent words and their counts.
4. Save the results to the MySQL database.

---

## 🛢 Example Database Output

```
+----+-------------------------------------------------------------------------------------------+--------------------------------------+-------+------+---------------------+
| id | url                                                                                       | word                                 | count | lang | created_at          |
+----+-------------------------------------------------------------------------------------------+--------------------------------------+-------+------+---------------------+
|  1 | https://docs.docker.com/compose/                                                          | docker                               |    74 | eng  | 2025-09-13 03:22:15 |
|  2 | https://docs.docker.com/compose/                                                          | driver                               |    31 | eng  | 2025-09-13 03:22:15 |
|  3 | https://docs.docker.com/compose/                                                          | compose                              |    30 | eng  | 2025-09-13 03:22:15 |
|  5 | https://docs.docker.com/compose/                                                          | engine                               |    29 | eng  | 2025-09-13 03:22:15 |
| ... | ...                                                                                      | ...                                  |  ...  | ...  | ...                 |
| 81 | https://translate.yandex.ru/?source_lang=en&target_lang=hy&text=fetch%20Text%20From%20Url | русский                              |     9 | ru   | 2025-09-13 17:55:00 |
| 82 | https://translate.yandex.ru/?source_lang=en&target_lang=hy&text=fetch%20Text%20From%20Url | текст                                |     5 | ru   | 2025-09-13 17:55:00 |
| 90 | https://translate.yandex.ru/?source_lang=en&target_lang=hy&text=fetch%20Text%20From%20Url | переводчик                           |     3 | ru   | 2025-09-13 17:55:00 |
+----+-------------------------------------------------------------------------------------------+--------------------------------------+-------+------+---------------------+
```

---

## 📌 Notes

* Each URL is analyzed only once.
* Duplicate entries are skipped.
* Stopwords are filtered out based on the detected language.

---

```