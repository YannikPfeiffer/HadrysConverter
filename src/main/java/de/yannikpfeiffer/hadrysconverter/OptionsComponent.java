package de.yannikpfeiffer.hadrysconverter;

import de.yannikpfeiffer.hadrysconverter.optionloading.Options;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;

public class OptionsComponent {

    private Options options;
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
        optionsTitledPane.setExpanded(true); // TODO: auf true setzen
        VBox.setMargin(optionsTitledPane, new Insets(10));

        generalOptionsLabel = new Label("Generelle Optionen");
        generalOptionsLabel.setStyle("-fx-font-size: 12pt");
        optionsPane.addRow(optionsPane.getRowCount(), generalOptionsLabel);

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
            if (options.isTaskBold()) {
                options.setTaskBold(false);
            } else {
                options.setTaskBold(true);
            }
            taskBoldButton.setSelected(options.isTaskBold());
            stylePreview();
        });

        taskItalicButton = new ToggleButton("K");
        taskItalicButton.setStyle("-fx-font-style: italic");
        taskItalicButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskItalicButton.setSelected(options.isTaskItalic());
        taskItalicButton.setOnAction(event -> {
            if (options.isTaskItalic()) {
                options.setTaskItalic(false);
            } else {
                options.setTaskItalic(true);
            }
            taskItalicButton.setSelected(options.isTaskItalic());
            stylePreview();
        });

        taskUnderscoredButton = new ToggleButton("U");
        taskUnderscoredButton.setStyle("-fx-underline: true");
        taskUnderscoredButton.setPrefWidth(taskBoldButton.getPrefWidth());
        taskUnderscoredButton.setSelected(options.isTaskUnderscored());
        taskUnderscoredButton.setOnAction(event -> {
            if (options.isTaskUnderscored()) {
                options.setTaskUnderscored(false);
            } else {
                options.setTaskUnderscored(true);
            }
            taskUnderscoredButton.setSelected(options.isTaskUnderscored());
            stylePreview();
        });

        answerBoldButton = new ToggleButton("B");
        answerBoldButton.setStyle("-fx-font-weight: bold");
        answerBoldButton.setSelected(options.isAnswerBold());
        answerBoldButton.setOnAction(event -> {
            if (options.isAnswerBold()) {
                options.setAnswerBold(false);
            } else {
                options.setAnswerBold(true);
            }
            answerBoldButton.setSelected(options.isAnswerBold());
            stylePreview();
        });

        answerItalicButton = new ToggleButton("K");
        answerItalicButton.setStyle("-fx-font-style: italic");
        answerItalicButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerItalicButton.setSelected(options.isAnswerItalic());
        answerItalicButton.setOnAction(event -> {
            if (options.isAnswerItalic()) {
                options.setAnswerItalic(false);
            } else {
                options.setAnswerItalic(true);
            }
            answerItalicButton.setSelected(options.isAnswerItalic());
            stylePreview();
        });

        answerUnderscoredButton = new ToggleButton("U");
        answerUnderscoredButton.setStyle("-fx-underline: true");
        answerUnderscoredButton.setPrefWidth(answerBoldButton.getPrefWidth());
        answerUnderscoredButton.setSelected(options.isAnswerUnderscored());
        answerUnderscoredButton.setOnAction(event -> {
            if (options.isAnswerUnderscored()) {
                options.setAnswerUnderscored(false);
            } else {
                options.setAnswerUnderscored(true);
            }
            answerUnderscoredButton.setSelected(options.isAnswerUnderscored());
            stylePreview();
        });

        optionsPane.addRow(
                optionsPane.getRowCount(), decorationsLabel,
                Utilities.createHBox(taskBoldButton, taskItalicButton, taskUnderscoredButton),
                Utilities.createHBox(answerBoldButton, answerItalicButton, answerUnderscoredButton));

        stylePreview();

        return optionsTitledPane;
    }

    private void stylePreview() {
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
