package com.lpineda.dsketch;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;
import org.apache.commons.math3.primes.Primes;

public class Sketch {
    private int w, d, p, max_bucket_length;
    private Map<Integer, Integer> hash_functions;
    private List<Bucket> matrix;
    public int max_key;

    public Sketch(Integer d_, Integer w_, Integer p_) {
        w = w_;
        d = d_;
        p = p_;
        max_bucket_length = 8;
        assert (p >= w);
        assert (Primes.isPrime(p_));
        max_key = Integer.MAX_VALUE / p;
        buildHashFunctions();

        matrix = new ArrayList<>();
        for (Integer i = 0; i< w*d; i++) {
            matrix.add(new Bucket(max_bucket_length));
        }
    }

    private void buildHashFunctions() {
        hash_functions = new LinkedHashMap<>();
        while (hash_functions.size() < d) {
            hash_functions.put(ThreadLocalRandom.current().nextInt(0, (p - 2)) + 1,
                    ThreadLocalRandom.current().nextInt(0, (p - 1)));
        }
        assert (hash_functions.size() == d);
    }

    private int index(int i, int j) {
        return (i*w) + j;

    }

    private Integer hashElement(Map.Entry<Integer, Integer> hash_function, int element_) {
        int a = hash_function.getKey();
        int b = hash_function.getValue();
        int hashed_element = ((a * element_ + b) % p) % w;
        return (hashed_element);
    }

    public void addElement(Integer element_) {
        assert (element_ < max_key);
        Integer row = 0;
        for (Map.Entry<Integer, Integer> hash_function: hash_functions.entrySet()) {
            Integer col = hashElement(hash_function, element_);
            //System.out.println("Hashing element " + element_ + " to row " + row + " column " + col);

            matrix.get(index(row, col)).addElement(element_);
            row++;
        }
    }

    public Long countElements() {
        Long elements = Long.valueOf(0);
        for (Integer i = 0; i < w; i++) {
            elements += matrix.get(i).size();
        }
        return elements;
    }

    private Boolean doesTheKeyExceedsTheThresholdOnAllBuckets(Integer element_, Integer threshold_) {
        Integer row = 0;
        for (Map.Entry<Integer,Integer> hash_function: hash_functions.entrySet()) {
            Integer col = hashElement(hash_function, element_);
            //System.out.println("idx " + index(row, col)+ " estimateElementFrequency element " +element_ + " is " +matrix.get(index(row, col)).estimateElementFrequency(element_).to);
            if (matrix.get(index(row, col)).estimateElementFrequency(element_).to < threshold_) {
                return Boolean.FALSE;
            }
            row++;
        }
        return Boolean.TRUE;
    }

    public HashSet<Integer> getHeavyHitters(Integer threshold_) {
        HashSet<Integer> heavy_hitters = new HashSet<>();
        for (Bucket bucket: matrix) {
            if (bucket.size() > threshold_) {
                Set<Integer> keySet = bucket.getKeySet();
                for (Integer element: keySet) {
                    if (doesTheKeyExceedsTheThresholdOnAllBuckets(element, threshold_)) {
                        heavy_hitters.add(element);
                    }
                }
            }
        }
        return heavy_hitters;
    }
}

