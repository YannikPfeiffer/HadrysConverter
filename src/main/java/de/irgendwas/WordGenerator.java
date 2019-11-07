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

    private BigInteger addListStyle(XWPFDocument doc) {
        try {
            BigInteger id = BigInteger.valueOf(1);
            return doc.getNumbering().addNum(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateDoc(ArrayList<String> textArrayList, String savePath, String fileName) {
        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            //Write the Document in file system
            FileOutputStream out = new FileOutputStream(new File(savePath + "\\" + fileName + ".docx"));



            XWPFHeader header;
            XWPFParagraph paragraph;
            XWPFParagraph headerParagraph;
            XWPFRun emptySpaceRun;
            XWPFRun textRun;
            XWPFRun headerRun;

            XWPFHeaderFooterPolicy headerFooterPolicy = document.createHeaderFooterPolicy();

            header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

            headerParagraph = header.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.BOTH);
            headerParagraph.setBorderBottom(Borders.SINGLE);

            headerRun = headerParagraph.createRun();
            headerRun.setText("Übung Web-Engineering");
            headerRun.addTab();
            headerRun.setText("Aufgabenblatt zu Kapitel 2");

            BigInteger pos1 = BigInteger.valueOf(9000); //int to set right text's position
            setTabStop(headerParagraph, STTabJc.Enum.forString("right"), pos1);

            paragraph = document.createParagraph();
            textRun = paragraph.createRun();

            textRun.setText("Aufgaben");
            textRun.setFontSize(16);
            textRun.setColor("000000");
            textRun.setBold(true);

            document.createNumbering();

            System.out.println("...set Header");
            for (String line : textArrayList) {

                paragraph = document.createParagraph();
                //create paragraph
                paragraph.setSpacingBetween(1.5);
                paragraph.setNumID(addListStyle(document));

                textRun = paragraph.createRun();

                //Set Italic, Blue
                textRun.setItalic(true);
                textRun.setFontSize(14);
                textRun.setText(line);
                textRun.setColor("166b99");

                XWPFParagraph para2 = document.createParagraph();
                para2.setSpacingBetween(1.5);
                para2.setIndentFromLeft(800);
                emptySpaceRun = para2.createRun();
                emptySpaceRun.setFontSize(14);
                emptySpaceRun.setText("");
                emptySpaceRun.setColor("000000");
            }

            document.write(out);
            out.close();
            System.out.println("File refactored in path: " + savePath + "\nWritten successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
