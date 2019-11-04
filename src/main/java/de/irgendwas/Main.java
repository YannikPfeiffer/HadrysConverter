package de.irgendwas;

import org.apache.commons.cli.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("w", "Create a word document");
        options.addRequiredOption("i", "inputFile", true, "The pdf file that should be parsed");
        options.addRequiredOption("o", "outputPath", true, "The path pf the output word file");
        options.addRequiredOption("n", "name", true, "The name of the new file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            PDFReader pdfReader = new PDFReader();
            ArrayList<String> text = pdfReader.getTextFromFile(cmd.getOptionValue("i"));
            text.forEach(System.out::println);
            if (cmd.hasOption("w")) {
                WordGenerator wordGenerator = new WordGenerator();
                wordGenerator.generateDoc(
                        text,
                        (cmd.hasOption('o') ? cmd.getOptionValue("o") : (System.getProperty("user.home") + "/Desktop")),
                        cmd.getOptionValue("n"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
