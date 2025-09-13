package com.example;

import com.example.fetcher.WebFetcher;
import com.example.db.DBManager;
import com.example.db.RunSQL;


import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class App {
    public static void main(String[] args) {
      try {
        RunSQL.runSQL();  // Step 2: create DB/table
    } catch (IOException | SQLException e) {
        System.err.println("Failed to initialize database: " + e.getMessage());
        return;
    }
        String url = Input.getUrlFromUser();
        System.out.println("The URL you entered is: " + url);

        try {
            String textFetcher = WebFetcher.fetchTextFromUrl(url);
            System.out.println("Extracted text:\n" +
                textFetcher.substring(0, textFetcher.length()) + "...");
            String lang = detectLanguage(textFetcher);  // Detect language
            System.out.println("Detected Language: " + lang);

            Set<String> stopwordsRu = loadStopwords("/stopwords/stopwords_ru.txt", new Locale("ru"));
            Set<String> stopwordsEng = loadStopwords("/stopwords/stopwords_en.txt", Locale.ENGLISH);
            Set<String> stopwordsHy = loadStopwords("/stopwords/stopwords_hy.txt", new Locale("hy"));


            String[] sentences = textFetcher.split("[.!?]");
            Map<String, Integer> wordCount = new HashMap<>();

            for (int i = 0;i< sentences.length; i++) {
                String[] words = sentences[i].split("\\s+");
                for (String word: words) {
                    word = word.toLowerCase().replaceAll("[^\\p{L}]","");
                    if(word.isEmpty() || stopwordsEng.contains(word) 
                                      || stopwordsRu.contains(word) 
                                      || stopwordsHy.contains(word))
                        {
                        continue;
                        }
                    wordCount.put(word,wordCount.getOrDefault(word,0)+1);
                    
                }
            }
            List<Map.Entry<String,Integer>> topWords = wordCount.entrySet().stream()
                        .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                        .limit(10).collect(Collectors.toList()); 
            System.out.println("\nTop 10 words:");
            for(Map.Entry<String,Integer>entry:topWords){
                String word = entry.getKey();
                int count = entry.getValue();
                System.out.println("Word: " + word + ",\n " 
                            + "Count: " + count +",\n") ;
                DBManager.insertWordStats(url, word, count, lang);
                System.out.println("Word statistics saved to database.\n");
                }
        }catch (IOException e) {
            System.out.println("Failed to fetch webpage: " + e.getMessage());
        }
    }

    private static String detectLanguage(String text){
        int ruCount=0;
        int enCount=0;
        int hyCount=0;
        for (char ch :text.toCharArray()){
            if(ch>='a'&&ch<='z'||ch>='A'&&ch<='Z')  enCount++;
            else if(ch>='а'&&ch<='я'||ch>='А'&&ch<='Я') ruCount++;
            else if((ch>=0x0531&&ch<=0x0556)||(ch>=0x0561&&ch<=0x0587)) hyCount++;  
        }
        if( ruCount>=enCount && ruCount>=hyCount) return "ru";
        else if(enCount>=ruCount && enCount>=hyCount) return "eng";
        else return "hy";
    }

    private static Set<String> loadStopwords(String resourcePath, Locale locale) {
        Set<String> stopwords = new HashSet<>();
        try (InputStream is = App.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase(locale));
            }
        } catch (IOException e) {
            System.err.println("Error loading stopwords from " + resourcePath + ": " + e.getMessage());
        }
        return stopwords;
    }

}