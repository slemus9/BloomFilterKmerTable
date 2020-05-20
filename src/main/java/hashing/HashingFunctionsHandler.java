package hashing;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HashingFunctionsHandler {

    /**
     * Number of functions to be created.
     * the functions must be of type: String -> Int
     */
    private final int numFunctions;

    /**
     * Constructor of the class
     * @param numFunctions - Number of functions to be created
     */
    public HashingFunctionsHandler(int numFunctions) {
        this.numFunctions = numFunctions;
    }

    /**
     * Creates a collection of hashing functions given the value of numFunctions
     * @param upperBound - Limit number that the hashing functions can output
     * @return - A Stream of hashing functions (MurmurHash3)
     */
    public Stream<Function<String, Integer>> getFunctions (int upperBound) {
        return getIndependentSeeds().map(
                seed -> s -> {
                    int mmh3 = MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), seed);
                    return ( mmh3 >>> 1) % upperBound;
                }
        );
    }

    /**
     * Creates a collection of different seeds for the hashing functions
     * @return - A list of seeds for the hashing functions
     */
    private Stream<Integer> getIndependentSeeds () {
        return IntStream.range(1, numFunctions + 1).boxed();
    }
}
