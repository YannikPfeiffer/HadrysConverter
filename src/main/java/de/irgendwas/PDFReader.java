package de.irgendwas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFReader {

    PDFReader(){

    };

    public ArrayList<String> getTextFromFile(String sourcePath) {
       // String fileName = "01-JSP-Aufgaben.pdf";
       // File myFile = new File("C:\\Users\\Yannik Pfeiffer\\Desktop\\"+fileName);

        ArrayList<String> finalTextArray = new ArrayList<>();

        File myFile = new File(sourcePath);

        try {
            PDDocument doc = PDDocument.load(myFile);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            System.out.println("Text size: " + text.length());

            BufferedReader buffReader = new BufferedReader(new StringReader(text));
            String line = null;
            ArrayList<String> textColumns = new ArrayList<>();

            //final int flags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE

            String regexp = "^(Übung|Seite|Aufgaben) (.*)$";
            String regexp2 = "^[0-9]+\\)(.*)$";

            //remove tags: Seite|Übung|Aufgaben
            Pattern p = Pattern.compile(regexp);
            Matcher m;
            while ((line = buffReader.readLine()) != null) {
                m = p.matcher(line);
                if (!m.matches()) {
                    textColumns.add(line);
                }
            }

            Pattern p2 = Pattern.compile(regexp2);

            //remove unnecessary breaks
            for (String line2 : textColumns) {
                System.out.println(line2);
                if (p2.matcher(line2).matches()) {
                    finalTextArray.add(line2);
                } else {
                    int pos = finalTextArray.size() - 1;
                    String getString = finalTextArray.get(pos);
                    String newString = getString.concat(line2);

                    finalTextArray.set(pos, newString);
                }
            }

            //remove numbering
            for (int i = 0; i<finalTextArray.size();i++){

                String line3 = finalTextArray.get(i).replaceAll("[0-9]+\\) ","");

                finalTextArray.set(i,line3);
            }
            System.out.println("ArrayList generated.");
        }
        catch (IOException e){
            System.err.println(e);
        }
        return finalTextArray;
    }
}
