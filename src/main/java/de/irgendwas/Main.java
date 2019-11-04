package de.irgendwas;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        PDFReader pdfReader = new PDFReader();
        ArrayList<String> text = pdfReader.getTextFromFile("C:\\Users\\Yannik Pfeiffer\\Downloads\\02 - Servlets - Aufgaben.pdf");
        for (String line: text) {
            System.out.println(line);
        }
        WordGenerator wordGenerator = new WordGenerator();
        String path = System.getProperty("user.home") + "/Desktop";
        wordGenerator.generateDoc(text,path,"JSP-02-Pfeiffer, Yannik");
    }
}
