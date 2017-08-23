package com.lpineda.dsketch;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.*;
import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SketchStepDefinitions {
    private int rows, cols, prime;
    private Sketch sketch_1;
    private Sketch sketch_2;
    private Map<Integer, Integer> hash_functions = null;

    @Given("^I create a sketch of dimensions (\\d+)x(\\d+) with a prime number p=(\\d+)$")
    public void create_a_sketch(Integer rows_, Integer cols_, Integer prime_) {
        hash_functions = Sketch.buildHashFunctions(rows_, prime_);
        rows = rows_;
        cols = cols_;
        prime = prime_;
        sketch_1 = new Sketch(rows, cols, prime, hash_functions);
    }

    @When("^I add (\\d+) random elements to the sketch$")
    public void add_random_elements_to_the_sketch(Integer elements_) {
        Random rand = new Random();
        for (Integer i = 0; i < elements_; i++) {
            sketch_1.addElement(rand.nextInt(Sketch.KEY_MAX_ALLOWED));
        }
    }

    @Then("^The sketch has (\\d+) elements$")
    public void sketch_elements(Long elements_) {
        assertThat(sketch_1.countElements(), is(elements_));
    }

    @When("^I add the elements on ([\\w\\.]+) to the sketch$")
    public void add_random_elements_with_binomial_dist(String filename) throws FileNotFoundException {
        Map<Integer, Integer> m = new HashMap<>();
        Scanner s = new Scanner(new File("./src/test/java/com/lpineda/dsketch/" + filename));
        while (s.hasNext()) {
            Integer e = s.nextInt();
            sketch_1.addElement(e);
            m.put(e, m.getOrDefault(e,0)+1);
        }

    }

    @Then("^I obtain a heavy key (\\d+) exceeding the threshold (\\d+)$")
    public void check_heavy_hitter(Integer element, Integer threshold_) {
        HashSet<Integer> s = sketch_1.getHeavyHitters(threshold_);
        //System.out.println("Heavy hitters " + sketch.getHeavyHitters(threshold_));
        //assertThat(s.size(), is(1));
        assertTrue(s.contains(element));
    }

    @Given("^I create a second sketch with the same parameters$")
    public void create_a_second_sketch() throws NullPointerException {
        if (hash_functions == null ) {
            throw new NullPointerException();
        }
        sketch_2 = new Sketch(rows, cols, prime, hash_functions);
    }

    @When("^I add the elements on ([\\w\\.]+) to the second sketch$")
    public void add_random_elements_to_the_second_sketch(String filename) throws FileNotFoundException {
        Map<Integer, Integer> m = new HashMap<>();
        Scanner s = new Scanner(new File("./src/test/java/com/lpineda/dsketch/" + filename));
        while (s.hasNext()) {
            Integer e = s.nextInt();
            sketch_2.addElement(e);
            m.put(e, m.getOrDefault(e,0)+1);
        }

    }

    @Then ("^There is one heavy changer (\\d+) exceeding threshold (\\d+)$")
    public void i_obtain_heavy_changers(Integer element_, Integer threshold_) {
        assertTrue(sketch_1.getHeavyChangers(threshold_, sketch_2).contains(element_));
    }
}
