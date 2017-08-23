package com.lpineda.dsketch;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.*;
import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SketchStepDefinitions {
    private Sketch sketch;

    @Given("^I create a sketch of dimensions (\\d+)x(\\d+) with a prime number p=(\\d+)$")
    public void create_a_sketch(Integer rows, Integer cols, Integer prime) {
        sketch = new Sketch(rows, cols, prime);
    }

    @When("^I add (\\d+) random elements to the sketch$")
    public void add_random_elements_to_the_sketch(Integer elements_) {
        Random rand = new Random();
        for (Integer i = 0; i < elements_; i++) {
            sketch.addElement(rand.nextInt(Integer.MAX_VALUE));
        }
    }

    @Then("^The sketch has (\\d+) elements$")
    public void sketch_elements(Long elements_) {
        assertThat(sketch.countElements(), is(elements_));
    }

    @When("^I add the elements on (.*) generated with a binomial distribution$")
    public void add_random_elements_with_binomial_dist(String filename) throws FileNotFoundException {
        Map<Integer, Integer> m = new HashMap<>();
        Scanner s = new Scanner(new File("./src/test/java/com/lpineda/dsketch/" + filename));
        while (s.hasNext()) {
            Integer e = s.nextInt();
            sketch.addElement(e);
            m.put(e, m.getOrDefault(e,0)+1);
        }

    }

    @Then("^I obtain a heavy key (\\d+) exceeding the threshold (\\d+)$")
    public void check_heavy_hitter(Integer element, Integer threshold_) {
        HashSet<Integer> s = sketch.getHeavyHitters(threshold_);
        System.out.println("Heavy hitters " + sketch.getHeavyHitters(threshold_));
        //assertThat(s.size(), is(1));
        assertTrue(s.contains(element));
    }
}
