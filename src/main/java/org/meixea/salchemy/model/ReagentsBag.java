package org.meixea.salchemy.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReagentsBag {
    private ObservableMap<Reagent, Integer> reagents;

    public ReagentsBag(){
        reagents = FXCollections.observableHashMap();
    }
    private ReagentsBag(ReagentsBag other){

        reagents = FXCollections.observableHashMap();

        reagents.putAll(other.reagents);

    }

    public void add(Reagent reagent, int amount){
        if(amount > 0)
            reagents.put(reagent, contains(reagent) + amount);
    }
    @Override
    public Object clone(){
        return new ReagentsBag(this);
    }
    public int contains(Reagent reagent){

        Integer result = reagents.get(reagent);

        if(result == null)
            return 0;

        return result;
    }
    public int getPotionsAvailable(Potion potion){
        return potion.getFormula().stream()
                .mapToInt( i -> contains(i) )
                .min()
                .orElse(0);
    }
    public List<Reagent> getReagentsList(){
        return new ArrayList<>(reagents.keySet());
    }
    public void remove(Reagent reagent, int amount){

        int old = contains(reagent);

        if(old < amount)
            throw new NotEnoughAmountException();

        else if(old == amount)
            reagents.remove(reagent);

        else
            reagents.put(reagent, old - amount);
    }
    public int removeAllFor(Potion potion){

        int amount = getPotionsAvailable(potion);

        potion.getFormula().stream()
                .forEach( reagent -> remove(reagent, amount) );

        return amount;
    }

    public int size(){
        return reagents.size();
    }
    public void subtract(Reagent reagent, int amount){

        int old = contains(reagent);

        if(old < amount)
            throw new NotEnoughAmountException();

        reagents.put(reagent, old - amount);
    }
}
