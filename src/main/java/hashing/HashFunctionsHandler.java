package hashing;


import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HashFunctionsHandler {

    private final int numFunctions;

    public HashFunctionsHandler (int numFunctions) {
        this.numFunctions = numFunctions;
    }

    public List<Function<String, Integer>> getFunctions () {
        return getIndependentSeeds().stream().map(
                seed ->
                    (Function<String, Integer>) s -> MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), seed) >>> 1
        ).collect(Collectors.toList());
    }

    private List<Integer> getIndependentSeeds () {
        Random r = new Random();
        List<Integer> seeds = IntStream.range(1, numFunctions + 1).boxed().collect(Collectors.toList());
        return seeds;
    }
}
