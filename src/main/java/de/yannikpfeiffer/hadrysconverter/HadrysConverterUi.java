package de.yannikpfeiffer.hadrysconverter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.yannikpfeiffer.hadrysconverter.optionloading.Options;
import de.yannikpfeiffer.hadrysconverter.optionloading.OptionsLoader;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HadrysConverterUi extends Application {

    private OptionsLoader optionsLoader;
    // Components
    private VBox vBox;
    private Label title;
    private GridPane gridPane;
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
    private InlineCssTextArea previewArea;
    private CheckBox openFileCheckbox;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    private Label progressLabel;
    private OptionsComponent optionsComponent;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        loadOptions(primaryStage);

        vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5, 0, 5, 0));

        title = new Label("Hadrys-Converter");
        title.setFont(new Font(25));
        vBox.getChildren().add(title);

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5));
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setMinWidth(primaryStage.getWidth());

        inputFileLabel = new Label("Datei mit Aufgaben");

        inputFilePathLabel = new Label();
        inputFilePathLabel.setPrefWidth(285);
        inputFilePathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        inputFilePathLabel.setText(optionsLoader.getOptions().getInputPath().toString());

        inputFileButton = new Button("Datei auswählen");
        inputFileButton.setMaxWidth(Double.MAX_VALUE);
        inputFileButton.setMinWidth(110);
        inputFileButton.setOnAction(event -> chooseInputFile(primaryStage));
        gridPane.addRow(0, inputFileLabel, Utilities.createHBox(inputFilePathLabel, inputFileButton));

        nameLabel = new Label("Name");

        firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(195);
        firstNameField.setText(optionsLoader.getOptions().getFirstName());
        lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(195);
        lastNameField.setText(optionsLoader.getOptions().getLastName());

        gridPane.addRow(1, nameLabel, Utilities.createHBox(firstNameField, lastNameField));

        numberLabel = new Label("Nummer der Übung");
        gridPane.add(numberLabel, 0, 2);

        numberSpinner = new Spinner<>(1, 20, 1, 1);
        GridPane.setHgrow(numberSpinner, Priority.ALWAYS);
        numberSpinner.getValueFactory().setValue(optionsLoader.getOptions().getExerciseNumber());
        numberLabel.setLabelFor(numberSpinner);
        numberSpinner.setMaxWidth(Double.MAX_VALUE);
        HBox numberHBox = Utilities.createHBox(numberSpinner);
        numberSpinner.setPrefWidth(400);
        gridPane.add(numberHBox, 1, 2);

        outputLabel = new Label("Ausgabe-Verzeichnis");

        outputPathLabel = new Label("");
        outputPathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        outputPathLabel.setText(optionsLoader.getOptions().getOutputPath().toString());
        HBox.setHgrow(outputLabel, Priority.ALWAYS);

        outputPathBtn = new Button("Pfad auswählen");
        outputPathBtn.setPrefWidth(inputFileButton.getPrefWidth());
        outputPathLabel.setPrefWidth(285);

        outputPathBtn.setMaxWidth(Double.MAX_VALUE);
        outputPathBtn.setMinWidth(inputFileButton.getMinWidth());
        outputPathBtn.setOnAction(event -> chooseDirectory(primaryStage));
        gridPane.addRow(3, outputLabel, Utilities.createHBox(outputPathLabel, outputPathBtn));

        vBox.getChildren().add(gridPane);

        previewArea = new InlineCssTextArea();
        previewArea.setEffect(new DropShadow());
        previewArea.setMaxWidth(550);
        previewArea.setMinHeight(125);
        previewArea.setPadding(new Insets(5));
        previewArea.setEditable(false);
        previewArea.setWrapText(true);
        VBox.setMargin(previewArea, new Insets(10));
        vBox.getChildren().add(previewArea);

        optionsComponent = new OptionsComponent(optionsLoader.getOptions(), previewArea);
        vBox.getChildren().add(optionsComponent.buildComponent());

        openFileCheckbox = new CheckBox("Datei nach Generierung öffnen?");

        generateButton = new Button("Generiere Datei");
        generateButton.setDefaultButton(true);

        progressBar = new ProgressBar();
        progressBar.progressProperty().unbind();
        progressBar.setVisible(false);

        progressIndicator = new ProgressIndicator();
        progressIndicator.progressProperty().unbind();
        progressIndicator.setVisible(false);

        generateButton.setOnAction(event -> {
            readPDF(primaryStage);
        });

        progressLabel = new Label();
        progressLabel.setVisible(false);

        vBox.getChildren()
                .add(Utilities.createHBox(new Insets(5, 10, 5, 10), openFileCheckbox, progressLabel, progressBar,
                        progressIndicator, generateButton));
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hadrys-Converter");
        //        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void chooseInputFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        File file = new File(inputFilePathLabel.getText());
        fileChooser.setInitialDirectory(file.isFile() ? file.getParentFile() : file);
        fileChooser.setTitle("Aufgabenblatt auswählen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
        File inputFile = null;
        try {
            inputFile = fileChooser.showOpenDialog(primaryStage);
        } catch (IllegalArgumentException e) {
            optionsLoader.getOptions().setInputPath(Path.of(System.getProperty("user.home")));
            inputFilePathLabel.setText(optionsLoader.getOptions().getInputPath().toString());
            chooseInputFile(primaryStage);
        }
        if (inputFile != null) {
            inputFilePathLabel.setText(inputFile.getPath());

            // Try to get exercise number from file name
            Pattern pattern = Pattern.compile("^(\\d{2}) - .*");
            String name = inputFile.getName();
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                String numberString = matcher.group(1);
                if (numberString != null) {
                    numberSpinner.getValueFactory().setValue(Integer.valueOf(numberString));
                }
            }
        }
    }

    private void readPDF(Stage primaryStage) {
        if (!validateInput(primaryStage)) {
            return;
        }

        try {
            optionsLoader.getOptions().setInputPath(Path.of(inputFilePathLabel.getText()));
            optionsLoader.getOptions().setFirstName(firstNameField.getText());
            optionsLoader.getOptions().setLastName(lastNameField.getText());
            optionsLoader.getOptions().setExerciseNumber(numberSpinner.getValue());
            optionsLoader.getOptions().setOutputPath(Path.of(outputPathLabel.getText()));
            optionsLoader.saveOptions();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog(primaryStage, "The Options could not be saved");
        }

        try {
            ReadingTask readingTask = new ReadingTask(inputFilePathLabel.getText(), optionsLoader.getOptions());
            bindTaskToProgressBar(readingTask);
            readingTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
                ArrayList<String> text = readingTask.getValue();
                text.forEach(System.out::println);
                progressLabel.setVisible(false);
                progressBar.setVisible(false);
                progressIndicator.setVisible(false);
                generateDocument(primaryStage, text);
            });
            new Thread(readingTask).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(primaryStage, "Die Datei konnte nicht gelesen werden.");
            progressBar.setVisible(false);
        }

    }

    private void generateDocument(Stage primaryStage, ArrayList<String> text) {
        progressLabel.textProperty().unbind();
        progressLabel.setText("Generiere Datei");
        WordGenerator wordGenerator = new WordGenerator(text, numberSpinner.getValue(),
                new String[] { lastNameField.getText(), firstNameField.getText() }, outputPathLabel.getText(),
                optionsLoader);
        bindTaskToProgressBar(wordGenerator);
        wordGenerator.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            handleSuccess(primaryStage);
        });
        try {
            new Thread(wordGenerator).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(
                    primaryStage,
                    "Die Datei konnte nicht erstellt werden. Falls Sie eine bestehende Datei überschreiben, "
                            + "schließen Sie bitte alle offenen Anwendungen, welche auf diese zugreifen.");
        }
    }

    private void bindTaskToProgressBar(Task task) {
        progressBar.setVisible(true);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());
        progressLabel.setVisible(true);
        progressLabel.textProperty().unbind();
        progressLabel.textProperty().bind(task.titleProperty());
        progressIndicator.setVisible(true);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(task.progressProperty());
    }

    private void handleSuccess(Stage primaryStage) {
        showSuccessDialog(primaryStage);

        if (openFileCheckbox.isSelected()) {
            String filePath = outputPathLabel.getText() + "/" + String.format("%02d", numberSpinner.getValue()) + "-"
                    + lastNameField.getText() + "," + firstNameField.getText() + ".docx";
            System.out.println(filePath);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "winword", filePath);
            processBuilder.redirectErrorStream(true);
            try {
                processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseDirectory(Stage primaryStage) {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(outputPathLabel.getText()));
        directoryChooser.setTitle("Ausgabe-Verzeichnis auswählen");
        File outputDir = directoryChooser.showDialog(primaryStage);
        if (outputDir != null) {
            outputPathLabel.setText(outputDir.getPath());
        }
    }

    private void loadOptions(Stage parentStage) {
        optionsLoader = new OptionsLoader();
        try {
            optionsLoader.loadOptions();
        } catch (JsonMappingException | JsonParseException e) {
            //            e.printStackTrace();
            try {
                optionsLoader.setOptions(new Options());
                optionsLoader.saveOptions();
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

        if (!firstNameField.getText().trim().matches("^[a-zA-ZüäöÜÄÖß\\-]+$")) {
            showErrorDialog(parentStage, "Der Vorname darf nur Buchstaben(A-Z, Ä, Ü, Ö) und Bindestriche enthalten.");
            return false;
        }

        if (lastNameField.getText().isBlank()) {
            showErrorDialog(parentStage, "Es muss ein Nachname angegeben werden");
            return false;
        }

        if (!lastNameField.getText().trim().matches("^[a-zA-ZüöäÜÖÄß\\-]+$")) {
            showErrorDialog(parentStage, "Der Nachname darf nur Buchstaben(A-Z, Ä, Ü, Ö) und Bindestriche  enthalten.");
            return false;
        }

        return true;
    }
}
