package dataStructures;

import java.util.HashMap;

public class KmerTable {

    private int k;
    private BloomFilter<String> kmerSet;
    private HashMap<String, Integer> kmerMap;

    public KmerTable (int k, int expectedNumKmers){
        this.k = k;
        this.kmerSet = new BloomFilter<>(4*expectedNumKmers, expectedNumKmers);
        this.kmerMap = new HashMap<>();
    }

    public void add (String kmer) {
        if (kmerSet.contains(kmer)) {
            kmerMap.compute(kmer, (k, v) -> v == null ? 1 : v + 1);
        } else {
            kmerSet.add(kmer);
            kmerMap.put(kmer, 1);
        }
    }

    public void cleanSingletonKmers () {
        kmerMap.forEach((k, v) -> {
            if (v == 1) kmerMap.remove(k);
        });
    }
}
