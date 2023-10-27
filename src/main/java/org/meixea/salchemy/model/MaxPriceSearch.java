package org.meixea.salchemy.model;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MaxPriceSearch {

    static public String getDefaultSearchName(int id){
        return "Поиск " + id;
    }

    private final int id;

    private SimpleStringProperty name;

    private SimpleObjectProperty<ReagentsBag> reagentsBag;

    public MaxPriceSearch(){

        int maxId = Model.modelData.maxPriceSearches.stream()
                .mapToInt( search -> search.getId() )
                .max()
                .orElseThrow();
        this.id = maxId + 1;

        this.reagentsBag = new SimpleObjectProperty(new ReagentsBag());

        this.name = new SimpleStringProperty( getDefaultSearchName(id) );
    }
    public MaxPriceSearch(int id, SimpleStringProperty name, SimpleObjectProperty<ReagentsBag> reagentsBag){

        this.id = id;
        this.name = name;
        this.reagentsBag = reagentsBag;

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
}
