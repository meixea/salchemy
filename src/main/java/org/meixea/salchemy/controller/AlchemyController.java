package org.meixea.salchemy.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.meixea.salchemy.model.Model;
import org.meixea.salchemy.model.Reagent;
import org.meixea.salchemy.view.AlchemyApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class AlchemyController {
    @FXML
    private ToolBar toolBar;
    @FXML
    private VBox toolBarVBox;
    @FXML
    private VBox mainPane;
    @FXML
    private Button newSearch;
    @FXML
    private Accordion appModesAccordion;
    @FXML
    private TitledPane maxPriceTitledPane;
    @FXML
    private TitledPane imbaPotionTitledPane;
    @FXML
    private TitledPane favoritesTitledPane;
    @FXML
    private Pane maxPricePane;
    @FXML
    private Pane imbaPotionPane;
    @FXML
    private MaxPriceController maxPricePaneController;
    public AlchemyController(){

    }
    public void initialize(){

        restoreAppState();

        appModesAccordion.expandedPaneProperty().addListener( (value, oldPane, newPane) -> {
            if(newPane != null) {
                AlchemyApplication.appProperties.put("mode", newPane.textProperty().getValue());
                saveState();
            }
        });

    }
    private void restoreAppState() {

        if( AlchemyApplication.appProperties.restore() ){

            String title = (String) AlchemyApplication.appProperties.get("mode");
            if(maxPriceTitledPane.textProperty().getValue().equals(title))
                appModesAccordion.setExpandedPane(maxPriceTitledPane);
            else if(imbaPotionTitledPane.textProperty().getValue().equals(title))
                appModesAccordion.setExpandedPane(imbaPotionTitledPane);
            else
                appModesAccordion.setExpandedPane(favoritesTitledPane);

        }
        else
            loadDefaultState();
    }
    private void loadDefaultState(){

        appModesAccordion.setExpandedPane(maxPriceTitledPane);

    }
    @FXML
    private void addNewSearch(){

        Model.createMaxPriceSearch();

    }
    private void saveState(){

        AlchemyApplication.appProperties.save();

    }
}