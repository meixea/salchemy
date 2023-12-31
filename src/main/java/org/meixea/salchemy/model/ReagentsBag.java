package org.meixea.salchemy.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReagentsBag {
    private ObservableList<ReagentInBag> reagents;

    private final int bag_id;

    public ReagentsBag(){

        int maxBagId = Model.modelData.reagentBags.stream()
                .mapToInt( search -> search.getId() )
                .max()
                .orElseThrow();
        this.bag_id = maxBagId + 1;

        reagents = FXCollections.observableArrayList();

    }
    public ReagentsBag(int id, ObservableList<ReagentInBag> reagents){

        this.bag_id = id;
        this.reagents = reagents;

    }
    private ReagentsBag(ReagentsBag other){

        this.bag_id = other.bag_id;

        reagents = FXCollections.observableArrayList();
        for( ReagentInBag rib : other.reagents )
            reagents.add((ReagentInBag) rib.clone());

    }

    public void add(Reagent reagent, int amount){

        ReagentInBag rib = findReagent(reagent);
        if(rib == null)
            reagents.add(new ReagentInBag(reagent, amount, amount));
        else {
            rib.quantityProperty().add(amount);
            rib.surplusProperty().add(amount);
        }

    }
    public ReagentInBag findReagent(Reagent reagent){
        return reagents.stream()
                .filter( rib -> rib.getReagent() == reagent )
                .findAny()
                .orElse(null);
    }
    @Override
    public Object clone(){
        return new ReagentsBag(this);
    }
/*
    public int contains(Reagent reagent){

        ReagentInBag rib = findReagent(reagent);

        if

        Integer result = reagents.get(reagent);

        if(result == null)
            return 0;

        return result;
    }
*/
    public int getQuantity(Reagent reagent){

        ReagentInBag rib = findReagent(reagent);
        if(rib == null)
            return 0;

        return rib.quantityProperty().getValue();

    }
    public int getId(){
        return bag_id;
    }
    public int getPotionsAvailable(Potion potion){
        return potion.getFormula().stream()
                .mapToInt( reagent -> getQuantity(reagent) )
                .min()
                .orElse(0);
    }
    public void removeQuantity(Reagent reagent, int amount){

        ReagentInBag rib = findReagent(reagent);

        if(rib == null)
            return;

        int newAmount = rib.quantityProperty().getValue() - amount;

        if(newAmount == 0)
            reagents.remove(rib);
        else
            rib.quantityProperty().set(newAmount);

    }
    public void removeSurplus(Reagent reagent, int amount){

        ReagentInBag rib = findReagent(reagent);

        rib.surplusProperty().set(rib.surplusProperty().getValue() - amount);

    }
    public void removePotionReagents(Potion potion){
        potion.getFormula().stream()
                .forEach( reagent -> removeQuantity(reagent, potion.getQuantity()) );
    }
    public int wasteMaxReagents(Potion potion){

        int amount = getPotionsAvailable(potion);

        potion.getFormula().stream()
                .forEach( reagent -> removeSurplus(reagent, amount) );

        return amount;
    }
    public void setSurplusValues(ReagentsBag other){
        reagents.stream().forEach(rib -> {
            rib.surplusProperty().setValue(other.getQuantity(rib.getReagent()));
        });
    }

    public ObservableList<ReagentInBag> getReagents(){
        return reagents;
    }
}
