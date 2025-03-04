package com.example.imagegalleryapp;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.List;

public class HelloApplication extends Application {
    private GridPane gridPane;
    private List<String> imagePaths;
    private ImageView mainImageView;
    private ImageView nextImageView;
    private int currentImageIndex = 0;
    private Timeline slideshowTimeline;
    private boolean slideshowRunning = false;

    @Override
    public void start(Stage stage) {
        imagePaths = List.of(
                "images/image1.jpeg", "images/image2.jpeg", "images/image3.jpeg",
                "images/image4.jpeg", "images/image5.jpeg", "images/image6.jpeg",
                "images/image7.jpeg", "images/image8.jpeg", "images/image9.jpeg"
        );

        // Create buttons with actions
        Button btnNext = createStyledButton("Next");
        Button btnBack = createStyledButton("Back");
        Button btnSlideshow = createStyledButton("Start Slideshow");

        HBox buttonBox = new HBox(10, btnBack, btnNext, btnSlideshow);
        buttonBox.setAlignment(Pos.CENTER);

        // Create the grid layout for thumbnails
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // StackPane to hold the current and next image on top of each other
        StackPane imageStackPane = new StackPane();
        mainImageView = new ImageView();
        nextImageView = new ImageView();
        imageStackPane.getChildren().addAll(mainImageView, nextImageView);

        // Set image visibility initially
        mainImageView.setVisible(false);
        nextImageView.setVisible(false);

        // Load thumbnail images
        loadThumbnails();

        // Set up button actions
        btnNext.setOnAction(e -> showNextImage());
        btnBack.setOnAction(e -> resetView());
        btnSlideshow.setOnAction(e -> toggleSlideshow(btnSlideshow));

        VBox layout = new VBox(20, gridPane, imageStackPane, buttonBox);
        layout.setAlignment(Pos.CENTER);

        // Set background color and padding for the VBox
        layout.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // Create a scene with a background color
        Scene scene = new Scene(layout, 900, 850);
        scene.setFill(Color.LIGHTBLUE); // Light blue color for the scene

        // Set the title and scene for the stage
        stage.setTitle("Internet Gallery App");
        stage.setScene(scene);
        stage.show();
    }

    private void loadThumbnails() {
        gridPane.getChildren().clear();
        int col = 0, row = 0;

        for (String path : imagePaths) {
            InputStream imageStream = getClass().getResourceAsStream("/" + path);
            if (imageStream == null) {
                System.err.println("Error: Image not found -> " + path);
                continue;
            }

            Image image = new Image(imageStream, 110, 110, true, true);
            ImageView thumbnail = new ImageView(image);
            thumbnail.setFitWidth(110);
            thumbnail.setFitHeight(110);
            thumbnail.setPreserveRatio(true);

            // Clip the image to be circular
            Circle clip = new Circle(55);
            clip.setCenterX(55);
            clip.setCenterY(55);
            thumbnail.setClip(clip);

            // Add hover effects on thumbnails
            thumbnail.setOnMouseEntered(e -> thumbnail.setOpacity(0.7));
            thumbnail.setOnMouseExited(e -> thumbnail.setOpacity(1.0));
            thumbnail.setOnMouseClicked(e -> showFullImage(path));

            gridPane.add(thumbnail, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void showFullImage(String imagePath) {
        InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
        if (imageStream == null) {
            System.err.println("Error: Full-size image not found -> " + imagePath);
            return;
        }

        Image fullImage = new Image(imageStream);
        mainImageView.setImage(fullImage);
        mainImageView.setVisible(true);
        gridPane.setVisible(false);
    }

    private void showNextImage() {
        currentImageIndex = (currentImageIndex + 1) % imagePaths.size();
        String nextImagePath = imagePaths.get(currentImageIndex);

        // Load the next image into nextImageView
        InputStream imageStream = getClass().getResourceAsStream("/" + nextImagePath);
        if (imageStream == null) {
            System.err.println("Error: Full-size image not found -> " + nextImagePath);
            return;
        }
        Image nextImage = new Image(imageStream);
        nextImageView.setImage(nextImage);
        nextImageView.setVisible(true);

        // Apply fade transition for main image
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), mainImageView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            mainImageView.setImage(nextImage);
            mainImageView.setVisible(true);

            // Apply fade-in for the main image after it becomes visible
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainImageView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void resetView() {
        mainImageView.setImage(null);
        nextImageView.setImage(null);
        mainImageView.setVisible(false);
        nextImageView.setVisible(false);
        gridPane.setVisible(true);
    }

    private void toggleSlideshow(Button slideshowButton) {
        if (slideshowRunning) {
            // Stop the slideshow
            slideshowTimeline.stop();
            slideshowButton.setText("Start Slideshow");
            showStopSlideshowPopUp();
        } else {
            // Start the slideshow
            slideshowTimeline = createSlideshowTimeline();
            slideshowTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
            slideshowTimeline.play();
            slideshowButton.setText("Stop Slideshow");
            showStartSlideshowPopUp();
        }
        slideshowRunning = !slideshowRunning;
    }

    private void showStartSlideshowPopUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Slideshow Started");
        alert.setHeaderText(null);
        alert.setContentText("Slideshow has started. Enjoy!");
        alert.showAndWait();
    }

    private void showStopSlideshowPopUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Slideshow Stopped");
        alert.setHeaderText(null);
        alert.setContentText("Slideshow has stopped.");
        alert.showAndWait();
    }

    private Timeline createSlideshowTimeline() {
        // Create a Timeline to change images every 2 seconds
        return new Timeline(
                new KeyFrame(Duration.seconds(2), e -> showNextImage())
        );
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-border-radius: 5px; -fx-padding: 10px; -fx-font-family: 'Algerian'; -fx-background-color: lightblue;");
        button.setOnMouseMoved(e -> button.setStyle("-fx-background-color: grey; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-border-radius: 5px; -fx-padding: 10px; -fx-font-family: 'Algerian'; -fx-background-color: lightblue;"));
        return button;
    }

    public static void main(String[] args) {
        launch();
    }
}
