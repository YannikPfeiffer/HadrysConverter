package de.irgendwas;

import org.apache.commons.cli.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("w", "Create a word document");
        options.addOption("i", "inputFile", true, "The pdf file that should be parsed");
        options.addOption("o", "outputPath", true, "The path pf the output word file");
        options.addOption("n", "name", true, "The name of the new file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            PDFReader pdfReader = new PDFReader();
            ArrayList<String> text = pdfReader.getTextFromFile(cmd.getOptionValue("i"));
            text.forEach(System.out::println);
            if (cmd.hasOption("w")) {
                if (!(cmd.hasOption('i') && cmd.hasOption('o') && cmd.hasOption('n'))) {
                    throw new IllegalArgumentException("If option w is set, options i, o and n also have to be set, too");
                }
                WordGenerator wordGenerator = new WordGenerator();
                wordGenerator.stylizeDocument(text, cmd.getOptionValue("o"), cmd.getOptionValue("n"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
