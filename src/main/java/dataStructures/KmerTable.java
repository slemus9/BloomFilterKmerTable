package dataStructures;

import io.reactivex.rxjava3.core.Observable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KmerTable {

    private int k;
    private BloomFilter<String> kmerSet;
    private int[] kmerMap;

    public KmerTable (int k, int expectedNumKmers, double expectedError){
        this.k = k;
        this.kmerSet = new BloomFilter<>(expectedNumKmers, expectedError);
        this.kmerMap = new int[expectedNumKmers];
    }

    public KmerTable (int k, BloomFilter<String> kmerSet, Observable<String> kmers) {
        this.k = k;
        this.kmerSet = kmerSet;
        this.kmerMap = new int[kmerSet.getSize()];
    }

    public void add (String kmer) {
        kmerMap[getPosition(kmer)] ++;
    }

    public int getSize () {
        return kmerMap.length;
    }

    private int getPosition (String key) {
        return key.hashCode() & (getSize() - 1);
    }

    private int get (String key) {
        return kmerMap[getPosition(key)];
    }

    public void fillMapFromSource (Observable<String> kmers) {
        
    }

    public void store (String outputFilePath, Observable<String> keys) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true));
            bw.write("kmer,frequency\n");
            keys.subscribe(
                    key -> bw.write(String.format("%s,%d\n", key, get(key)))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
