package de.yannikpfeiffer.hadrysconverter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.yannikpfeiffer.hadrysconverter.optionloading.Options;
import de.yannikpfeiffer.hadrysconverter.optionloading.OptionsLoader;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class HadrysConverterUi extends Application {

    private OptionsLoader optionsLoader;
    // Components
    private VBox vBox;
    private Label title;
    private GridPane pane;
    private Label inputFileLabel;
    private Label inputFilePathLabel;
    private Button inputFileButton;
    private Label nameLabel;
    private TextField firstNameField;
    private TextField lastNameField;
    private Label numberLabel;
    private Spinner<Integer> numberSpinner;
    private Label outputLabel;
    private Label outputPathLabel;
    private Button outputPathBtn;
    private Button generateButton;
    private DirectoryChooser directoryChooser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hadrys-Converter");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        loadOptions(primaryStage);

        vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5, 0, 5, 0));

        title = new Label("Hadrys-Converter");
        title.setFont(new Font(25));
        vBox.getChildren().add(title);

        pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.setPadding(new Insets(5));
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setMinWidth(primaryStage.getWidth());

        inputFileLabel = new Label("Datei mit Aufgaben");

        inputFilePathLabel = new Label();
        inputFilePathLabel.setPrefWidth(285);
        inputFilePathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        inputFilePathLabel.setText(optionsLoader.getOptions().getInputPath().toString());

        inputFileButton = new Button("Datei auswählen");
        inputFileButton.setMaxWidth(Double.MAX_VALUE);
        inputFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(inputFilePathLabel.getText()).getParentFile());
            fileChooser.setTitle("Aufgabenblatt auswählen");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
            File inputFile = fileChooser.showOpenDialog(primaryStage);
            if (inputFile != null) {
                inputFilePathLabel.setText(inputFile.getPath());
            }
        });
        pane.addRow(0, inputFileLabel, createHBox(inputFilePathLabel, inputFileButton));

        nameLabel = new Label("Name");

        firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(195);
        firstNameField.setText(optionsLoader.getOptions().getFirstName());
        lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(195);
        lastNameField.setText(optionsLoader.getOptions().getLastName());

        pane.addRow(1, nameLabel, createHBox(firstNameField, lastNameField));

        numberLabel = new Label("Nummer der Übung");
        pane.add(numberLabel, 0, 2);

        numberSpinner = new Spinner<>(1, 20, 1, 1);
        GridPane.setHgrow(numberSpinner, Priority.ALWAYS);
        numberSpinner.getValueFactory().setValue(optionsLoader.getOptions().getExerciseNumber());
        numberLabel.setLabelFor(numberSpinner);
        numberSpinner.setMaxWidth(Double.MAX_VALUE);
        HBox numberHBox = createHBox(numberSpinner);
        numberSpinner.setPrefWidth(400);
        pane.add(numberHBox, 1, 2);

        outputLabel = new Label("Ausgabe-Verzeichnis");

        outputPathLabel = new Label("");
        outputPathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        outputPathLabel.setText(optionsLoader.getOptions().getOutputPath().toString());
        HBox.setHgrow(outputLabel, Priority.ALWAYS);

        outputPathBtn = new Button("Pfad auswählen");
        outputPathBtn.setPrefWidth(inputFileButton.getPrefWidth());
        outputPathLabel.setPrefWidth(285);
        outputPathBtn.setMaxWidth(Double.MAX_VALUE);
        outputPathBtn.setOnAction(event -> {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(outputPathLabel.getText()));
            directoryChooser.setTitle("Ausgabe-Verzeichnis auswählen");
            File outputDir = directoryChooser.showDialog(primaryStage);
            if (outputDir != null) {
                outputPathLabel.setText(outputDir.getPath());
            }
        });
        pane.addRow(3, outputLabel, createHBox(outputPathLabel, outputPathBtn));

        vBox.getChildren().add(pane);

        generateButton = new Button("Generiere Datei");
        generateButton.setOnAction(event -> {
            if (!validateInput(primaryStage)) {
                return;
            }

            try {
                optionsLoader.saveOptions(new Options(Path.of(inputFilePathLabel.getText()), firstNameField.getText(),
                        lastNameField.getText(), numberSpinner.getValue(), Path.of(outputPathLabel.getText())));
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(primaryStage, "The Options could not be saved");
            }

            PDFReader pdfReader = new PDFReader();

            ArrayList<String> text = null;
            try {
                text = pdfReader.getTextFromFile(inputFilePathLabel.getText());
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(primaryStage, "Die Datei konnte nicht gelesen werden.");
                return;
            }
            text.forEach(System.out::println);

            WordGenerator wordGenerator = new WordGenerator();

            try {
                wordGenerator.generateDoc(text, outputPathLabel.getText(),
                        new String[] { lastNameField.getText(), firstNameField.getText() }, numberSpinner.getValue());
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(primaryStage, "Die Datei konnte nicht erstellt werden");
                return;
            }
            showSuccessDialog(primaryStage);
        });
        VBox.setMargin(generateButton, new Insets(10, 0, 10, 0));
        vBox.getChildren().add(generateButton);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHBox(Node... nodes) {
        HBox hbox = new HBox(nodes);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPrefWidth(400);
        //        hbox.setBorder(new Border(new BorderStroke(Paint.valueOf("#ababab"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        return hbox;
    }

    private void loadOptions(Stage parentStage) {
        optionsLoader = new OptionsLoader();
        try {
            optionsLoader.loadOptions();
        } catch (JsonMappingException | JsonParseException e) {
            //            e.printStackTrace();
            try {
                optionsLoader.saveOptions(new Options());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            showErrorDialog(
                    parentStage,
                    "Die Datei mit den gespeicherten Einstellungen entspricht nicht dem Standard und wurde daher überschrieben");
        } catch (IOException e) {
            //            e.printStackTrace();
            showErrorDialog(parentStage, "Die gespeicherten Einstellungen konnten nicht geladen werden");
        }
    }

    private void showSuccessDialog(Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erfolg!");
        alert.setHeaderText("");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(parentStage);
        alert.setContentText("Die Datei wurde erfolgreich erstellt");

        alert.showAndWait();
    }

    private void showErrorDialog(Stage parentStage, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Es ist ein Fehler aufgetreten");
        alert.setContentText(message);
        if (parentStage.isShowing()) {
            alert.initOwner(parentStage);
        }
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait();
    }

    private boolean validateInput(Stage parentStage) {
        if (inputFilePathLabel.getText().isEmpty()) {
            showErrorDialog(parentStage, "Es muss ein Aufgabenblatt angegeben sein.");
            return false;
        }

        if (outputPathLabel.getText().isEmpty()) {
            showErrorDialog(parentStage, "Es muss ein Ausgabe-Verzeichnis angegeben werden");
            return false;
        }

        if (firstNameField.getText().isBlank()) {
            showErrorDialog(parentStage, "Es muss ein Vorname angegeben werden");
            return false;
        }

        if (!firstNameField.getText().trim().matches("^[a-zA-ZüäöÜÄÖ\\-]+$")) {
            showErrorDialog(parentStage, "Der Vorname darf nur Buchstaben(A-Z, Ä, Ü, Ö) und Bindestriche enthalten.");
            return false;
        }

        if (lastNameField.getText().isBlank()) {
            showErrorDialog(parentStage, "Es muss ein Nachname angegeben werden");
            return false;
        }

        if (!lastNameField.getText().trim().matches("^[a-zA-ZüöäÜÖÄ\\-]+$")) {
            showErrorDialog(parentStage, "Der Nachname darf nur Buchstaben(A-Z, Ä, Ü, Ö) und Bindestriche  enthalten.");
            return false;
        }

        return true;
    }
}
