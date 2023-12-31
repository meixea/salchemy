package org.meixea.salchemy.model;

import java.sql.SQLException;
import java.util.*;

public class AlchemyProperty implements Comparable<AlchemyProperty> {
    private final int id;
    private String name;
    private AlchemyType type;
    private int price;
    private AlchemyPropertyCategory category;
    public AlchemyProperty(
            int id,
            String name,
            AlchemyType type,
            int price,
            AlchemyPropertyCategory category
    ){

        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.category = category;

    }
    @Override
    public int compareTo(AlchemyProperty other){

        int priceDifference = this.getPrice() - other.getPrice();
        if( priceDifference != 0 )
            return price;

        return this.getName().compareTo(other.getName());
    }
    public int getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public AlchemyType getType() {
        return type;
    }
    public int getPrice() {
        return price;
    }
    public AlchemyPropertyCategory getCategory(){
        return category;
    }
    @Override
    public int hashCode(){
        return getId();
    }
    @Override
    public boolean equals(Object o){

        if( this == o )
            return true;
        if( o == null || this.getClass() != o.getClass() )
            return false;

        AlchemyProperty other = (AlchemyProperty) o;

        return this.getId() == other.getId();

    }
    @Override
    public String toString(){
        return String.format("%s(%d)", getName(), getPrice());
    }
}
