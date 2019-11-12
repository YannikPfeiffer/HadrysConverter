package de.yannikpfeiffer.hadrysconverter.optionloading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.nio.file.Path;

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

    public Options() {
        this(Path.of(System.getProperty("user.home")), "", "", 1,
                Path.of(System.getProperty("user.home") + "/Desktop"));
    }

    public Options(Path inputPath, String firstName, String lastName, int exerciseNumber, Path outputPath) {
        this.inputPath = inputPath;
        this.firstName = firstName;
        this.lastName = lastName;
        this.exerciseNumber = exerciseNumber;
        this.outputPath = outputPath;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public void setInputPath(Path inputPath) {
        this.inputPath = inputPath;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    @Override
    public String toString() {
        return "Options{" + "inputPath=" + inputPath + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", exerciseNumber=" + exerciseNumber + ", outputPath=" + outputPath + '}';
    }
}


