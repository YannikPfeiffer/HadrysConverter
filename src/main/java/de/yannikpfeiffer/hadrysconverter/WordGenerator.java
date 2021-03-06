package de.yannikpfeiffer.hadrysconverter;

import de.yannikpfeiffer.hadrysconverter.optionloading.OptionsLoader;
import javafx.concurrent.Task;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class WordGenerator extends Task<Void> {
    private ArrayList<String> textArrayList;
    private int exerciseNumber;
    private String[] authorName;
    private String savePath;
    private OptionsLoader optionsLoader;

    public WordGenerator(ArrayList<String> textArrayList, int exerciseNumber, String[] authorName, String savePath,
            OptionsLoader optionsLoader) {
        this.textArrayList = textArrayList;
        this.exerciseNumber = exerciseNumber;
        this.authorName = authorName;
        this.savePath = savePath;
        this.optionsLoader = optionsLoader;
    }

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

    private static void addCustomHeadingStyle(XWPFDocument docxDocument, XWPFStyles styles, String strStyleId,
            int headingLevel, int pointSize, String hexColor) {

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
        fonts.setAscii("Loma");

        CTRPr rpr = CTRPr.Factory.newInstance();
        rpr.setRFonts(fonts);
        rpr.setSz(size);
        rpr.setSzCs(size2);

        CTColor color = CTColor.Factory.newInstance();
        color.setVal(hexToBytes(hexColor));
        rpr.setColor(color);
        style.getCTStyle().setRPr(rpr);
        // is a null op if already defined

        //style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }

    public static byte[] hexToBytes(String hexString) {
        byte[] bytes = new BigInteger(hexString, 16).toByteArray();
        return bytes;
    }

    public void generateDoc() throws IOException {

        //Blank Document
        XWPFDocument document = new XWPFDocument();

        //Write the Document in file system
        this.updateTitle("Erstelle Datei");
        this.updateProgress(0, 2);
        String fileName = String.format("%02d", exerciseNumber) + "-" + authorName[0] + "," + authorName[1];
        this.updateProgress(1, 2);
        FileOutputStream out = new FileOutputStream(new File(savePath + "\\" + fileName + ".docx"));
        this.updateProgress(2, 2);

        //Declaration of various variables
        updateTitle("Erstelle Kopfzeile");
        updateProgress(0, 4);
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
        updateProgress(1, 4);

        headerParagraph = header.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.BOTH);
        headerParagraph.setBorderBottom(Borders.SINGLE);

        headerRun = headerParagraph.createRun();
        headerRun.setText(authorName[1] + " " + authorName[0]);
        headerRun.addTab();
        headerRun.setText("Aufgabenblatt zu Kapitel " + exerciseNumber);
        updateProgress(2, 4);

        setTabStop(headerParagraph, STTabJc.Enum.forString("right"), BigInteger.valueOf(9000));
        updateProgress(3, 4);

        paragraph = document.createParagraph();
        textRun = paragraph.createRun();

        textRun.setText(optionsLoader.getOptions().getTitle());
        textRun.setFontSize(16);
        textRun.setColor("13152d"); //13152d, 000224
        textRun.setBold(false);
        updateProgress(4, 4);

        document.createNumbering();

        System.out.println("...set Header");

        String numericListID = "numList";
        //String emptyTextID = "empTxtID";

        addCustomHeadingStyle(document, styles, numericListID, 1, 26, optionsLoader.getOptions().getTaskColor()); //
        //addCustomHeadingStyle(document, styles, emptyTextID, 1, 26, "13152d"); not working as intended -> disabled until motivation is back
        updateTitle("Schreibe Aufgaben");
        updateProgress(0, textArrayList.size());
        int i = 0;
        for (String line : textArrayList) {

            paragraph = document.createParagraph();
            //create paragraph
            paragraph.setSpacingBetween(1.5);
            paragraph.setStyle("numList");
            paragraph.setNumID(addListStyle(document));
            paragraph.setIndentFromLeft(700);
            paragraph.setIndentFromRight(
                    1420); //equals to 2,5 cm from the right border line ----> 56,8 or approx 57 equals 1 mm
            textRun = paragraph.createRun();

            //Set Italic, Blue
            textRun.setItalic(optionsLoader.getOptions().isTaskItalic());
            textRun.setBold(optionsLoader.getOptions().isTaskBold());
            textRun.setUnderline((optionsLoader.getOptions().isTaskUnderscored()) ?
                    UnderlinePatterns.SINGLE :
                    UnderlinePatterns.NONE);
            textRun.setFontSize(optionsLoader.getOptions().getTaskFontSize());
            textRun.setText(line.trim());
            textRun.setColor(optionsLoader.getOptions().getTaskColor());

            XWPFParagraph para2 = document.createParagraph();
            para2.setSpacingBetween(1.5);
            para2.setIndentFromLeft(767);
            para2.setIndentFromRight(1000);
            //para2.setStyle("empTxtID");
            emptySpaceRun = para2.createRun();
            emptySpaceRun.setColor(
                    optionsLoader.getOptions().getAnswerColor()); //only does something as long as an empty space exists
            emptySpaceRun.setItalic(optionsLoader.getOptions().isAnswerItalic());
            emptySpaceRun.setBold(optionsLoader.getOptions().isAnswerBold());
            emptySpaceRun.setUnderline(optionsLoader.getOptions().isAnswerUnderscored() ?
                    UnderlinePatterns.SINGLE :
                    UnderlinePatterns.NONE);
            emptySpaceRun.setFontSize(optionsLoader.getOptions().getAnswerFontSize());
            emptySpaceRun.setText(" ");

            updateProgress(i++, textArrayList.size());

        }

        document.write(out);
        out.close();
        System.out.println("File refactored in path: " + savePath + "\nWritten successfully.");
        updateProgress(i + 1, textArrayList.size());
    }

    @Override
    protected Void call() throws Exception {
        generateDoc();
        return null;
    }
}
