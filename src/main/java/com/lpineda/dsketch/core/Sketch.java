package com.lpineda.dsketch.core;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.math3.primes.Primes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sketch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sketch.class);

    private int w, d, p, max_bucket_length;
    private Long biggest_bucket_size_on_sketch;
    private Map<Integer, Integer> hash_functions;
    private List<Bucket> matrix;
    public static int KEY_MAX_ALLOWED;

    public Sketch(Integer d_, Integer w_, Integer p_, Map<Integer, Integer> hash_functions_) {
        LOGGER.debug(MessageFormat.format("Initializing {0}", Sketch.class.getName()));
        w = w_;
        d = d_;
        p = p_;
        max_bucket_length = 8;
        biggest_bucket_size_on_sketch = (long)0;
        if (p < w) {
            LOGGER.warn(String.format("Sketch \'cols\' must be minor than \'prime\'. " +
                    "Configured: \'cols\': %d, \'prime\': %d.", this.w, this.p));
        }
        if (!Primes.isPrime(p_)) {
            LOGGER.warn(String.format("Building sketch with an invalid parameter. " +
                    "Parameter \'prime\' must be prime. Configured: %d", this.d));
        }
        KEY_MAX_ALLOWED = Integer.MAX_VALUE / p;
        hash_functions = hash_functions_;

        matrix = new ArrayList<>();
        for (Integer i = 0; i < w * d; i++) {
            matrix.add(new Bucket(max_bucket_length));
        }
        LOGGER.debug(String.format("Sketch configuration. Height (rows): %d. Width (cols): %d. Prime: %d", d, w, p));
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
        return ((a * element_ + b) % p) % w;
    }

    public void addElement(Integer element_) {
        assert (element_ < KEY_MAX_ALLOWED);
        Integer row = 0;
        Long biggest_bucket_size = (long)0;
        for (Map.Entry<Integer, Integer> hash_function : hash_functions.entrySet()) {
            Integer col = hashElement(hash_function, element_);
            //System.out.println("Hashing element " + element_ + " to row " + row + " column " + col);
            Bucket bucket = matrix.get(index(row, col));
            bucket.addElement(element_);
            if (bucket.size() > biggest_bucket_size)
                biggest_bucket_size = bucket.size();
            row++;
        }

        if (biggest_bucket_size > biggest_bucket_size_on_sketch) {
            biggest_bucket_size_on_sketch = biggest_bucket_size;
        }
    }

    public Long getBiggestBucketCounter() {
        return biggest_bucket_size_on_sketch;
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
            Integer element_, Sketch old_sketch, Integer threshold_) {

        assert (d == old_sketch.d);
        assert (threshold_ > getMeanError());
        Integer row = 0;
        for (Map.Entry<Integer, Integer> hash_function : hash_functions.entrySet()) {
            Integer col = hashElement(hash_function, element_);
            SumEstimation estimate_first_epoch = this.matrix.get(index(row, col)).estimateElementFrequency(element_);
            SumEstimation estimate_second_epoch = old_sketch.matrix.get(index(row, col)).estimateElementFrequency(element_);
            //System.out.println("Key " + element_ + " difference " + SumEstimation.estimateChange(estimate_first_epoch, estimate_second_epoch));
            row++;
            if (SumEstimation.estimateChange(estimate_first_epoch, estimate_second_epoch) < threshold_) {
                return false;
            }
        }
        return true;
    }

    public HashSet<Integer> getHeavyChangers(Integer threshold_, Sketch old_sketch) {
        HashSet<Integer> heavy_changers = new HashSet<>();
        //System.out.println("Mean error " + getMeanError());
        assert (matrix.size() == old_sketch.matrix.size());
        for (Integer i = 0; i < matrix.size(); i++) {
            Bucket bucket_on_first_epoch = matrix.get(i);
            Bucket bucket_on_second_epoch = old_sketch.matrix.get(i);
            if (bucket_on_first_epoch.size() > threshold_ || bucket_on_second_epoch.size() > threshold_) {
                Set<Integer> potential_heavy_changers = new HashSet<>();
                potential_heavy_changers.addAll(bucket_on_first_epoch.getKeySet());
                potential_heavy_changers.addAll(bucket_on_second_epoch.getKeySet());
                for (Integer key : potential_heavy_changers) {
                    if (theDifferenceBetweenFrequencyEstimationExceedsTheThresholdOnAllBuckets(key,old_sketch,threshold_)) {
                        heavy_changers.add(key);
                    }
                }
            }
        }
        return heavy_changers;
    }
}

