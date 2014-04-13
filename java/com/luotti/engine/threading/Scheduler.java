package com.luotti.engine.threading;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;

import com.luotti.engine.utilities.memory.IDisposable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Scheduler implements IDisposable {

    private String sIdentifier;
    private static AtomicInteger SCHEDULER_ID;
    private ScheduledThreadPoolExecutor mScheduler;

    static {
        Scheduler.SCHEDULER_ID = new AtomicInteger();
    }

    private void purge()
    {
        this.mScheduler.purge();
    }

    public int getPoolSize()
    {
        return this.mScheduler.getPoolSize();
    }

    public float getLoadFactor()
    {
        return (this.mScheduler.getQueue().size() / Properties.SCHEDULER_QUEUE_SIZE) * 100;
    }

    public String getIdentifier()
    {
        return this.sIdentifier;
    }

    public void resizePool(int size)
    {
        this.mScheduler.setCorePoolSize(size);
    }

    public Scheduler construct(int size)
    {
        this.sIdentifier = "SCHEDULER-" + SCHEDULER_ID.getAndIncrement();

        this.mScheduler = new ScheduledThreadPoolExecutor(size,
            new ThreadFactoryBuilder().setNameFormat(this.sIdentifier + "-THREAD-%d").build()
        );

        this.mScheduler.prestartAllCoreThreads(); this.scheduleAtFixedRate(
            new Runnable() { @Override public void run() { Scheduler.this.purge(); } },
            Properties.SCHEDULER_PURGATION_INTERVAL, Properties.SCHEDULER_PURGATION_INTERVAL
        );

        Environment.getLogger().printOut(LogLevel.DEBUG,  "Instantiated a new Scheduler with a pool size of: " + size + ".");

        return this;
    }

    @Override
    public void destruct()
    {
        // if (notify == true) {
        List pending = this.mScheduler.shutdownNow();
        Environment.getLogger().printOut(LogLevel.DEBUG, "Destroying " + this.sIdentifier + ", with " + pending.size() + " tasks awaiting execution.");
        // }
    }

    public ScheduledFuture<?> schedule(Runnable task, long delay)
    {
        return this.mScheduler.schedule(task,delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long delay, long interval)
    {
        return this.mScheduler.scheduleAtFixedRate(task, delay, interval, TimeUnit.MILLISECONDS);
    }
}