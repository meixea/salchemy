package org.meixea.salchemy.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;

public class AlchemyQueue extends LinkedHashMap<Potion, Integer> {

    private ReagentsBag unused = null;
    public int getPrice(){
        return entrySet().stream()
                .mapToInt( entry -> entry.getKey().getPrice() * entry.getValue() )
                .sum();
    }

    public ReagentsBag getUnused() {
        return unused;
    }

    public void setUnused(ReagentsBag unused){
        this.unused = unused;
    }
}
