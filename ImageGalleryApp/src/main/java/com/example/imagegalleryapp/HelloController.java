package com.example.imagegalleryapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label welcomeText;  // Label reference defined in FXML

    @FXML
    protected void onHelloButtonClick() {
        // Change the text of the Label when the button is clicked
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
