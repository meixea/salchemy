package org.meixea.salchemy.model;

import java.sql.SQLException;
import java.util.*;

public class AlchemyProperty implements Comparable<AlchemyProperty> {

    static List<AlchemyProperty> allProperties;
    static public List<AlchemyProperty> getAllProperties(){
        return allProperties;
    }
    static public AlchemyProperty getProperty(String name){
        return allProperties.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
    static public AlchemyProperty getProperty(int id){

        if(id < 1 || id > allProperties.size())
            throw new IndexOutOfBoundsException("No such ID for AlchemyProperty");

        return allProperties.stream()
                .filter(i -> i.getId() == id)
                .findAny()
                .orElse(null);
    }
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
}
