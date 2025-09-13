package com.example.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebFetcher {

        public static String fetchTextFromUrl(String url) throws IOException {
        // Connect to the URL with a 10-second timeout
        Document doc = Jsoup.connect(url).timeout(10000).get();

        doc.select("script, style, noscript").remove();
        String text = doc.body().text();

        return text;
    }
}