package de.irgendwas;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class HadrysConverterUi extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("HadrysConverter");
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(5, 0, 5, 0));

        Label title = new Label("Hadrys-Converter");
        title.setFont(new Font(25));
        root.getChildren().add(title);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.setPadding(new Insets(5));
        pane.setMinWidth(primaryStage.getWidth());

        Label fileLabel = new Label("Datei mit Aufgaben");

        Label filePathLabel = new Label();
        filePathLabel.setMaxWidth(180);
        Button selectFileBtn = new Button("Datei auswählen");
        GridPane.setHalignment(selectFileBtn, HPos.CENTER);
        selectFileBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Aufgabenblatt auswählen");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
            File inputFile = fileChooser.showOpenDialog(primaryStage);
            filePathLabel.setText(inputFile.getPath());
        });
        pane.addRow(0, fileLabel, filePathLabel, selectFileBtn);

        Label nameLabel = new Label("Name");
        TextField firstNameField = new TextField("Vorname");
        TextField lastNameField = new TextField("Nachname");
        pane.addRow(1, nameLabel, firstNameField, lastNameField);

        Label numberLabel = new Label("Nummer der Übung");
        Spinner<Integer> numberSpinner = new Spinner<>(1, 20, 1, 1);
        numberLabel.setLabelFor(numberSpinner);
        pane.add(numberLabel, 0, 2);
        GridPane.setHgrow(numberSpinner, Priority.ALWAYS);
        GridPane.setFillWidth(numberSpinner, true);
        pane.add(numberSpinner, 1, 2, 2, 1);

        Label outputLabel = new Label("Ordner in den gespeichert werden soll");
        Label outputPathLabel = new Label("");
        outputLabel.setMaxWidth(180);
        Button outputPathBtn = new Button("Pfad auswählen");
        GridPane.setHalignment(outputPathBtn, HPos.CENTER);
        outputPathBtn.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Ziel Ordner auswählen");
            File outputDir = directoryChooser.showDialog(primaryStage);
            outputPathLabel.setText(outputDir.getPath());
        });
        pane.addRow(3, outputLabel, outputPathLabel, outputPathBtn);

        Button generateButton = new Button("Generiere Datei");
        Label successLabel = new Label();
        successLabel.setTextFill(Paint.valueOf("#00aa00"));
        successLabel.setTextAlignment(TextAlignment.CENTER);
        successLabel.setFont(new Font(20));
        GridPane.setHalignment(generateButton, HPos.CENTER);
        generateButton.setOnAction(event -> {
            PDFReader pdfReader = new PDFReader();
            ArrayList<String> text = pdfReader.getTextFromFile(filePathLabel.getText());
            text.forEach(System.out::println);

            WordGenerator wordGenerator = new WordGenerator();

            Integer number = numberSpinner.getValue();

            wordGenerator.generateDoc(text, outputPathLabel.getText(),
                    String.format("%02d", number) + " - " + lastNameField.getText() + "," + firstNameField.getText());
            successLabel.setText("Die Datei wurde erfolgreich erstellt");
        });
        pane.add(generateButton, 2, 4);
        pane.add(successLabel, 0, 5, 3, 1);

        root.getChildren().add(pane);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
