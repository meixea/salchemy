package org.meixea.salchemy.model;

import org.meixea.salchemy.model.combinators.Combinator;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PotionCombinator implements Iterator<Potion> {
    private List<ReagentInBag> listReagents;
    private int numberReagents;
    private Combinator combinator;
    public PotionCombinator(ReagentsBag bag, int numberReagents){

        combinator = new Combinator(bag.getReagents().size(), numberReagents);

        this.listReagents = bag.getReagents();
        this.numberReagents = numberReagents;
    }
    @Override
    public boolean hasNext(){
        return combinator.hasNext();
    }
    @Override
    public Potion next(){

        int[] index = combinator.next();

        Potion potion = new Potion();

        for(int i = 0; i < numberReagents; i++)
            potion.addReagent(listReagents.get(index[i]).getReagent());

        return potion;
    }
    public Stream<Potion> stream(){

        Iterable<Potion> it = () -> this;
        return StreamSupport.stream(it.spliterator(), false);
    }
}
