package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.application.Platform;
import javafx.scene.layout.Priority;


/**
 * Represents an iTunes GalleryApp!
 * Coded By Henry Moss Ivy for CS1302 @UGA
 * Professor: Dr. David Coterell
 */
public class GalleryApp extends Application {

    private GalleryLoader[] gallery; // holds customcomponents that store the images
    private boolean isPaused;
    Image[] images; // holds the downloaded images
    BottomBar bottomBar;

    @Override
    /** {@inheritdoc} */
    public void start(Stage stage) {
        VBox pane = new VBox(10);
        isPaused = false;
        CustomMenu customMenu = new CustomMenu();
        TilePane tile = new TilePane();
        tile.setHgap(0);
        tile.setPrefColumns(5);
        gallery = new GalleryLoader[20];
        for (int i = 0; i < 20; i++) {
            gallery[i] = new GalleryLoader(this);
            tile.getChildren().add(gallery[i]);
        }
        bottomBar = new BottomBar();

        TopBar topBar = new TopBar(this);

        pane.getChildren().addAll(customMenu, topBar, tile, bottomBar);
        pane.setVgrow(tile, Priority.ALWAYS);
        stage.sizeToScene();
        Scene scene = new Scene(pane);
        stage.setMaxWidth(640);
        stage.setMaxHeight(680);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    } // start

    /**
     * Calls the setProgress on the progress bar.
     *
     * @param value progress of the progress bar
     */
    public void progressBar(double value) {
        bottomBar.progress.setProgress(value);
    }

    /**
     * Returns a galleryloader object.
     *
     * @return gallery[index]
     * @param index which galleryLoader to return
     */
    public GalleryLoader getLoader(int index) {
        return gallery[index];
    }

    /**
     * Returns whether the random replacement of images  is paused or not.
     *
     * @return isPaused whether it is paused or not
     */
    public boolean getPause() {
        return isPaused;
    }

    /**
     * Sets the pause state.
     *
     * @param p whether random replacement should be paused or not.
     */
    public void setPause(boolean p) {
        isPaused = p;
    }

    /**
     * This method used to put the changing of images
     * on Platfor.runLater but it caused many a bugs
     * so now it just calls the .setImage Method with
     * amd puts the image from an instance variable in this
     * class into it.
     *
     * @param imgView which Imageview to change
     * @param index index of which image in the image array
     */
    public void runImageLater(ImageView imgView, int index) {
        if (images[index] == null) {
            System.out.println("Not yet allocated");
        }
        // tried using platform runlater but some weird syncing stuff happen
        // with the random replacement of images and it takes a long time
        // for it to start occuring
        //Platform.runLater(() -> imgView.setImage(images[index]));
        imgView.setImage(images[index]);
    }

    /**
     * Sets the size of the image array. Also creates a new array.
     *
     * @param size size of the new array
     */
    void setImageArraySize(int size) {
        images = new Image[size];
    }

    /**
     * Downloads the image and stores it in an image array.
     *
     * @param i index of where  to store the image
     * @param imageUrl the url of the image to be downloaded
     */
    void downLoadImage(int i, String imageUrl) {
        try {
            images[i] = new Image(imageUrl, 100, 100, false, false);
        } catch (IllegalArgumentException iae) {
            System.out.println("The supplied URL is invalid");
        }
    }
} // GalleryApp
