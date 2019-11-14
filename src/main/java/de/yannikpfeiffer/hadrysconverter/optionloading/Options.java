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
    private String taskFontStyle;
    private int taskFontSize;
    private String answerColor;
    private String answerFontStyle;
    private int answerFontSize;

    public Options() {
        this.inputPath = Path.of(System.getProperty("user.home"));
        this.firstName = "";
        this.lastName = "";
        this.outputPath = Path.of(System.getProperty("user.home") + "/Desktop");

        this.title = "Aufgaben";

        this.taskColor = "166b99";
        this.taskFontStyle = "italic";
        this.taskFontSize = 14;

        this.answerColor = "13152d";
        this.answerFontStyle = "normal";
        this.answerFontSize = 11;
    }




    @Override
    public String toString() {
        return "Options{" + "inputPath=" + inputPath + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", exerciseNumber=" + exerciseNumber + ", outputPath=" + outputPath + '}';
    }

}


