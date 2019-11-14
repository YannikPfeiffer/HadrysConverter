package de.yannikpfeiffer.hadrysconverter.optionloading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public Options() {

        this(Path.of(System.getProperty("user.home")), "", "", 1,
                Path.of(System.getProperty("user.home") + "/Desktop"), "Aufgaben");
    }




    @Override
    public String toString() {
        return "Options{" + "inputPath=" + inputPath + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", exerciseNumber=" + exerciseNumber + ", outputPath=" + outputPath + '}';
    }

}


