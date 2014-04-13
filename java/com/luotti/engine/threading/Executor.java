package com.luotti.engine.threading;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;

import com.luotti.engine.utilities.memory.IDisposable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Executor implements IDisposable {

    private String sIdentifier;
    private ThreadPoolExecutor mExecutor;
    private static AtomicInteger EXECUTOR_ID;

    static {
        Executor.EXECUTOR_ID = new AtomicInteger();
    }

    public void purge()
    {
        this.mExecutor.purge();
    }

    public int getPoolSize()
    {
        return this.mExecutor.getPoolSize();
    }

    public float getLoadFactor()
    {
        return (this.mExecutor.getQueue().size() / Properties.EXECUTOR_QUEUE_SIZE) * 100;
    }

    public String getIdentifier()
    {
        return this.sIdentifier;
    }

    public void resizePool(int size)
    {
        this.mExecutor.setCorePoolSize(size);
    }

    public void execute(Runnable task)
    {
        this.mExecutor.execute(task);
    }

    public Future submit(Runnable task)
    {
        return this.mExecutor.submit(task);
    }

    public Executor construct(int size)
    {
        this.sIdentifier = "EXECUTOR-" + EXECUTOR_ID.getAndIncrement();

        this.mExecutor = new ThreadPoolExecutor(size, size, 500L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<Runnable>(Properties.EXECUTOR_QUEUE_SIZE), new ThreadFactoryBuilder().setNameFormat(this.sIdentifier + "-THREAD-%d").build());
        this.mExecutor.prestartAllCoreThreads(); Environment.getLogger().printOut(LogLevel.DEBUG,  "Instantiated a new Executor with a pool size of: " + size + ".");

        return this;
    }

    @Override
    public void destruct()
    {
        // if (notify == true) {
        List pending = this.mExecutor.shutdownNow();
        Environment.getLogger().printOut(LogLevel.DEBUG, "Destroying " + this.sIdentifier + ", with " + pending.size() + " tasks awaiting execution.");
        // }
    }
}