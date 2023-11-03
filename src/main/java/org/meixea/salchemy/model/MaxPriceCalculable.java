package org.meixea.salchemy.model;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

public class MaxPriceCalculable extends Task<List<Potion>> {

    private final ReagentsBag bag;
    public MaxPriceCalculable(ReagentsBag bag){

        this.bag = (ReagentsBag) bag.clone();

    }
    @Override
    public List<Potion> call(){

        List<Potion> result = new ArrayList<>();

        while(true) {

            Potion potion2 = getMaxPricePotion(bag, 2);

            if (potion2.getPrice() == 0)
                break;

            Potion potion3 = getMaxPricePotion(bag, 3);

            Potion bestPotion = potion3.compareTo(potion2) > 0 ? potion3 : potion2;

            bestPotion.setQuantity(bag.getPotionsAvailable(bestPotion));

            result.add(bestPotion);

            bag.removePotionReagents(bestPotion);

        }

        return result;
    }
    private Potion getMaxPricePotion(ReagentsBag bag, int numberReagents){

        return new PotionCombinator(bag, numberReagents).stream()
                .max(Comparator.comparingInt(Potion::getPrice))
                .orElse(new Potion());

    }

    public ReagentsBag getSurplusBag() {
        return bag;
    }
}
