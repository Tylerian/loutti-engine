package com.luotti.engine.utilities.math;

import java.util.Random;

public class Randomizer {

    private static Random mRandom;

    static {
        Randomizer.mRandom =
        new Random(System.nanoTime());
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
