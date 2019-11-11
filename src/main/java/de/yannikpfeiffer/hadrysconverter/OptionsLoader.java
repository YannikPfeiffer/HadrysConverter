package de.yannikpfeiffer.hadrysconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class OptionsLoader {
    Options options;

    public OptionsLoader() {
        this.options = new Options();
    }

    public void loadOptions() {
        Path home = Path.of(System.getProperty("user.home"));
        File settingsFile = home.resolve(".hadrysConverter-settings.json").toFile();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            if (settingsFile.createNewFile()) {
                objectMapper.writeValue(settingsFile, new Options());
            } else {
                options = objectMapper.readValue(settingsFile, Options.class);
                System.out.println(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
