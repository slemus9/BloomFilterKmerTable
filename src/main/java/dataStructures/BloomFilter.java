package dataStructures;

import hashing.HashingFunctionsHandler;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BloomFilter <A> {

    /**
     * A list of hash functions: h_i : A -> Int.
     * Each hash maps to a position in the BitArray
     */
    private final List<Function<A, Integer>> hashFunctions;

    /**
     * The array used to indicate if an element belongs to the set of elements.
     * An element x belongs to the BloomFilter if bitArray[h_i(x)] = 1 for all
     * i in {1 .. |hashFunctions|}
     */
    private final BitArray bitArray;

    /**
     * Number of elements in the BloomFilter
     */
    private int size = 0;

    /**
     * Bloom filter constructor given the expected number of elements to be added and
     * the expected probability of false positives
     * @param expectedNumEntries - Expected number of elements to be added
     * @param expectedError - Expected probability of false positives
     */
    public BloomFilter (int expectedNumEntries, double expectedError) {
        int bitArraySize = getBitArraySize(expectedNumEntries, expectedError);
        this.bitArray = new BitArray(bitArraySize);
        int numFunctions = getNumberOfFunctions(bitArraySize, expectedNumEntries);
        this.hashFunctions = generateFunctions(numFunctions);
    }

    /**
     * Calculates the bit array that minimizes the false positive probability to the expectedError value,
     * given the expected number of elements to be added
     * @param expectedNumEntries - Expected number of elements to be added
     * @param expectedError - Expected probability of false positives
     * @return the size of the needed bit array
     */
    private int getBitArraySize (int expectedNumEntries, double expectedError) {
        double logErr = Math.log(expectedError);
        double log2 = Math.log(2);
        double size = - (expectedNumEntries*logErr)/(Math.pow(log2, 2));
        return (int) size;
    }

    /**
     * Creates a list a list of hashing functions
     * @param numFunctions - Number of functions to be created
     * @return - A list of hash functions
     */
    private List<Function<A, Integer>> generateFunctions (int numFunctions) {
        HashingFunctionsHandler functionsHandler = new HashingFunctionsHandler(numFunctions);
        return functionsHandler
                .getFunctions(bitArray.getSize())
                .map(h -> (Function<A, Integer>) a -> h.apply(a.toString()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the number of needed hashing functions in accordance to the expected
     * number of elements to be added to the set and the size of the bit array
     * @param size - Size of the bit array
     * @param expectedNumEntries - Expected number of elements to be added
     * @return the number of functions to be created
     */
    private int getNumberOfFunctions (int size, int expectedNumEntries){
        return (int) Math.round(((size + 0.0)/expectedNumEntries)*Math.log(2));
    }

    /**
     * Applies and returns the indices that correspond to the given element
     * in the bloom filter (the application of the hashing functions to the
     * element)
     * @param a - Element to verify
     * @return a collection of the corresponding elements
     */
    private Stream<Integer> getIndices (A a) {
        return hashFunctions.stream().map(h -> h.apply(a));
    }

    /**
     * Adds an element to the bloom filter. Sets all the bits to 1 in the bit array,
     * that correspond to the indices of the element mapped by the hashing functions
     * @param a - Element to be added
     */
    public void add (A a) {
        getIndices(a).forEach(idx -> bitArray.set(idx, true));
        size ++;
    }

    /**
     * Checks if an element (with an error rate of the given expectedError value) belongs to
     * the bloom filter
     * @param a - Element to verify
     * @return true if the element belongs to the set, false otherwise
     */
    public boolean contains (A a) {
        return getIndices(a).allMatch(bitArray::get);
    }

    /**
     *
     * @return number of elements in the bloom filter
     */
    public int getSize () {
        return size;
    }

    public static void main(String[] args) {
        BloomFilter<String> bloomFilter = new BloomFilter<>(10, 0.01);
        bloomFilter.add("Sebastian");
        bloomFilter.add("DNA sequence");
        bloomFilter.add("Lemus");
        System.out.println(bloomFilter.contains("Sebastian"));
        System.out.println(bloomFilter.contains("RNA sequence"));
        System.out.println(bloomFilter.contains("Lemus"));
        System.out.println(bloomFilter.contains("lemus"));
    }
}
