package de.irgendwas;

import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordGenerator {

    public void stylizeDocument(ArrayList<String> textArrayList, String savePath, String fileName){
        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            //Write the Document in file system
            FileOutputStream out = new FileOutputStream( new File(savePath+"\\"+fileName+".docx"));

            XWPFParagraph paragraph = document.createParagraph();

            XWPFRun emptySpace;
            XWPFRun nextParagraph;


            for (String line: textArrayList) {
                //create paragraph
                paragraph.setSpacingBetween(1.5);
                nextParagraph = paragraph.createRun();

                //Set Italic, Blue
                nextParagraph.setItalic(true);
                nextParagraph.setFontSize(14);
                nextParagraph.setText(line);
                nextParagraph.setColor("166b99");
                nextParagraph.addBreak();

                emptySpace = paragraph.createRun();
                emptySpace.setFontSize(14);
                emptySpace.setText("");
                emptySpace.setColor("000000");
                emptySpace.addBreak();
                emptySpace.addBreak();
            }


            document.write(out);
            out.close();
            System.out.println("File refactored in path: " + savePath + " written successully");
        } catch (IOException e){
            System.err.println(e);
        }
    }
}