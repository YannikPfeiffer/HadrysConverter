package de.yannikpfeiffer.hadrysconverter.optionloading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class OptionsLoader {
    private Options options;
    private File settingsFile;

    public OptionsLoader() {
        this.options = new Options();
        settingsFile = Path.of(System.getProperty("user.home")).resolve(".hadrysConverter-settings.json").toFile();
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public void loadOptions() throws IOException {
        if (settingsFile.createNewFile()) {
            saveOptions();
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            options = objectMapper.readValue(settingsFile, Options.class);
        }
    }

    public void saveOptions() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(settingsFile, options);
    }

}
