package com.luotti.engine.threading;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;
import com.luotti.engine.utilities.memory.IDisposable;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public class ThreadController implements IDisposable {

    private List<Executor> lExecutors;
    private List<Scheduler> lSchedulers;

    public ThreadController()
    {
        this.lExecutors = new ArrayList();
        this.lSchedulers = new ArrayList();

        for (int i = 0; i < Properties.EXECUTOR_POOL_SIZE; i++)
        {
            this.lExecutors.add(new Executor().construct(Properties.THREAD_POOL_SIZE));
        }

        for (int i = 0; i < Properties.SCHEDULER_POOL_SIZE; i++)
        {
            this.lSchedulers.add(new Scheduler().construct(Properties.THREAD_POOL_SIZE));
        }
    }

    @Override
    public void destruct()
    {
        byte executor_count = 0;
        for (Executor executor : this.lExecutors)
        {
            executor_count++;
            executor.destruct();
        }

        byte scheduler_count = 0;
        for (Scheduler scheduler : this.lSchedulers)
        {
            scheduler_count++;
            scheduler.destruct();
        }

        Environment.getLogger().printOut(LogLevel.DEBUG, "ThreadFactory has been destroyed with: " + executor_count + " executors and " + scheduler_count + " schedulers.");
    }

    public boolean bootstrap()
    {
        return this.getQuietestExecutor() != null
                && this.getQuietestScheduler() != null;
    }

    public Executor getQuietestExecutor()
    {
        float loadFactor = 100.0F;
        Executor quietest =  null;

        synchronized (this.lExecutors)
        {
            for (Executor executor : lExecutors)
            {
                if (executor.getLoadFactor() < loadFactor)
                {
                    quietest = executor;
                    loadFactor = executor.getLoadFactor();
                }
            }
        }

        return quietest;
    }

    public Scheduler getQuietestScheduler()
    {
        float loadFactor = 100.0F;
        Scheduler quietest = null;

        synchronized (this.lSchedulers)
        {
            for (Scheduler scheduler : this.lSchedulers)
            {
                if (scheduler.getLoadFactor() < loadFactor)
                {
                    quietest = scheduler;
                    loadFactor = scheduler.getLoadFactor();
                }
            }
        }

        return quietest;
    }

    public void execute(Runnable task)
    {
        this.getQuietestExecutor().execute(task);
    }

    public Future submit(Runnable task)
    {
        return this.getQuietestExecutor().submit(task);
    }

    public ScheduledFuture<?> schedule(Runnable task, long delay)
    {
        return this.getQuietestScheduler().schedule(task, delay);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long delay, long interval)
    {
        return this.getQuietestScheduler().scheduleAtFixedRate(task, delay, interval);
    }
}
