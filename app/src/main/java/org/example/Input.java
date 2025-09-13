package com.example;

import java.util.Scanner;

public class Input {
    public static String getUrlFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a URL: ");
        String url = scanner.nextLine();
        return url;
    }
}
