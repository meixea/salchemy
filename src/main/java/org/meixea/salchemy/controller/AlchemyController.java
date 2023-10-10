package org.meixea.salchemy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlchemyController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}