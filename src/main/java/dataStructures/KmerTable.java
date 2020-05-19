package dataStructures;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KmerTable {

//    private int k;
    private BloomFilter<String> kmerSet;
    private Map<CharSequence, Short> kmerMap;

//    public KmerTable (int k, int expectedNumKmers, double expectedError){
//        this.k = k;
//        this.kmerSet = new BloomFilter<>(expectedNumKmers, expectedError);
//        this.kmerMap = new int[expectedNumKmers];
//    }

    public KmerTable (int expectedNumEntries, double expectedError) {
//        this.k = k;
//        this.kmerSet = kmerSet;
        this.kmerSet = new BloomFilter<>(expectedNumEntries, expectedError);
        this.kmerMap = new HashMap<>();
    }

    public void add (String kmer) {
        if (kmerSet.contains(kmer)){
            kmerMap.compute(kmer, (k, v) -> v == null ? 2 : (short)(v + 1));
        } else {
            kmerSet.add(kmer);
        }
    }

    public short get (String kmer) {
        return kmerMap.getOrDefault(kmer, (short) 1);
    }

    public int getSize () {
        return kmerMap.size();
    }


    public void store (String outputFilePath) {
        try {
            File outputFile = new File(outputFilePath);
            if (outputFile.exists()){
                outputFile.delete();
            }

            System.out.println("Writing into file: " + outputFilePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true));
            bw.write("kmer,frequency\n");

            kmerMap.forEach((key, v) -> {
                System.out.println("Storing kmer: " + key);
                try {
                    bw.write(String.format("%s,%d\n", key, v));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
