package hashing;

import java.util.List;
import java.util.Random;
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
        return getIndependentSeeds().map(
                seed ->
                    (Function<String, Integer>) s -> MurmurHash3.murmurhash3_x86_32(s, 0, s.length(), seed)
        ).collect(Collectors.toList());
    }

    private Stream<Integer> getIndependentSeeds () {
        Random r = new Random();
        return IntStream.range(1, numFunctions).map(x -> r.nextInt(Short.MAX_VALUE) + x).boxed();
    }
}
