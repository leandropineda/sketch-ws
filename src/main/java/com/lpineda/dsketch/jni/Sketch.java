package com.lpineda.dsketch.jni;

/**
 * Created by lpineda on 16/8/2017.
 */
public class Sketch {
    static {
        System.load("C:\\repos\\sketch-base\\bin\\sketch.dll"); // Load native library at runtime
    }

    // Declare native methods that receives nothing and returns void
    private native void clear();

    // Test Driver
    public static void main(String[] args) {
        Sketch s = new Sketch();
        s.clear();
    }
}
