package hashing;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
     * @return - A list of hashing functions (MurmurHash3)
     */
    public List<Function<String, Integer>> getFunctions (int upperBound) {
        return getIndependentSeeds().stream().map(
                seed ->
                    (Function<String, Integer>) s -> (MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), seed) >>> 1) % upperBound
        ).collect(Collectors.toList());
    }

    /**
     * Creates a collection of different seeds for the hashing functions
     * @return - A list of seeds for the hashing functions
     */
    private List<Integer> getIndependentSeeds () {
        List<Integer> seeds = IntStream.range(1, numFunctions + 1).boxed().collect(Collectors.toList());
        return seeds;
    }
}
