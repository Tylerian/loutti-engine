package com.luotti.engine.utilities.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.luotti.engine.Environment;

public class Randomizer {

    public static Random RANDOM;

    static {
        Randomizer.RANDOM = new Random(
            Environment.traceNanoTime() ^ Environment.traceMilliTime()
        );
    }

    public static int nextInt()
    {
        return ThreadLocalRandom.current().nextInt();
    }

    public static double nextDouble()
    {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static int nextInt(int minimum, int maximum)
    {
        return Math.abs(minimum + (ThreadLocalRandom.current().nextInt()  % ( (maximum - minimum) + 1) ) );
    }
}
