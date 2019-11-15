package de.yannikpfeiffer.hadrysconverter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.yannikpfeiffer.hadrysconverter.optionloading.Options;
import de.yannikpfeiffer.hadrysconverter.optionloading.OptionsLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
        gridPane.addRow(0, inputFileLabel, createHBox(inputFilePathLabel, inputFileButton));

        nameLabel = new Label("Name");

        firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(195);
        firstNameField.setText(optionsLoader.getOptions().getFirstName());
        lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(195);
        lastNameField.setText(optionsLoader.getOptions().getLastName());

        gridPane.addRow(1, nameLabel, createHBox(firstNameField, lastNameField));

        numberLabel = new Label("Nummer der Übung");
        gridPane.add(numberLabel, 0, 2);

        numberSpinner = new Spinner<>(1, 20, 1, 1);
        GridPane.setHgrow(numberSpinner, Priority.ALWAYS);
        numberSpinner.getValueFactory().setValue(optionsLoader.getOptions().getExerciseNumber());
        numberLabel.setLabelFor(numberSpinner);
        numberSpinner.setMaxWidth(Double.MAX_VALUE);
        HBox numberHBox = createHBox(numberSpinner);
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
        gridPane.addRow(3, outputLabel, createHBox(outputPathLabel, outputPathBtn));

        vBox.getChildren().add(gridPane);

        previewArea = new InlineCssTextArea();
        previewArea.setEffect(new DropShadow());
        previewArea.setMaxWidth(550);
        previewArea.setMinHeight(125);
        previewArea.setPadding(new Insets(5));
        stylePreview();
        previewArea.setEditable(false);
        previewArea.setWrapText(true);
        VBox.setMargin(previewArea, new Insets(10));
        vBox.getChildren().add(previewArea);

        GridPane optionsPane = new GridPane();
        optionsPane.setAlignment(Pos.CENTER);
        optionsPane.setHgap(25);
        optionsPane.setVgap(10);
        optionsPane.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(optionsPane);
        TitledPane optionsTitledPane = new TitledPane("Erweiterte Einstellungen", scrollPane);
        optionsTitledPane.setExpanded(true); // TODO: auf true setzen
        VBox.setMargin(optionsTitledPane, new Insets(10));

        Label generalOptionsLabel = new Label("Generelle Optionen");
        generalOptionsLabel.setStyle("-fx-font-size: 12pt");
        optionsPane.addRow(optionsPane.getRowCount(), generalOptionsLabel);

        Label titleLabel = new Label("Titel:");

        TextField titleTextField = new TextField(optionsLoader.getOptions().getTitle());
        titleTextField.setOnKeyTyped(event -> {
            optionsLoader.getOptions().setTitle(titleTextField.getText());
            stylePreview();
        });
        optionsPane.addRow(optionsPane.getRowCount(), titleLabel, titleTextField);

        Label formatLabel = new Label("Format");
        formatLabel.setStyle("-fx-font-size: 12pt");
        optionsPane.addRow(optionsPane.getRowCount(), formatLabel);

        //Label taskColorLabel = new Label("Farbe:");
        Label taskLabel = new Label("Aufgabenstellung");
        String tableHeadingStyle = "-fx-font-size: 11pt;";
        taskLabel.setStyle(tableHeadingStyle);
        Label answerLabel = new Label("Antwort");
        answerLabel.setStyle(tableHeadingStyle);
        optionsPane.addRow(optionsPane.getRowCount(), new Label(""), taskLabel, answerLabel);

        Label colorLabel = new Label("Farbe");
        colorLabel.setStyle(tableHeadingStyle);

        ColorPicker taskColorPicker = new ColorPicker(Color.web(optionsLoader.getOptions().getTaskColor()));
        taskColorPicker.setOnAction(event -> {
            optionsLoader.getOptions().setTaskColor(getColorAsHexString(taskColorPicker.getValue()));
            stylePreview();
        });
        taskColorPicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(taskColorPicker, true);

        ColorPicker answerColorPicker = new ColorPicker(Color.web(optionsLoader.getOptions().getAnswerColor()));
        answerColorPicker.setOnAction(event -> {
            optionsLoader.getOptions().setAnswerColor(getColorAsHexString(answerColorPicker.getValue()));
            stylePreview();
        });
        answerColorPicker.setMaxWidth(Double.MAX_VALUE);

        optionsPane.addRow(optionsPane.getRowCount(), colorLabel, taskColorPicker, answerColorPicker);

        Label fontSizeLabel = new Label("Schriftgröße");
        fontSizeLabel.setStyle(tableHeadingStyle);

        Spinner<Integer> taskFontSizeSpinner = new Spinner<>(1, 25, optionsLoader.getOptions().getTaskFontSize());
        taskFontSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            optionsLoader.getOptions().setTaskFontSize(newValue);
            stylePreview();
        });
        GridPane.setFillWidth(taskFontSizeSpinner, true);

        Spinner<Integer> answerFontSizeSpinner = new Spinner<>(1, 25, optionsLoader.getOptions().getAnswerFontSize());
        answerFontSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            optionsLoader.getOptions().setAnswerFontSize(newValue);
            stylePreview();
        });

        optionsPane.addRow(optionsPane.getRowCount(), fontSizeLabel, taskFontSizeSpinner, answerFontSizeSpinner);

        Label decorationsLabel = new Label("Dekorationen");
        decorationsLabel.setStyle(tableHeadingStyle);

        ToggleButton taskBoldButton = new ToggleButton("B");
        taskBoldButton.setStyle("-fx-font-weight: bold");
        taskBoldButton.setSelected(optionsLoader.getOptions().isTaskBold());
        taskBoldButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isTaskBold()) {
                optionsLoader.getOptions().setTaskBold(false);
            } else {
                optionsLoader.getOptions().setTaskBold(true);
            }
            taskBoldButton.setSelected(optionsLoader.getOptions().isTaskBold());
            stylePreview();
        });

        ToggleButton taskItalicButton = new ToggleButton("K");
        taskItalicButton.setStyle("-fx-font-style: italic");
        taskItalicButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskItalicButton.setSelected(optionsLoader.getOptions().isTaskItalic());
        taskItalicButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isTaskItalic()) {
                optionsLoader.getOptions().setTaskItalic(false);
            } else {
                optionsLoader.getOptions().setTaskItalic(true);
            }
            taskItalicButton.setSelected(optionsLoader.getOptions().isTaskItalic());
            stylePreview();
        });

        ToggleButton taskUnderscoredButton = new ToggleButton("U");
        taskUnderscoredButton.setStyle("-fx-underline: true");
        taskUnderscoredButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskUnderscoredButton.setSelected(optionsLoader.getOptions().isTaskUnderscored());
        taskUnderscoredButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isTaskUnderscored()) {
                optionsLoader.getOptions().setTaskUnderscored(false);
            } else {
                optionsLoader.getOptions().setTaskUnderscored(true);
            }
            taskUnderscoredButton.setSelected(optionsLoader.getOptions().isTaskUnderscored());
            stylePreview();
        });

        ToggleButton answerBoldButton = new ToggleButton("B");
        answerBoldButton.setStyle("-fx-font-weight: bold");
        answerBoldButton.setSelected(optionsLoader.getOptions().isAnswerBold());
        answerBoldButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isAnswerBold()) {
                optionsLoader.getOptions().setAnswerBold(false);
            } else {
                optionsLoader.getOptions().setAnswerBold(true);
            }
            answerBoldButton.setSelected(optionsLoader.getOptions().isAnswerBold());
            stylePreview();
        });

        ToggleButton answerItalicButton = new ToggleButton("K");
        answerItalicButton.setStyle("-fx-font-style: italic");
        answerItalicButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerItalicButton.setSelected(optionsLoader.getOptions().isAnswerItalic());
        answerItalicButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isAnswerItalic()) {
                optionsLoader.getOptions().setAnswerItalic(false);
            } else {
                optionsLoader.getOptions().setAnswerItalic(true);
            }
            answerItalicButton.setSelected(optionsLoader.getOptions().isAnswerItalic());
            stylePreview();
        });

        ToggleButton answerUnderscoredButton = new ToggleButton("U");
        answerUnderscoredButton.setStyle("-fx-underline: true");
        answerUnderscoredButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerUnderscoredButton.setSelected(optionsLoader.getOptions().isAnswerUnderscored());
        answerUnderscoredButton.setOnAction(event -> {
            if (optionsLoader.getOptions().isAnswerUnderscored()) {
                optionsLoader.getOptions().setAnswerUnderscored(false);
            } else {
                optionsLoader.getOptions().setAnswerUnderscored(true);
            }
            answerUnderscoredButton.setSelected(optionsLoader.getOptions().isAnswerUnderscored());
            stylePreview();
        });

        optionsPane.addRow(optionsPane.getRowCount(), decorationsLabel,
                createHBox(taskBoldButton, taskItalicButton, taskUnderscoredButton),
                createHBox(answerBoldButton, answerItalicButton, answerUnderscoredButton));

        vBox.getChildren().add(optionsTitledPane);

        generateButton = new Button("Generiere Datei");
        generateButton.setOnAction(event -> generateDocument(primaryStage));
        VBox.setMargin(generateButton, new Insets(10, 0, 10, 0));
        vBox.getChildren().add(generateButton);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hadrys-Converter");
        //        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private String getColorAsHexString(Color color) {
        return String.format("%02x%02x%02x", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void stylePreview() {
        previewArea.replaceText(optionsLoader.getOptions().getTitle()
                + "\n1.\tBitte beantworten Sie diese Frage\n\tLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy");
        String titleStyle = "-fx-font-size: 16pt; -fx-fill: #13152d;";
        String taskStyle = String.format(
                "-fx-fill: #%s; -fx-font-style: %s; -fx-font-size: %dpt; -fx-font-weight: %s; -fx-underline: %b;",
                optionsLoader.getOptions().getTaskColor(),
                (optionsLoader.getOptions().isTaskItalic() ? "italic" : "normal"),
                optionsLoader.getOptions().getTaskFontSize(),
                (optionsLoader.getOptions().isTaskBold() ? "bold" : "normal"),
                optionsLoader.getOptions().isTaskUnderscored());

        String answerStyle = String.format(
                "-fx-fill: #%s; -fx-font-style: %s; -fx-font-size: %dpt; -fx-font-weight: %s; -fx-underline: %b;",
                optionsLoader.getOptions().getAnswerColor(),
                (optionsLoader.getOptions().isAnswerItalic() ? "italic" : "normal"),
                optionsLoader.getOptions().getAnswerFontSize(),
                optionsLoader.getOptions().isAnswerBold() ? "bold" : "normal",
                optionsLoader.getOptions().isAnswerUnderscored());

        previewArea.setStyle(0, titleStyle);
        previewArea.setStyle(1, taskStyle);
        previewArea.setStyle(2, answerStyle);
    }

    private void chooseInputFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(inputFilePathLabel.getText()).getParentFile());
        fileChooser.setTitle("Aufgabenblatt auswählen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf"));
        File inputFile = fileChooser.showOpenDialog(primaryStage);
        if (inputFile != null) {
            inputFilePathLabel.setText(inputFile.getPath());
        }

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

    private void generateDocument(Stage primaryStage) {
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

    private HBox createHBox(Node... nodes) {
        HBox hbox = new HBox();

        for (int i = 0; i < nodes.length; i++) {
            if (i > 0) {
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                hbox.getChildren().add(region);
            }
            hbox.getChildren().add(nodes[i]);
        }

        //        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        //        hbox.setPrefWidth(400);
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
