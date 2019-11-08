package de.irgendwas;

import org.apache.commons.cli.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("w").desc("Create a word document").build());
        options.addOption(Option.builder("n")
                .longOpt("name")
                .hasArg()
                .argName("name")
                .desc("Your name as LastName,FirstName")
                .build());
        options.addOption(Option.builder("o")
                .longOpt("outputPath")
                .hasArg()
                .argName("path")
                .desc("The path of the output word file")
                .build());
        options.addOption(Option.builder("i")
                .longOpt("number")
                .hasArg()
                .argName("number")
                .desc("The Number of the Übung")
                .build());
        options.addOption(Option.builder().longOpt("help").desc("Prints help page").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("hadrysConverter <inputFile>", options, true);
                return;
            }

            if (cmd.getArgList().isEmpty()) {
                throw new IllegalArgumentException("You have to supply a file to parse");
            }

            PDFReader pdfReader = new PDFReader();
            ArrayList<String> text = pdfReader.getTextFromFile(cmd.getArgs()[0]);
            text.forEach(System.out::println);

            if (cmd.hasOption("w")) {

                Integer exerciseNumber = Integer.valueOf(cmd.getOptionValue("i", "1"));

                String[] names = cmd.getOptionValue("n", "Max,Mustermann").split(",");
                String name = names[0] + "," + names[1];

                WordGenerator wordGenerator = new WordGenerator();

                wordGenerator.generateDoc(text, cmd.getOptionValue("o", "."), names, exerciseNumber);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
