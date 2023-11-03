package org.meixea.salchemy.model.combinators;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Combinator implements Iterator<int[]> {
    private int size;
    private int k;
    private int[] nextIndexes;

    public Combinator(int size, int k){

        this.size = size;
        this.k = k;

        nextIndexes = null;
    }

    @Override
    public boolean hasNext(){

        return (k <= size) && (nextIndexes == null || nextIndexes[0] < (size - k));

    }

    @Override
    public int[] next(){

        if(nextIndexes == null) {
            nextIndexes = new int[k];
            for(int i = 0; i < k; i++)
                nextIndexes[i] = i;
        }
        else{
            int j = k - 1;
            while( true ) {
                int maxJ = size - (k - j);
                if (nextIndexes[j] < maxJ)
                    break;
                j--;
            }
            nextIndexes[j]++;
            for(int i = j + 1; i < k; i++)
                nextIndexes[i] = nextIndexes[i - 1] + 1;
        }

        return nextIndexes.clone();
    }
    public Stream<int[]> stream(){

        Iterable<int[]> it = () -> this;
        return StreamSupport.stream(it.spliterator(), false);
    }
}
