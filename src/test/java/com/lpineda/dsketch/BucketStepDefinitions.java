package com.lpineda.dsketch;

import com.lpineda.dsketch.core.Bucket;
import com.lpineda.dsketch.core.SumEstimation;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.apache.commons.math3.distribution.BinomialDistribution;
import java.lang.Math;

public class BucketStepDefinitions {
    private Bucket bucket;

    @Given("^A bucket of maximum length (.*)$")
    public void i_create_a_bucket_of_length(Integer length_) throws Throwable {
        bucket = new Bucket(length_);
    }

    @When("^I add (\\d+) randomly chosen elements to the bucket (.*) times$")
    public void i_add_randomly_chosen_elements_to_the_bucket(Integer different_elements_, Integer times_) throws Throwable {
        Random rand = new Random();
        List<Integer> elements = new ArrayList<>();

        for (Long i = Long.valueOf(0); i < different_elements_; i++) {
            elements.addAll(Collections.nCopies(times_, rand.nextInt()));
        }

        Collections.shuffle(elements);
        for (Integer element : elements) {
            bucket.addElement(element);
        }
    }


    @Then("^The size of the bucket is (\\d+)$")
    public void the_size_of_the_bucket_is(Long expected_size_) throws Throwable {
        assertThat(bucket.size(), is(expected_size_));
    }

    @Then("^The size of the bucket is less than (\\d+)$")
    public void the_size_of_the_bucket_is_less_than(Long upper_bound_size_) throws Throwable {
        assertTrue(Long.valueOf(bucket.keySetSize()) <= upper_bound_size_);
    }

    @Then("^The number of different elements is (\\d+)$")
    public void the_number_of_different_elements_in_the_bucket_is(Integer n_elements_) throws Throwable {
        assertThat(bucket.keySetSize(), is(n_elements_));
    }

    @When("^I insert (\\d+) keys between 0 and (\\d+) which were generated with a binomial distribution the most probable key is estimated correctly$")
    public void i_insert_into_the_bucket_random_keys_with_binomial_distribution_and_estimate_the_frequency(Integer n_elements_, Integer key_range_) throws Throwable {
        BinomialDistribution rand = new BinomialDistribution(key_range_, 0.5);
        Map<Integer, Long> map = new HashMap<>();

        for (Integer i = 0; i < n_elements_; i++) {
            Integer key = rand.sample();

            bucket.addElement(key);
            map.put(key, map.getOrDefault(key, Long.valueOf(0)) + 1);
        }

        Map.Entry<Integer, Long> maxEntry = null;
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        SumEstimation estimated_frequency = bucket.estimateElementFrequency(maxEntry.getKey());

        assertTrue(Math.abs(estimated_frequency.to - maxEntry.getValue()) <= 1);
        assertTrue((maxEntry.getValue() >= estimated_frequency.from && maxEntry.getValue() <= estimated_frequency.to));
    }


}