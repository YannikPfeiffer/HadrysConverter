package de.irgendwas;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Pane imagePane = new Pane();

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
                    new String[] { lastNameField.getText(), firstNameField.getText() }, numberSpinner.getValue());
            successLabel.setText("Die Datei wurde erfolgreich erstellt");
        });
        pane.add(generateButton, 2, 4);
        pane.add(successLabel, 0, 5, 3, 1);

        //background customization

        if (true) {
            String pathToImages = "./hadyrsImages/";
            pathToImages = Paths.get("").toAbsolutePath().toString()+"/hadrysImages/";
            ClassLoader loader = HadrysConverterUi.class.getClassLoader();
            String imageName = "hadrys_stylized_layer";
            ArrayList<Image> images = new ArrayList<>();
            ArrayList<ImageView> imageViews = new ArrayList<>();

            Blend blush;
            ColorAdjust monochrome;
            monochrome = new ColorAdjust();
            monochrome.setSaturation(-1.0);


            for (int i = 0; i != 4; i++) {
                try {
                    images.add(new Image(new FileInputStream(pathToImages + imageName + i + ".png")));
                    System.out.println(System.getProperty("user.dir"));
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                imageViews.add(new ImageView(images.get(i)));
                ImageView localImageView = imageViews.get(i);
                localImageView.setClip(new ImageView(images.get(i)));

                blush = new Blend(
                        BlendMode.MULTIPLY,
                        monochrome,
                        new ColorInput(
                                0,
                                0,
                                localImageView.getImage().getWidth(),
                                localImageView.getImage().getHeight(),
                                Color.RED
                        )
                );
                localImageView.effectProperty().bind(
                        Bindings
                                .when(localImageView.hoverProperty())
                                .then((Effect) blush)
                                .otherwise((Effect) null)
                );
                localImageView.setCache(true);
                localImageView.setCacheHint(CacheHint.SPEED);

                localImageView.setX(50);
                localImageView.setY(50);
            }

            for (ImageView imageView : imageViews
            ) {
                imagePane.getChildren().add(imageView);
            }
        }

        if (false) {
            root.setId("pane");

            ClassLoader classLoader = getClass().getClassLoader();
            URL response = classLoader.getResource("style.css");
        }

        root.getChildren().addAll(pane,imagePane);
        Scene scene = new Scene(root, 500, 500);

        if (false) {
            root.setStyle("-fx-background-image: url('hadrysImages/hardys_stylized_layer1.png');\n" +
                    //  "    -fx-background-repeat: stretch;\n" +
                    "    -fx-background-size: 500 506;\n" +
                    "    -fx-background-position: center center;");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
