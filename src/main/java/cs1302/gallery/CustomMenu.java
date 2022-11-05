package cs1302.gallery;

import javafx.scene.layout.HBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.event.ActionEvent;

/**
 * Represents the top menu. Is an HBox.
 */
public class CustomMenu extends HBox {

    /**
     * Constructor for CustomMenu, again pretty straightforward.
     */
    public CustomMenu() {
        super();
        Menu menu1 = new Menu("File");
        MenuItem menuItem = new MenuItem("Exit");
        menuItem.setOnAction(e -> System.exit(0));
        MenuBar menu = new MenuBar();
        menu1.getItems().add(menuItem);
        menu.getMenus().add(menu1);
        this.getChildren().add(menu);
    }
}
