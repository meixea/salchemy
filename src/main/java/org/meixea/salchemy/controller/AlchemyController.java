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
import org.meixea.salchemy.model.MaxPriceSearch;
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

        appModesAccordion.expandedPaneProperty().addListener( (value, oldPane, newPane) -> {
            if(newPane != null) {
                AlchemyApplication.appProperties.put("mode", newPane.textProperty().getValue());
                AlchemyApplication.appProperties.save();
            }
        });

        restoreAppState();

    }
    private void restoreAppState() {

        if( AlchemyApplication.appProperties.restore() ){

            //  Restore current application mode: max price, imba or favorites
            String title = (String) AlchemyApplication.appProperties.get("mode");
            if(maxPriceTitledPane.textProperty().getValue().equals(title))
                appModesAccordion.setExpandedPane(maxPriceTitledPane);
            else if(imbaPotionTitledPane.textProperty().getValue().equals(title))
                appModesAccordion.setExpandedPane(imbaPotionTitledPane);
            else
                appModesAccordion.setExpandedPane(favoritesTitledPane);

            //  Restore currently selected search tab
            Tab selectedTab = maxPricePaneController.searchesTabPane.getTabs().stream()
                    .filter( tab -> ((MaxPriceSearch)tab.getProperties().get("search")).getId()
                            .toString()
                            .equals(AlchemyApplication.appProperties.get("max_search_selected")))
                    .findAny()
                    .orElseThrow();
            MaxPriceSearch selectedSearch = (MaxPriceSearch)selectedTab.getProperties().get("search");
            if(maxPricePaneController.searchesTabPane.getSelectionModel().getSelectedItem() != selectedTab)
                maxPricePaneController.searchesTabPane.getSelectionModel().select(selectedTab);
            else
                maxPricePaneController.selectSearch(selectedSearch);

        }
        else
            loadDefaultState();
    }
    private void loadDefaultState(){

        Tab selectedTab = maxPricePaneController.searchesTabPane.getSelectionModel().getSelectedItem();
        MaxPriceSearch selected = (MaxPriceSearch) selectedTab.getProperties().get("search");
        maxPricePaneController.selectSearch(selected);
        AlchemyApplication.appProperties.put("max_search_selected",
                selected.getId().toString());

        appModesAccordion.setExpandedPane(maxPriceTitledPane);

    }
    @FXML
    private void addNewSearch(){

        Model.createMaxPriceSearch();

    }
}