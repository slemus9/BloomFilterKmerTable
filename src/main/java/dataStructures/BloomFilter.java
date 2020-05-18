package dataStructures;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BloomFilter <A> {

    private List<Function<A, Integer>> hashFunctions;
    private BitArray bitArray;
    private int size;

    public BloomFilter (int size) {
        this.size = size;
        this.bitArray = new BitArray(size);
    }

    private Stream<Integer> getIndices (A a) {
        return hashFunctions.stream().map(h -> h.apply(a));
    }

    public void add (A a) {
        getIndices(a).forEach(idx -> bitArray.set(idx, true));
    }

    public boolean contains (A a) {
        return getIndices(a).allMatch(bitArray::get);
    }

    public int getSize () {
        return size;
    }
}
