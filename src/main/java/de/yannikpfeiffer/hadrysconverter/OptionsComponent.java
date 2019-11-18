package de.yannikpfeiffer.hadrysconverter;

import de.yannikpfeiffer.hadrysconverter.optionloading.Options;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;

public class OptionsComponent {

    private Options options;
    // Components
    private InlineCssTextArea previewArea;
    private GridPane optionsPane;
    private ScrollPane scrollPane;
    private TitledPane optionsTitledPane;
    private Label generalOptionsLabel;
    private Label titleLabel;
    private TextField titleTextField;
    private Label formatLabel;
    private Label taskLabel;
    private String tableHeadingStyle;
    private Label answerLabel;
    private Label colorLabel;
    private ColorPicker taskColorPicker;
    private ColorPicker answerColorPicker;
    private Label fontSizeLabel;
    private Spinner<Integer> taskFontSizeSpinner;
    private Spinner<Integer> answerFontSizeSpinner;
    private Label decorationsLabel;
    private ToggleButton taskBoldButton;
    private ToggleButton taskItalicButton;
    private ToggleButton taskUnderscoredButton;
    private ToggleButton answerBoldButton;
    private ToggleButton answerItalicButton;
    private ToggleButton answerUnderscoredButton;

    OptionsComponent(Options options, InlineCssTextArea previewArea) {
        this.options = options;
        this.previewArea = previewArea;
    }

