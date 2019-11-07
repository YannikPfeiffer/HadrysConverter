package de.irgendwas;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class WordGenerator {

    public static void setTabStop(XWPFParagraph oParagraph, STTabJc.Enum oSTTabJc, BigInteger oPos) {
        CTP oCTP = oParagraph.getCTP();
        CTPPr oPPr = oCTP.getPPr();
        if (oPPr == null) {
            oPPr = oCTP.addNewPPr();
        }

        CTTabs oTabs = oPPr.getTabs();
        if (oTabs == null) {
            oTabs = oPPr.addNewTabs();
        }

        CTTabStop oTabStop = oTabs.addNewTab();
        oTabStop.setVal(oSTTabJc);
        oTabStop.setPos(oPos);
    }

    public void generateDoc(ArrayList<String> textArrayList, String savePath, String fileName) {
        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            //Write the Document in file system
            FileOutputStream out = new FileOutputStream(new File(savePath + "\\" + fileName + ".docx"));

            XWPFParagraph paragraph;

            XWPFRun emptySpace;
            XWPFRun nextParagraph;

            XWPFHeaderFooterPolicy headerFooterPolicy = document.createHeaderFooterPolicy();
            XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

            XWPFParagraph headerParagraph = header.createParagraph();

            headerParagraph.setAlignment(ParagraphAlignment.BOTH);
            headerParagraph.setBorderBottom(Borders.SINGLE);

            XWPFRun run = headerParagraph.createRun();

            run.setText("Übung Web-Engineering");
            run.addTab();
            //run = headerParagraph.createRun();
            run.setText("Aufgabenblatt zu Kapitel 1");

            BigInteger pos1 = BigInteger.valueOf(9000);
            setTabStop(headerParagraph, STTabJc.Enum.forString("right"), pos1);

            paragraph = document.createParagraph();
            nextParagraph = paragraph.createRun();

            nextParagraph.setText("Aufgaben");
            nextParagraph.setFontSize(16);
            nextParagraph.setColor("000000");
            nextParagraph.setBold(true);

            for (String line : textArrayList) {

                paragraph = document.createParagraph();
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
            System.out.println("File refactored in path: " + savePath + " written successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
