package de.yannikpfeiffer.hadrysconverter.optionloading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
public class Options implements Serializable {
    @JsonSerialize(converter = PathToStringConverter.class)
    @JsonDeserialize(converter = StringToPathConverter.class)
    private Path inputPath;
    private String firstName;
    private String lastName;
    private int exerciseNumber;
    @JsonSerialize(converter = PathToStringConverter.class)
    @JsonDeserialize(converter = StringToPathConverter.class)
    private Path outputPath;
    private String title;
    private String taskColor;
    private boolean taskItalic;
    private boolean taskBold;
    private boolean taskUnderscored;
    private int taskFontSize;
    private String answerColor;
    private boolean answerItalic;
    private boolean answerBold;
    private boolean answerUnderscored;
    private int answerFontSize;
    private String numberingRegex;

    public Options() {
        reset();
    }

    public void reset() {
        this.inputPath = Path.of(System.getProperty("user.home"));
        this.firstName = "";
        this.lastName = "";
        this.outputPath = Path.of(System.getProperty("user.home") + "/Desktop");

        this.title = "Aufgaben";
        this.numberingRegex = ".";

        this.taskColor = "166b99";
        this.taskItalic = true;
        this.taskBold = false;
        this.taskUnderscored = false;
        this.taskFontSize = 14;

        this.answerColor = "13152d";
        this.answerItalic = false;
        this.answerBold = false;
        this.answerUnderscored = false;
        this.answerFontSize = 11;

    }

    @Override
    public String toString() {
        return "Options{" + "inputPath=" + inputPath + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", exerciseNumber=" + exerciseNumber + ", outputPath=" + outputPath + '}';
    }

}


