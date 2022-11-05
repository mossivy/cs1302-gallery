package cs1302.gallery;

import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;


/**
 * Custom component that represents a VBox. This is where the images go.
 */
public class GalleryLoader extends VBox {

    protected static final String DEFAULT_IMG =
         "http://cobweb.cs.uga.edu/~mec/cs1302/gui/default.png";
     /** Default height and width for Images. */
    protected static final int DEF_HEIGHT = 100;
    protected static final int DEF_WIDTH = 100;

    ImageView imgView;
    GalleryApp mainApp;

    /**
     * Construcotr for the gallery loader.
     *
     * @param mainApp GalleryApp reference
     */
    public GalleryLoader(GalleryApp mainApp) {
        super();
        this.mainApp = mainApp;
        Image img = new Image(DEFAULT_IMG, DEF_HEIGHT, DEF_WIDTH, false, false);
        imgView = new ImageView(img);
        imgView.setPreserveRatio(true);
        this.getChildren().addAll(imgView);
    }

    /**
     * Calls the runimageLater method in GallerApp but passes with Imageview to change.
     *
     * @param index passes the index of which image to use
     */
    void loadImage(int index) {
        try {
            mainApp.runImageLater(imgView, index);
        } catch (IllegalArgumentException iae) {
            System.out.println("The supplied URL is invalid");
        }
    }
}
