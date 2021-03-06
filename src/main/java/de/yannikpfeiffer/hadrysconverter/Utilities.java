package de.yannikpfeiffer.hadrysconverter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class Utilities {
    public static HBox createHBox(Node... nodes) {
        return createHBox(new Insets(0), nodes);
    }

    public static HBox createHBox(Insets padding, Node... nodes) {
        HBox hbox = new HBox();

        for (int i = 0; i < nodes.length; i++) {
            if (i > 0) {
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                hbox.getChildren().add(region);
            }
            hbox.getChildren().add(nodes[i]);
        }

        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(padding);
        return hbox;
    }
}
