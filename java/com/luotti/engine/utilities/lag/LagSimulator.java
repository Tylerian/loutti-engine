package com.luotti.engine.utilities.lag;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;

public class LagSimulator {

    public static void simulate(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }

        catch (InterruptedException ex)
        {
            Environment.getLogger().printOut(LogLevel.CRITICAL, "LagSimulator.simulate(" + milliseconds + ") has thrown an exception.", ex);
        }
    }
}
