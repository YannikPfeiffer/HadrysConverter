package de.yannikpfeiffer.hadrysconverter.optionloading;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.nio.file.Path;

public class StringToPathConverter extends StdConverter<String, Path> {
    @Override
    public Path convert(String s) {
        return Path.of(s);
    }
}
