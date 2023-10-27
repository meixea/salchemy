package org.meixea.salchemy.model;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class Reagent {
    private final SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleObjectProperty<AlchemyReagentCategory> category;
    private final ObservableList<AlchemyProperty> properties;

    public Reagent(int id, String name, AlchemyReagentCategory category, List<AlchemyProperty> properties){
        this.id = new ReadOnlyIntegerWrapper(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleObjectProperty<>(category);
        this.properties = FXCollections.observableList(properties);
    }
    @Override
    public int hashCode(){
        if( nameProperty() == null )
            return 0;

        return nameProperty().getValue().hashCode();
    }
    @Override
    public boolean equals(Object o){

        if( this == o )
            return true;

        if( o == null )
            return false;

        if(o instanceof String) {
            String otherName;
            otherName = (String) o;
            return otherName.equalsIgnoreCase(this.nameProperty().getValue());
        }

        else if(o instanceof Reagent) {
            Reagent otherReagent = (Reagent) o;
            return this.idProperty().getValue() == otherReagent.idProperty().getValue();
        }

        return false;

    }
    public void addProperty(AlchemyProperty newProperty){
        properties.add(newProperty);
    }
    public boolean hasProperty(AlchemyProperty property){
        return properties.contains(property);
    }
    public SimpleIntegerProperty idProperty(){
        return id;
    }
    public SimpleStringProperty nameProperty(){
        return name;
    }
    public List<AlchemyProperty> getProperties(){
        return properties;
    }
    public SimpleObjectProperty<AlchemyReagentCategory> categoryProperty(){
        return category;
    }
}
