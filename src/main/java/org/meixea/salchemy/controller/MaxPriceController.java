package org.meixea.salchemy.controller;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.meixea.salchemy.model.*;
import org.meixea.salchemy.view.*;

import java.util.ArrayList;
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

    @FXML
    TableView<ReagentInBag> bagTable;
    @FXML
    TableColumn<ReagentInBag, AlchemyReagentCategory> bagCategoryColumn;
    @FXML
    TableColumn<ReagentInBag, String> bagNameColumn;
    @FXML
    TableColumn<ReagentInBag, SimpleIntegerProperty> bagQuantityColumn;
    @FXML
    TableColumn<ReagentInBag, Number> bagSurplusColumn;

    public void initialize(){

        initReagentsTable();

//      Initial creating tab panels
        initializeSearchesTab();

        initBagTable();

        if( AlchemyApplication.appProperties.restore() ){
            loadState();
        }
        else
            loadDefaultState();

    }
    private void initializeSearchesTab(){

        List<MaxPriceSearch> searches = Model.getModelData().maxPriceSearches;

//      Handle adding new searches
        Model.getModelData().maxPriceSearches.addListener((ListChangeListener<MaxPriceSearch>) change -> {
            while(change.next()) {
                if(change.wasPermutated()){}
                else if(change.wasUpdated()){}
                else if(change.wasRemoved()){}
                else if(change.wasAdded()) {
                    for(MaxPriceSearch addedSearch : change.getAddedSubList()){
                        Tab newTab = createNewSearchTab(addedSearch);
                        searchesTabPane.selectionModelProperty().getValue().select(newTab);
                    }
                }
            }
        });

//      Handle making tabs closeable only if their more than 1
        searchesTabPane.getTabs().addListener((ListChangeListener<Tab>) change -> updateTabCloseButtonState());

//      Create tabs
        for(MaxPriceSearch search : searches)
            createNewSearchTab(search);

//      Handle selecting search tab
        searchesTabPane.selectionModelProperty().getValue().selectedItemProperty().addListener(
                (value, oldValue, newValue) -> {

                    MaxPriceSearch selected = (MaxPriceSearch) newValue.getProperties().get("search");

                    selectSearch( selected );

                    AlchemyApplication.appProperties.put("max_search_selected",
                            selected.getId().toString());
                    AlchemyApplication.appProperties.save();

                });

    }
    private void initReagentsTable(){

        regsTableCategoryColumn.setEditable(false);
        regsTableCategoryColumn.setCellValueFactory( features ->
                features.getValue().categoryProperty()
        );
        regsTableCategoryColumn.setCellFactory( column -> new ReagentCategoryCell(null) );

        regsTableNameColumn.setEditable(false);
        regsTableNameColumn.setCellValueFactory( features ->
                features.getValue().nameProperty()
        );
        regsTableNameColumn.prefWidthProperty().bind(
                regsTable.widthProperty()
                        .subtract(regsTableCategoryColumn.widthProperty()
                                .add(15))
        );

//      Handle selecting reagents in base table - add to reagents bag
        regsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            addReagentToBag(newValue);
        });

    }
    private void initBagTable(){

        SelectionHandler<ReagentInBag> onClickHandler = rib -> deleteReagentFromBag(rib);

        bagNameColumn.prefWidthProperty().bind(
                bagTable.widthProperty()
                        .subtract(bagCategoryColumn.widthProperty()
                                .add(bagQuantityColumn.widthProperty())
                                .add(bagSurplusColumn.widthProperty()))
        );

        bagCategoryColumn.setCellValueFactory( features ->
                features.getValue().getReagent().categoryProperty()
        );
        bagCategoryColumn.setCellFactory( column -> new ReagentCategoryCell(onClickHandler) );

        bagNameColumn.setCellValueFactory( features ->
                features.getValue().getReagent().nameProperty()
        );
        bagNameColumn.setCellFactory( column_value -> new TextCell(Pos.CENTER_LEFT, onClickHandler));

        bagQuantityColumn.setCellValueFactory( features ->
                new SimpleObjectProperty(features.getValue().quantityProperty())
        );
        bagQuantityColumn.setCellFactory( column -> new ReagentQuantityCell() );

        bagSurplusColumn.setCellValueFactory( features ->
                features.getValue().surplusProperty()
        );
        bagSurplusColumn.setCellFactory( column_value -> new TextCell(Pos.CENTER, onClickHandler));

    }
    private Tab createNewSearchTab(final MaxPriceSearch search){

        Tab tab = new Tab();
        tab.getProperties().put("search", search);

        tab.textProperty().bind(search.nameProperty());

//      Handle closing search tab
        tab.setOnClosed( event -> {
            Model.deleteMaxPriceSearch(search);
        });

        AnchorPane anchorPane = new AnchorPane();
        TableView resultTable = new MaxPriceResultTableView(search);
        anchorPane.setTopAnchor(resultTable, 0.0);
        anchorPane.setLeftAnchor(resultTable, 0.0);
        anchorPane.setRightAnchor(resultTable, 0.0);
        anchorPane.setBottomAnchor(resultTable, 0.0);
        anchorPane.getChildren().add(resultTable);

        tab.setContent(anchorPane);

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
    void selectSearch(MaxPriceSearch search){

        List<TableColumn<ReagentInBag, ?>> bagSortList = new ArrayList<>(bagTable.getSortOrder());
        bagTable.setItems(search.bagProperty().getValue().getReagents());
        if(bagSortList.size() == 0)
            bagTable.getSortOrder().add(bagNameColumn);
        else
            bagTable.getSortOrder().addAll(bagSortList);

        ObservableList<Reagent> regItems = FXCollections.observableArrayList(Model.modelData.alchemyReagents);
        for( ReagentInBag rib : bagTable.getItems() )
            regItems.remove(rib.getReagent());

        List<TableColumn<Reagent, ?>> regsSortList = new ArrayList<>(regsTable.getSortOrder());
        regsTable.setItems(regItems);
        if(regsSortList.size() == 0)
            regsTable.getSortOrder().add(regsTableNameColumn);
        else
            regsTable.getSortOrder().addAll(regsSortList);

    }
    private void updateTabCloseButtonState(){

        List<Tab> tabs = searchesTabPane.getTabs();

        tabs.get(0).setClosable(tabs.size() > 1);
    }
    private void addReagentToBag(Reagent reagent){

        if( reagent == null )
            return;

        Platform.runLater( () -> {
            regsTable.getSelectionModel().clearSelection();
            regsTable.getItems().remove(reagent);
            bagTable.getItems().add(new ReagentInBag(reagent, 10, 10));
        } );

    }
    private void deleteReagentFromBag(ReagentInBag rib){

        bagTable.getItems().remove(rib);

        regsTable.getItems().add(rib.getReagent());

        regsTable.sort();

    }
}