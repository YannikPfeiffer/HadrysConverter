package de.yannikpfeiffer.hadrysconverter;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.nio.file.Path;

public class PathToStringConverter extends StdConverter<Path, String> {
    @Override
    public String convert(Path path) {
        return path.toAbsolutePath().toString();
    }
}
