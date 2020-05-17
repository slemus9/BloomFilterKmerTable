package dataStructures;

import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;

public class KmerTable {

    private Integer k;
    private BloomFilter<String> kmerSet;
    private HashMap<String, Integer> kmerMap;

    public KmerTable (Integer k){
        this.k = k;
    }

}
