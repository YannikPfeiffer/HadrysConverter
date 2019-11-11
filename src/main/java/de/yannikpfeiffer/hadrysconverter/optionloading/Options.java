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
    @JsonSerialize(converter = PathToStringConverter.class)
    @JsonDeserialize(converter = StringToPathConverter.class)
    private Path outputPath;

    public Options() {
        this(Path.of(System.getProperty("user.home")), "Max", "Mustermann",
                Path.of(System.getProperty("user.home") + "/Desktop"));
    }

    public Options(Path inputPath, String firstName, String lastName, Path outputPath) {
        this.inputPath = inputPath;
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public String toString() {
        return "Options{" + "inputPath=" + inputPath + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", outputPath=" + outputPath + '}';
    }
}


