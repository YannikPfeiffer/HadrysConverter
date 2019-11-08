package de.irgendwas;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HadrysConverterUi extends Application {

    private VBox vBox;
    private Label title;
    private GridPane pane;
    private Label fileLabel;
    private Label filePathLabel;
    private Button selectFileBtn;
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

        fileLabel = new Label("Datei mit Aufgaben");

        filePathLabel = new Label();
        filePathLabel.setMaxWidth(150);

        selectFileBtn = new Button("Datei auswählen");
        selectFileBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(selectFileBtn, HPos.CENTER);
        GridPane.setFillWidth(selectFileBtn, true);
        selectFileBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Aufgabenblatt auswählen");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
            File inputFile = fileChooser.showOpenDialog(primaryStage);
            filePathLabel.setText(inputFile.getPath());
        });
        pane.addRow(0, fileLabel, filePathLabel, selectFileBtn);

        nameLabel = new Label("Name");

        firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");

        pane.addRow(1, nameLabel, firstNameField, lastNameField);

        numberLabel = new Label("Nummer der Übung");
        pane.add(numberLabel, 0, 2);

        numberSpinner = new Spinner<>(1, 20, 1, 1);
        GridPane.setHgrow(numberSpinner, Priority.ALWAYS);
        numberLabel.setLabelFor(numberSpinner);
        GridPane.setFillWidth(numberSpinner, true);
        pane.add(numberSpinner, 2, 2, 2, 1);

        outputLabel = new Label("Ausgabe-Verzeichnis");

        outputPathLabel = new Label("");
        outputPathLabel.setMaxWidth(150);

        outputPathBtn = new Button("Pfad auswählen");
        GridPane.setHalignment(outputPathBtn, HPos.CENTER);
        outputPathBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(outputPathBtn, true);
        outputPathBtn.setOnAction(event -> {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Ausgabe-Verzeichnis auswählen");
            File outputDir = directoryChooser.showDialog(primaryStage);
            outputPathLabel.setText(outputDir.getPath());
        });
        pane.add(outputLabel, 0, 3);
        pane.add(outputPathLabel, 1, 3);
        pane.add(outputPathBtn, 2, 3);

        vBox.getChildren().add(pane);

        generateButton = new Button("Generiere Datei");
        generateButton.setOnAction(event -> {
            if (!validateInput(primaryStage)) {
                return;
            }

            PDFReader pdfReader = new PDFReader();

            ArrayList<String> text = null;
            try {
                text = pdfReader.getTextFromFile(filePathLabel.getText());
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
        alert.initOwner(parentStage);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait();
    }

    private boolean validateInput(Stage parentStage) {
        if (filePathLabel.getText().isEmpty()) {
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
