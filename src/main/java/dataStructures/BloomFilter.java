package dataStructures;

import hashing.HashFunctionsHandler;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BloomFilter <A> {

    private final List<Function<A, Integer>> hashFunctions;
    private final BitArray bitArray;
    private final int size;

    public BloomFilter (int size, int expectedNumEntries) {
        this.size = size;
        this.bitArray = new BitArray(size);
        this.hashFunctions = generateFunctions(expectedNumEntries);
    }

    public BloomFilter (int size, List<Function<A, Integer>> hashFunctions) {
        this.size = size;
        this.bitArray = new BitArray(size);
        this.hashFunctions = hashFunctions;
    }

    private List<Function<A, Integer>> generateFunctions (int expectedNumEntries) {
        int numFunctions = getNumberOfFunctions(expectedNumEntries);
        HashFunctionsHandler functionsHandler = new HashFunctionsHandler(numFunctions);
        return functionsHandler
                .getFunctions().stream()
                .map(h -> (Function<A, Integer>) a -> h.apply(a.toString()))
                .collect(Collectors.toList());
    }

    private int getNumberOfFunctions (int expectedNumEntries){
        return (int) (size/expectedNumEntries*Math.log(2));
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
