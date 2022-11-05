package cs1302.gallery;

import javafx.scene.layout.HBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;

/**
 * Custom component that represents the bottom bar of the application
 * holds the progress bar and tag.
 */
public class BottomBar extends HBox {

    ProgressBar progress;

    /**
     * Constructor, nuff said.?
     */
    BottomBar() {
        super();
        progress = new ProgressBar();
        Label label = new Label("Images provided courtesy of iTunes");
        this.getChildren().addAll(progress, label);
    }
}
