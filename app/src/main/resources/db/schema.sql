CREATE DATABASE IF NOT EXISTS webpage_analyzer;

USE webpage_analyzer;

CREATE TABLE IF NOT EXISTS words_statistics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    word VARCHAR(100),
    count INT,
    lang VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_url_word (url, word)
);