    TitledPane buildComponent() {
        optionsPane = new GridPane();
        optionsPane.setAlignment(Pos.CENTER);
        optionsPane.setHgap(25);
        optionsPane.setVgap(10);
        optionsPane.setPadding(new Insets(10));

        scrollPane = new ScrollPane(optionsPane);

        optionsTitledPane = new TitledPane("Erweiterte Einstellungen", scrollPane);
        optionsTitledPane.setExpanded(false);
        VBox.setMargin(optionsTitledPane, new Insets(10));

        generalOptionsLabel = new Label("Generelle Optionen");
        generalOptionsLabel.setStyle("-fx-font-size: 12pt");

        Button resetToStandardButton = new Button("Zurücksetzen");
        resetToStandardButton.setOnAction(event -> {
            options.reset();
            stylePreview();
            fillFields();
        });
        GridPane.setHalignment(resetToStandardButton, HPos.RIGHT);

        optionsPane.addRow(optionsPane.getRowCount(), generalOptionsLabel, new Region(), resetToStandardButton);

        titleLabel = new Label("Titel:");

        titleTextField = new TextField(options.getTitle());
        titleTextField.setOnKeyTyped(event -> {
            options.setTitle(titleTextField.getText());
            stylePreview();
        });
        optionsPane.addRow(optionsPane.getRowCount(), titleLabel, titleTextField);

        formatLabel = new Label("Format");
        formatLabel.setStyle("-fx-font-size: 12pt");
        optionsPane.addRow(optionsPane.getRowCount(), formatLabel);

        //Label taskColorLabel = new Label("Farbe:");
        taskLabel = new Label("Aufgabenstellung");
        tableHeadingStyle = "-fx-font-size: 11pt;";
        taskLabel.setStyle(tableHeadingStyle);
        answerLabel = new Label("Antwort");
        answerLabel.setStyle(tableHeadingStyle);
        optionsPane.addRow(optionsPane.getRowCount(), new Label(""), taskLabel, answerLabel);

        colorLabel = new Label("Farbe");
        colorLabel.setStyle(tableHeadingStyle);

        taskColorPicker = new ColorPicker(Color.web(options.getTaskColor()));
        taskColorPicker.setOnAction(event -> {
            options.setTaskColor(getColorAsHexString(taskColorPicker.getValue()));
            stylePreview();
        });
        taskColorPicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(taskColorPicker, true);

        answerColorPicker = new ColorPicker(Color.web(options.getAnswerColor()));
        answerColorPicker.setOnAction(event -> {
            options.setAnswerColor(getColorAsHexString(answerColorPicker.getValue()));
            stylePreview();
        });
        answerColorPicker.setMaxWidth(Double.MAX_VALUE);

        optionsPane.addRow(optionsPane.getRowCount(), colorLabel, taskColorPicker, answerColorPicker);

        fontSizeLabel = new Label("Schriftgröße");
        fontSizeLabel.setStyle(tableHeadingStyle);

        taskFontSizeSpinner = new Spinner<>(1, 25, options.getTaskFontSize());
        taskFontSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            options.setTaskFontSize(newValue);
            stylePreview();
        });
        GridPane.setFillWidth(taskFontSizeSpinner, true);

        answerFontSizeSpinner = new Spinner<>(1, 25, options.getAnswerFontSize());
        answerFontSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            options.setAnswerFontSize(newValue);
            stylePreview();
        });

        optionsPane.addRow(optionsPane.getRowCount(), fontSizeLabel, taskFontSizeSpinner, answerFontSizeSpinner);

        decorationsLabel = new Label("Dekorationen");
        decorationsLabel.setStyle(tableHeadingStyle);

        taskBoldButton = new ToggleButton("B");
        taskBoldButton.setStyle("-fx-font-weight: bold");
        taskBoldButton.setSelected(options.isTaskBold());
        taskBoldButton.setOnAction(event -> {
            options.setTaskBold(!options.isTaskBold());
            taskBoldButton.setSelected(options.isTaskBold());
            stylePreview();
        });

        taskItalicButton = new ToggleButton("K");
        taskItalicButton.setStyle("-fx-font-style: italic");
        taskItalicButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskItalicButton.setSelected(options.isTaskItalic());
        taskItalicButton.setOnAction(event -> {
            options.setTaskItalic(!options.isTaskItalic());
            taskItalicButton.setSelected(options.isTaskItalic());
            stylePreview();
        });

        taskUnderscoredButton = new ToggleButton("U");
        taskUnderscoredButton.setStyle("-fx-underline: true");
        taskUnderscoredButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskUnderscoredButton.setSelected(options.isTaskUnderscored());
        taskUnderscoredButton.setOnAction(event -> {
            options.setTaskUnderscored(!options.isTaskUnderscored());
            taskUnderscoredButton.setSelected(options.isTaskUnderscored());
            stylePreview();
        });

        answerBoldButton = new ToggleButton("B");
        answerBoldButton.setStyle("-fx-font-weight: bold");
        answerBoldButton.setSelected(options.isAnswerBold());
        answerBoldButton.setOnAction(event -> {
            options.setAnswerBold(!options.isAnswerBold());
            answerBoldButton.setSelected(options.isAnswerBold());
            stylePreview();
        });

        answerItalicButton = new ToggleButton("K");
        answerItalicButton.setStyle("-fx-font-style: italic");
        answerItalicButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerItalicButton.setSelected(options.isAnswerItalic());
        answerItalicButton.setOnAction(event -> {
            options.setAnswerItalic(!options.isAnswerItalic());
            answerItalicButton.setSelected(options.isAnswerItalic());
            stylePreview();
        });

        answerUnderscoredButton = new ToggleButton("U");
        answerUnderscoredButton.setStyle("-fx-underline: true");
        answerUnderscoredButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerUnderscoredButton.setSelected(options.isAnswerUnderscored());
        answerUnderscoredButton.setOnAction(event -> {
            options.setAnswerUnderscored(!options.isAnswerUnderscored());
            answerUnderscoredButton.setSelected(options.isAnswerUnderscored());
            stylePreview();
        });

        optionsPane.addRow(optionsPane.getRowCount(), decorationsLabel,
                Utilities.createHBox(taskBoldButton, taskItalicButton, taskUnderscoredButton),
                Utilities.createHBox(answerBoldButton, answerItalicButton, answerUnderscoredButton));

        stylePreview();

        return optionsTitledPane;
    }

    private void fillFields() {
        titleTextField.setText(options.getTitle());

        taskColorPicker.setValue(Color.web(options.getTaskColor()));
        taskFontSizeSpinner.getValueFactory().setValue(options.getTaskFontSize());
        taskBoldButton.setSelected(options.isTaskBold());
        taskItalicButton.setSelected(options.isTaskItalic());
        taskUnderscoredButton.setSelected(options.isTaskUnderscored());

        answerColorPicker.setValue(Color.web(options.getAnswerColor()));
        answerFontSizeSpinner.getValueFactory().setValue(options.getAnswerFontSize());
        answerBoldButton.setSelected(options.isAnswerBold());
        answerItalicButton.setSelected(options.isAnswerItalic());
        answerUnderscoredButton.setSelected(options.isAnswerUnderscored());
    }

    private void stylePreview() {
        previewArea.replaceText(options.getTitle()
                + "\n1.\tBitte beantworten Sie diese Frage\n\tLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy");
        String titleStyle = "-fx-font-size: 16pt; -fx-fill: #13152d;";
        String taskStyle = String.format(
                "-fx-fill: #%s; -fx-font-style: %s; -fx-font-size: %dpt; -fx-font-weight: %s; -fx-underline: %b;",
                options.getTaskColor(), (options.isTaskItalic() ? "italic" : "normal"), options.getTaskFontSize(),
                (options.isTaskBold() ? "bold" : "normal"), options.isTaskUnderscored());

        String answerStyle = String.format(
                "-fx-fill: #%s; -fx-font-style: %s; -fx-font-size: %dpt; -fx-font-weight: %s; -fx-underline: %b;",
                options.getAnswerColor(), (options.isAnswerItalic() ? "italic" : "normal"), options.getAnswerFontSize(),
                options.isAnswerBold() ? "bold" : "normal", options.isAnswerUnderscored());

        previewArea.setStyle(0, titleStyle);
        previewArea.setStyle(1, taskStyle);
        previewArea.setStyle(2, answerStyle);
    }

    private String getColorAsHexString(Color color) {
        return String.format("%02x%02x%02x", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
