package org.meixea.salchemy.model;

import javafx.beans.property.SimpleIntegerProperty;

public class ReagentInBag {

    private Reagent reagent;
    private SimpleIntegerProperty quantity;
    private SimpleIntegerProperty surplus;

    public ReagentInBag(Reagent reagent, int quantity, int surplus){

        this.reagent = reagent;
        this.quantity = new SimpleIntegerProperty(quantity);
        this.surplus = new SimpleIntegerProperty(surplus);

    }

    public Reagent getReagent() {
        return reagent;
    }
    @Override
    public Object clone(){
        return new ReagentInBag(reagent, quantity.getValue(), surplus.getValue());
    }

    public SimpleIntegerProperty quantityProperty(){
        return quantity;
    }
    public SimpleIntegerProperty surplusProperty(){
        return surplus;
    }

}
