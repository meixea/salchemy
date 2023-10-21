package org.meixea.salchemy.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModelData {

    public ObservableList<MaxPriceSearch> maxPriceSearches = FXCollections.observableArrayList();
    public MaxPriceSearch createMaxPriceSearch(){

        int maxId = maxPriceSearches.stream()
                .mapToInt( search -> search.idProperty().getValue() )
                .max()
                .orElseThrow();

        MaxPriceSearch newSearch = new MaxPriceSearch(maxId + 1, null);

        maxPriceSearches.add(newSearch);

        return newSearch;
    }

    public MaxPriceSearch getMaxPriceSearch(int id){
        return maxPriceSearches.stream()
                .filter( search -> search.idProperty().getValue() == id )
                .findAny()
                .orElse(null);
    }

}
