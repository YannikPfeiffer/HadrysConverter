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

    private static void addCustomHeadingStyle(XWPFDocument docxDocument, XWPFStyles styles, String strStyleId, int headingLevel, int pointSize, String hexColor) {

        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);


        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        // style shows up in the formats bar
        ctStyle.setQFormat(onoffnull);

        // style defines a heading of the given level
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        XWPFStyle style = new XWPFStyle(ctStyle);

        CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
        size.setVal(new BigInteger(String.valueOf(pointSize)));
        CTHpsMeasure size2 = CTHpsMeasure.Factory.newInstance();
        size2.setVal(new BigInteger("24"));

        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii("Loma" );

        CTRPr rpr = CTRPr.Factory.newInstance();
        rpr.setRFonts(fonts);
        rpr.setSz(size);
        rpr.setSzCs(size2);

        CTColor color=CTColor.Factory.newInstance();
        color.setVal(hexToBytes(hexColor));
        rpr.setColor(color);
        style.getCTStyle().setRPr(rpr);
        // is a null op if already defined

        //style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }

    public static byte[] hexToBytes(String hexString) {
        byte[] bytes = new BigInteger(hexString,16).toByteArray();
        return bytes;
    }

    public void generateDoc(ArrayList<String> textArrayList, String savePath, String fileName, String[] authorName, int exerciseNumber) {
        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            //Write the Document in file system
            FileOutputStream out = new FileOutputStream(new File(savePath + "\\" + fileName + ".docx"));

            //Declaration of various variables
            XWPFHeader header;
            XWPFHeaderFooterPolicy headerFooterPolicy;
            XWPFParagraph paragraph;
            XWPFParagraph headerParagraph;
            XWPFRun emptySpaceRun;
            XWPFRun textRun;
            XWPFRun headerRun;
            XWPFStyles styles; //needed to style elements that don't provide a built in styling function

            styles = document.createStyles();

            headerFooterPolicy = document.createHeaderFooterPolicy();

            header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

            headerParagraph = header.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.BOTH);
            headerParagraph.setBorderBottom(Borders.SINGLE);

            headerRun = headerParagraph.createRun();
            headerRun.setText(authorName[1]+" "+authorName[0]);
            headerRun.addTab();
            headerRun.setText("Aufgabenblatt zu Kapitel "+exerciseNumber);

            setTabStop(headerParagraph, STTabJc.Enum.forString("right"), BigInteger.valueOf(9000));

            paragraph = document.createParagraph();
            textRun = paragraph.createRun();

            textRun.setText("Aufgaben");
            textRun.setFontSize(16);
            textRun.setColor("13152d"); //13152d, 000224
            textRun.setBold(false);

            document.createNumbering();

            System.out.println("...set Header");

            String numericListID = "numList";
            //String emptyTextID = "empTxtID";

            addCustomHeadingStyle(document, styles, numericListID, 1, 26, "166b99"); //
            //addCustomHeadingStyle(document, styles, emptyTextID, 1, 26, "13152d"); not working as intended -> disabled until motivation is back
            for (String line : textArrayList) {

                paragraph = document.createParagraph();
                //create paragraph
                paragraph.setSpacingBetween(1.5);
                paragraph.setStyle("numList");
                paragraph.setNumID(addListStyle(document));
                paragraph.setIndentFromLeft(700);
                paragraph.setIndentFromRight(1420); //equals to 2,5 cm from the right border line ----> 56,8 or approx 57 equals 1 mm
                textRun = paragraph.createRun();

                //Set Italic, Blue
                textRun.setItalic(true);
                textRun.setFontSize(14);
                textRun.setText(line);
                textRun.setColor("166b99");

                XWPFParagraph para2 = document.createParagraph();
                para2.setSpacingBetween(1.5);
                para2.setIndentFromLeft(767);
                para2.setIndentFromRight(1000);
                //para2.setStyle("empTxtID");
                emptySpaceRun = para2.createRun();
                emptySpaceRun.setColor("13152d"); //only does something as long as an empty space exists
                emptySpaceRun.setFontSize(14);
                emptySpaceRun.setText("");

            }

            document.write(out);
            out.close();
            System.out.println("File refactored in path: " + savePath + "\nWritten successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
