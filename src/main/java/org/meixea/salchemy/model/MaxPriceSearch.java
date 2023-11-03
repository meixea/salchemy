package org.meixea.salchemy.model;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MaxPriceSearch {

    static public String getDefaultSearchName(int id){
        return "Поиск " + id;
    }
    static private ExecutorService calculationPool = Executors.newCachedThreadPool();

    private final int id;

    private SimpleStringProperty name;

    private SimpleObjectProperty<ReagentsBag> reagentsBag;
    private ObservableList<Potion> calculationResult;
    private MaxPriceCalculable nowCalculating = null;

    public MaxPriceSearch(){

        int maxId = Model.modelData.maxPriceSearches.stream()
                .mapToInt( search -> search.getId() )
                .max()
                .orElseThrow();
        this.id = maxId + 1;

        this.reagentsBag = new SimpleObjectProperty(new ReagentsBag());

        this.name = new SimpleStringProperty( getDefaultSearchName(id) );

        this.calculationResult = FXCollections.observableArrayList();

        setupListeners();
    }
    public MaxPriceSearch(int id, SimpleStringProperty name, SimpleObjectProperty<ReagentsBag> reagentsBag){

        this.id = id;
        this.name = name;
        this.reagentsBag = reagentsBag;
        this.calculationResult = FXCollections.observableArrayList();

        setupListeners();
    }

    public Integer getId() {
        return id;
    }

    public SimpleObjectProperty<ReagentsBag> bagProperty() {
        return reagentsBag;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
    private void setupListeners(){

        for(ReagentInBag item : reagentsBag.getValue().getReagents())
            item.quantityProperty().addListener( (i, oldValue, newValue) -> reCalculate() );

        reagentsBag.get().getReagents().addListener( (ListChangeListener<ReagentInBag>) changes -> {

            boolean needRecalculate = false;

            while(changes.next()){
                if(changes.wasAdded()){
                    needRecalculate = true;
                    for(ReagentInBag item : changes.getAddedSubList()){
                        item.quantityProperty().addListener( (i, oldValue, newValue) -> {
                            reCalculate();
                        } );
                    }
                }
                if(changes.wasRemoved())
                    needRecalculate = true;
            }

            if(needRecalculate)
                reCalculate();
        });
    }
    private void reCalculate(){

        if(nowCalculating != null)
            nowCalculating.cancel();

        nowCalculating = new MaxPriceCalculable(reagentsBag.getValue());

        nowCalculating.setOnSucceeded(event -> {

            setCalculationResults(nowCalculating.getValue(), nowCalculating.getSurplusBag());
            nowCalculating = null;

        });

        calculationPool.execute(nowCalculating);

    }
    private void setCalculationResults(List<Potion> results, ReagentsBag surplusBag){

        calculationResult.clear();
        calculationResult.addAll(results);
        reagentsBag.getValue().setSurplusValues(surplusBag);


    }
    static public void shutdown(){
        calculationPool.shutdownNow();
    }
    public void cancelCalculation(){
        if(nowCalculating != null) {
            nowCalculating.cancel();
            nowCalculating = null;
        }
    }

    public ObservableList<Potion> getCalculationResult() {
        return calculationResult;
    }
}
