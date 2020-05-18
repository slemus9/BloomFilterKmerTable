package hashing;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HashFunctionsHandler {

    private final int numFunctions;

    public HashFunctionsHandler (int numFunctions) {
        this.numFunctions = numFunctions;
    }

    public List<Function<String, Integer>> getFunctions () {
        return getIndependentSeeds().stream().map(
                seed ->
                    (Function<String, Integer>) s -> MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), seed)
        ).collect(Collectors.toList());
    }

    private List<Integer> getIndependentSeeds () {
        Random r = new Random();
        List<Integer> seeds = IntStream.range(0, numFunctions).map(x -> r.nextInt(Short.MAX_VALUE)).boxed().collect(Collectors.toList());
        while (new HashSet(seeds).size() != numFunctions)
            getIndependentSeeds();
        return seeds;
    }
}
