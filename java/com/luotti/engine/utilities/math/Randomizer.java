package com.luotti.engine.utilities.math;

import java.util.Random;

import com.luotti.engine.Environment;

public class Randomizer {

    private static Random mRandom;

    static {
        Randomizer.mRandom =
        new Random(Environment.traceNanoTime());
    }

    public static int nextInt()
    {
        return Randomizer.mRandom.nextInt();
    }

    public static int nextInt(int minimum, int maximum)
    {
        return Math.abs(minimum + (Randomizer.mRandom.nextInt()  % ( (maximum - minimum) + 1) ) );
    }
}
