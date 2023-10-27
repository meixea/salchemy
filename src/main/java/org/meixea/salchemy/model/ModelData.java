package org.meixea.salchemy.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModelData {

    public List<AlchemyProperty> alchemyProperties;
    public ObservableList<Reagent> alchemyReagents;

    public ObservableList<MaxPriceSearch> maxPriceSearches;

    public ObservableList<ReagentsBag> reagentBags;

    public AlchemyProperty getProperty(String name){
        return alchemyProperties.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
    public AlchemyProperty getProperty(int id){

        if(id < 1 || id > alchemyProperties.size())
            throw new IndexOutOfBoundsException("No such ID for AlchemyProperty");

        return alchemyProperties.stream()
                .filter(i -> i.getId() == id)
                .findAny()
                .orElse(null);
    }
    public Reagent getReagent(String name){
        return alchemyReagents.stream()
                .filter( i -> i.nameProperty().getValue().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

    public ReagentsBag getReagentsBag(int id){

        return reagentBags.stream()
                .filter( bag -> bag.getId() == id )
                .findAny()
                .orElse(null);
    }
    public MaxPriceSearch getMaxPriceSearch(int id){

        return maxPriceSearches.stream()
                .filter( search -> search.getId() == id )
                .findAny()
                .orElse(null);
    }

}
