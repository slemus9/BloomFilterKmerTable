package dataStructures;

import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;

public class KmerTable {

    private int k;
    private BloomFilter<String> kmerSet;
    private HashMap<String, Integer> kmerMap;

    public KmerTable (int k){
        this.k = k;
    }

}
