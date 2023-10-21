package org.meixea.salchemy.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.meixea.salchemy.model.AlchemyReagentCategory;
import org.meixea.salchemy.model.MaxPriceSearch;
import org.meixea.salchemy.model.Model;
import org.meixea.salchemy.model.Reagent;
import org.meixea.salchemy.view.AlchemyApplication;
import org.meixea.salchemy.view.ReagentCategoryCell;

import java.util.List;

public class MaxPriceController {
    @FXML
    private Pane mainPain;
    @FXML
    TabPane searchesTabPane;

    @FXML
    TableView<Reagent> regsTable;
    @FXML
    TableColumn<Reagent, AlchemyReagentCategory> regsTableCategoryColumn;
    @FXML
    TableColumn<Reagent, String> regsTableNameColumn;

    public void initialize(){

        regsTable.setItems(FXCollections.observableList(Reagent.getAllReagents()));
        regsTable.setFocusTraversable(false);

        regsTableCategoryColumn.setEditable(false);
        regsTableCategoryColumn.setCellValueFactory( features ->
            features.getValue().categoryProperty()
        );
        regsTableCategoryColumn.setCellFactory( column -> new ReagentCategoryCell() );

        regsTableNameColumn.setEditable(false);
        regsTableNameColumn.setCellValueFactory( features ->
                features.getValue().nameProperty()
        );
        regsTableNameColumn.prefWidthProperty().bind(
                regsTable.widthProperty()
                        .subtract(regsTableCategoryColumn.widthProperty()
                                .add(15))
        );

//      Handle making tabs closeable only if their more than 1
        searchesTabPane.getTabs().addListener((ListChangeListener<Tab>) change -> updateTabCloseButtonState());

//      Handle adding or removing new searches
        Model.getModelData().maxPriceSearches.addListener((ListChangeListener<MaxPriceSearch>) change -> {
            while(change.next()) {
                if(change.wasPermutated()){}
                else if(change.wasUpdated()){}
                else if(change.wasRemoved()){
                    System.out.println("Removed");
                }
                else if(change.wasAdded()) {
                    for(MaxPriceSearch addedSearch : change.getAddedSubList()){
                        Tab newTab = createNewSearchTab(addedSearch);
                        searchesTabPane.selectionModelProperty().getValue().select(newTab);
                    }
                }
            }
        });

//      Handle selecting search tab
        searchesTabPane.selectionModelProperty().getValue().selectedItemProperty().addListener(
                (value, oldValue, newValue) -> {
                    selectSearch(Model.getModelData().getMaxPriceSearch(
                            (Integer) newValue.getProperties().get("searchId")));
                });

//      Initial creating tab panels
        initializeSearchesTab(Model.getModelData().maxPriceSearches);

        if( AlchemyApplication.appProperties.restore() ){
            loadState();
        }
        else
            loadDefaultState();

//      Handle selecting reagents in base table - add to reagents bag
        regsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            addReagentToBag(newValue);
        });

    }
    private void initializeSearchesTab(List<MaxPriceSearch> searches){
        for(MaxPriceSearch search : searches){

            createNewSearchTab(search);

        }
    }
    private Tab createNewSearchTab(final MaxPriceSearch search){

        Tab tab = new Tab();
        tab.getProperties().put("searchId", search.idProperty().getValue());

        tab.textProperty().bind(search.nameProperty());
        tab.setOnClosed( event -> {
            Model.deleteMaxPriceSearch(search.idProperty().getValue());
        });
        tab.setContent(new AnchorPane());

        searchesTabPane.getTabs().add(tab);

        return tab;
    }
    private void loadState(){

    }
    private void loadDefaultState(){

    }
    public Pane getMainPane(){
        return mainPain;
    }
    private void selectSearch(MaxPriceSearch search){
        System.out.println(search.nameProperty().getValue() + ": " + search.idProperty().getValue());
    }
    private void updateTabCloseButtonState(){

        List<Tab> tabs = searchesTabPane.getTabs();

        tabs.get(0).setClosable(tabs.size() > 1);
    }
    private void addReagentToBag(Reagent reagent){

    }
}