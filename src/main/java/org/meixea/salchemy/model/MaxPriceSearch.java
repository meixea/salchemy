package org.meixea.salchemy.model;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MaxPriceSearch {

    private final SimpleIntegerProperty id;

    private SimpleStringProperty name;

    public MaxPriceSearch(int id, String name){

        this.id = new SimpleIntegerProperty(id);

        this.name = new SimpleStringProperty( name == null ?
                getDefaultSearchName(id) :
                name
        );
    }
    private String getDefaultSearchName(int id){
        return "Поиск " + id;
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
}
