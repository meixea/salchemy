package org.meixea.salchemy.model;

import java.util.*;

public class Potion implements Comparable<Potion> {
    private List<Reagent> formula = new ArrayList<>();
    private int price;
    private int quantity = 0;
    private int potion_id = -1;
    public Potion(){
        price = 0;
    }
    public Potion(Reagent r1, Reagent r2){
        Collections.addAll(formula, r1, r2);
        calculatePrice();
    }
    public Potion(Reagent r1, Reagent r2, Reagent r3){
        Collections.addAll(formula, r1, r2, r3);
        calculatePrice();
    }
    private void calculatePrice(){
        price = 0;
        for(AlchemyProperty prop : getCommonProperties())
            price += prop.getPrice();
    }

    public void addReagent(Reagent reagent){
        formula.add(reagent);
        calculatePrice();
    }
    @Override
    public int compareTo(Potion other){

        int prices = this.price - other.price;

        if(prices == 0)
            return other.size() - this.size();

        return prices;
    }
    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || this.getClass() != o.getClass())
            return false;

        Potion other = (Potion) o;

        return Objects.equals(new HashSet(formula), new HashSet(other.formula));
    }
    public List<Reagent> getFormula(){
        return formula;
    }
    public int getPrice(){
        return price;
    }
    public Set<AlchemyProperty> getCommonProperties(){

        Set<AlchemyProperty> result = new TreeSet<>();

        if( size() < 2 )
            return result;

        Set<AlchemyProperty> all = new HashSet<>(formula.get(0).getProperties());

        for( int i = 1; i < size(); i++ ){
            List<AlchemyProperty> props = formula.get(i).getProperties();
            for(AlchemyProperty prop : props)
                if(all.contains(prop))
                    result.add(prop);
                else
                    all.add(prop);
        }

        return result;
    }
    @Override
    public int hashCode(){
        return formula.hashCode();
    }
    public int size(){
        return formula.size();
    }

    public int getPotion_id() {
        return potion_id;
    }

    public void setPotion_id(int potion_id) {
        this.potion_id = potion_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(", ");
        formula.stream().forEach( reagent -> joiner.add(reagent.nameProperty().getValue()) );
        return String.format("(%s)[%d]x%d", joiner, price, quantity);
    }
}
