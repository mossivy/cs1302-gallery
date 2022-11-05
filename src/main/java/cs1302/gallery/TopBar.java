package cs1302.gallery;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import java.net.URLEncoder;
import java.net.URL;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.util.Random;
import javafx.util.Duration;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

/**
 * Custom component that represents the main toolbar of the application.
 * Most of the brains of the program is held here since I figured all user interaction
 * is routed through this HBox.
 *
 * I'm so glad to be done with this lol... Debugging sucked and I made threads quite large
 * to make this work.
 */
public class TopBar extends HBox {

    //instance variables
    Button load;
    Button pause;
    private GalleryApp mainApp; // many calls to the GalleryApp... Holds a reference
    TextField urlField;
    JsonObject result;
    JsonArray results;
    int numResults; // numResults from the Json... later turned into num of distinct Urls
    URL url;
    InputStreamReader reader;
    JsonElement je, artworkUrl100;
    JsonObject root;
    String[] imageUrls; //store the imgUrls in a String Array

    /**
     * Private function to get a random number.
     * This function helps with the randomReplacement of images
     *
     * @param min minimum number to be generated
     * @param max maximum number to be generated
     * @return random number within range
     */
    private int getRandom(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("getRandom exception");
        }
        Random number = new Random();
        return min + number.nextInt((max - min) + 1);
    }

    // Eventhandler that later goes into a Keyframe to be continuosly called
    EventHandler<ActionEvent> handler = event -> {
        try {
            int randomImg = getRandom(0, 19);
            int randomQuery = getRandom(20, numResults - 1);
            Image tempImage = mainApp.images[randomImg];
            mainApp.getLoader(randomImg).loadImage(randomQuery);
            mainApp.images[randomImg] = mainApp.images[randomQuery];
            mainApp.images[randomQuery] = tempImage;
        } catch (IllegalArgumentException iae) {
            System.out.println("Random image replace error");
        }
    };
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
    Timeline timeline = new Timeline();

    /**
     * Constuctor for the TopBar class; it is a HBox that holds two buttons
     * and a search bar.
     *
     * @param mainApp pass the main Application object here
     */
    public TopBar(GalleryApp mainApp) {
        super(5); //call to HBox constroctor
        this.mainApp = mainApp;
        load = new Button("Update images");
        pause = new Button("Pause");
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        urlField = new TextField("");
        Label label = new Label("Search Query:");
        urlField.setText("pop"); // Lol super hacky way to get initial results but it works
        jSonInit(new ActionEvent()); //jSonInit is seen later but it gets the jSon parsed
        //and then loads the gallery
        mainApp.setPause(false);
        urlField.setText("");
        load.setOnAction(this::jSonInit); // load button action
        pause.setOnAction(this::pauseGallery); // pause button action

        this.getChildren().addAll(pause, separator, label, urlField, load);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);

    }

    /**
     * This method puts the download and replacement of images into a thread.
     * I tried to get the continous call to the random replacement of images
     * outside this thread but it caused weird sync issues.
     *
     * @return r Runnable
     */
    private Runnable downLoadAndLoad() {
        Runnable r = () -> {
            timeline.pause(); // stop random replacement of images while loading
            for (int i = 0; i < numResults; i++) {
                // these two method calls are used a lot in this class
                // see GalleryApp for more details on the methods
                mainApp.downLoadImage(i, imageUrls[i]);
                mainApp.progressBar(1.0 *  i / numResults);
            }
            for (int i = 0; i < 20; i++) {
                // a little bit of a complicated method call since it first calls GalleryApp method
                // which then calls GalleryLoader method
                // which again calls GalleryApp method for Platform.runLater
                // but it organizes each Tile into its own custom components while still threading
                mainApp.getLoader(i).loadImage(i);
            }
            if (!mainApp.getPause()) {
                timeline.play(); // start random replacement of images
            }
        };
        return r;
    }

    /**
     * Loads the gallery of images, pretty straightforward.
     *
     * @param e event from button press (comes from jSonUnit)
     */
    void loadGallery(ActionEvent e) {
        try {
            mainApp.setImageArraySize(numResults);
            mainApp.progressBar(0);
            Runnable r = downLoadAndLoad();
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.start();

        } catch (IllegalArgumentException iae) {
            System.out.println("Load error");
        }
    }

    /**
     * This is the method that gets called whenever the load button is pressed.
     * First gets the text from the url field and then gets a json from the itunes API
     * then it puts the urls into an array via {@code initImgUrls} if there are enough
     * distinct urls, then {@loadGallery} is called... otherwise an alert error is produced.
     *
     * @param e ActionEvent from load button being pressed.
     */
    private void jSonInit(ActionEvent e) {
        try {
            int originalNumResults = numResults;
            String sUrl = "https://itunes.apple.com/search?term="
                + URLEncoder.encode(urlField.getText(), "UTF-8") + "&limit=100&media=music";
            url = new URL(sUrl);
            reader = new InputStreamReader(url.openStream());
            je = JsonParser.parseReader(reader);
            root = je.getAsJsonObject();
            results  = root.getAsJsonArray("results");
            numResults = results.size();
            if (initImgUrls()) {
                loadGallery(e);
            } else {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Error");
                errorAlert.setContentText("Not enough distinct results... try different query.");
                errorAlert.setResizable(true);
                errorAlert.show();
                numResults = originalNumResults;
            }
        } catch (UnsupportedEncodingException uee) {
            System.out.println("JSON error");
        } catch (MalformedURLException mue) {
            System.out.println("Malformed URL");
        } catch (IOException ioe) {
            System.out.println("IO error");
        }
    }

    /**
     * Method to setup the image urls and check if there is enough distinct urls.
     *
     * @return distinct returns whether there is enough distinct urls or not
     */
    boolean initImgUrls() {
        String[] unDistinctImageUrls = new String[numResults]; // holds all jSon urls returned
        String[] tempUrls = new String[numResults]; // holds distinct urls

        int numDistinct = 0;
        boolean duplicate;
        for (int i = 0; i < numResults; i++) {
            duplicate = false;
            result = results.get(i).getAsJsonObject();
            artworkUrl100 = result.get("artworkUrl100");
            unDistinctImageUrls[i] = artworkUrl100.getAsString();
            for (int n = 0; n < i && !duplicate; n++) {
                if (unDistinctImageUrls[n].equals(unDistinctImageUrls[i])) {
                    duplicate = true;
                }
            }
            if (!duplicate) {
                tempUrls[numDistinct] = unDistinctImageUrls[i];
                numDistinct++;
            }
        }
        if (numDistinct >= 21) {
            imageUrls = new String[numDistinct];
            numResults = numDistinct;
            for (int i = 0; i < numResults; i++) {
                imageUrls[i] = tempUrls[i];
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to interpret the press of the pause button into the play and pause of
     * of the KeyFrame.
     *
     * @param e actionevent from the pause button being pressed.
     */
    void pauseGallery(ActionEvent e) {
        try {
            if (mainApp.getPause()) {
                pause.setText("Pause");
                timeline.play();
                mainApp.setPause(false);
            } else {
                pause.setText("Play");
                timeline.pause();
                mainApp.setPause(true);
            }

        } catch (IllegalArgumentException iae) {
            System.out.println("Pause error");
        }
    }
}
