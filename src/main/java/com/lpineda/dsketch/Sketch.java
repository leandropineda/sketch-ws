package com.lpineda.dsketch;

import java.util.*;

import org.apache.commons.math3.primes.Primes;

public class Sketch {
    private int w, d, p, max_bucket_length;
    private Map<Integer, Integer> hash_functions;
    private List<Bucket> matrix;
    public static int KEY_MAX_ALLOWED;

    public Sketch(Integer d_, Integer w_, Integer p_, Map<Integer, Integer> hash_functions_) {
        w = w_;
        d = d_;
        p = p_;
        max_bucket_length = 8;
        assert (p >= w);
        assert (Primes.isPrime(p_));
        KEY_MAX_ALLOWED = Integer.MAX_VALUE / p;
        hash_functions = hash_functions_;

        matrix = new ArrayList<>();
        for (Integer i = 0; i < w * d; i++) {
            matrix.add(new Bucket(max_bucket_length));
        }
    }

    public static Map<Integer, Integer> buildHashFunctions(Integer n_functions, Integer max_constant_value) {
        LinkedHashMap<Integer, Integer> hash_functions = new LinkedHashMap<>();
        Random rand = new Random();
        while (hash_functions.size() < n_functions) {
            hash_functions.put(rand.nextInt((max_constant_value - 2)) + 1,
                    rand.nextInt((max_constant_value - 1)));
        }

        return hash_functions;
    }

    private int index(int i, int j) {
        return (i * w) + j;

    }

    private Integer hashElement(Map.Entry<Integer, Integer> hash_function, int element_) {
        int a = hash_function.getKey();
        int b = hash_function.getValue();
        int hashed_element = ((a * element_ + b) % p) % w;
        return (hashed_element);
    }

    public void addElement(Integer element_) {
        assert (element_ < KEY_MAX_ALLOWED);
        Integer row = 0;
        for (Map.Entry<Integer, Integer> hash_function : hash_functions.entrySet()) {
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

    public Long getMeanError() {
        Long mean_err = Long.valueOf(0);
        for (Bucket bckt: matrix) {
            mean_err += bckt.getError();
        }
        return mean_err / (d*w);
    }

    private Boolean theKeyExceedsTheThresholdOnAllBuckets(Integer element_, Integer threshold_) {
        Integer row = 0;
        for (Map.Entry<Integer, Integer> hash_function : hash_functions.entrySet()) {
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
        for (Bucket bucket : matrix) {
            if (bucket.size() > threshold_) {
                Set<Integer> keySet = bucket.getKeySet();
                for (Integer element : keySet) {
                    if (theKeyExceedsTheThresholdOnAllBuckets(element, threshold_)) {
                        heavy_hitters.add(element);
                    }
                }
            }
        }
        return heavy_hitters;
    }

    private boolean theDifferenceBetweenFrequencyEstimationExceedsTheThresholdOnAllBuckets (
            Integer element_, Sketch sketch_, Integer threshold_) {
            assert (d == sketch_.d);
            assert (threshold_ > getMeanError());
            Integer row = 0;
            for (Map.Entry<Integer, Integer> hash_function : hash_functions.entrySet()) {
                Integer col = hashElement(hash_function, element_);
                Range estimate_first_epoch = matrix.get(index(row, col)).estimateElementFrequency(element_);
                Range estimate_second_epoch = sketch_.matrix.get(index(row, col)).estimateElementFrequency(element_);
                //System.out.println("Key " + element_ + " difference " + Range.estimateChange(estimate_first_epoch, estimate_second_epoch));
                if (Range.estimateChange(estimate_first_epoch, estimate_second_epoch) < threshold_) {
                    return false;
                }
                row++;
            }
        return true;
    }

    public HashSet<Integer> getHeavyChangers(Integer threshold_, Sketch sketch_) {
        HashSet<Integer> heavy_changers = new HashSet<>();
        //System.out.println("Mean error " + getMeanError());
        assert (matrix.size() == sketch_.matrix.size());
        for (Integer i = 0; i < matrix.size(); i++) {
            Bucket bucket_on_first_epoch = matrix.get(i);
            Bucket bucket_on_second_epoch = sketch_.matrix.get(i);
            if (bucket_on_first_epoch.size() > threshold_ || bucket_on_second_epoch.size() > threshold_) {
                Set<Integer> potential_heavy_changers = new HashSet<>();
                potential_heavy_changers.addAll(bucket_on_first_epoch.getKeySet());
                potential_heavy_changers.addAll(bucket_on_second_epoch.getKeySet());
                for (Integer key : potential_heavy_changers) {
                    if (theDifferenceBetweenFrequencyEstimationExceedsTheThresholdOnAllBuckets(key,sketch_,threshold_)) {
                        heavy_changers.add(key);
                    }
                }
            }
        }
        return heavy_changers;
    }
}

