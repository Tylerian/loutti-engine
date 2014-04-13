package net.luotti.engine.communication.events;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.threading.Executor;
import com.luotti.engine.settings.Properties;

import net.luotti.engine.communication.sessions.Session;
import net.luotti.engine.communication.protocol.MessageRequest;

public class EventDispatcher implements IEventDispatcher, Runnable {

    private int iQueueLimit;
    private boolean bIsActive;
    private String sIdentifier;
    private Executor mExecutor;
    private BlockingQueue mQueue;
    private static AtomicInteger THREAD_ID;
    private static AtomicInteger DISPATCHER_ID;

    static {
        EventDispatcher.THREAD_ID = new AtomicInteger();
        EventDispatcher.DISPATCHER_ID = new AtomicInteger();
    }

    @Override
    public void run()
    {
        IEventListener listener = null;
        Thread.currentThread().setName(sIdentifier + "-THREAD-" + THREAD_ID.incrementAndGet());

        try
        {
            listener = (IEventListener) Class.forName(Properties.EVENT_LISTENER_QUALIFIED_CLASS).newInstance();
        }

        catch (Exception ex)
        {
            Environment.printOutBootError("IEventDispatcher has thrown an exception.");
        }

        while (this.bIsActive)
        {
            try
            {
                long stop, start;
                MessageRequest request = (MessageRequest) this.mQueue.take();
                start = Environment.traceNanoTime(); listener.invoke(request); stop = Environment.traceNanoTime();
                Environment.getLogger().printOut(LogLevel.DEBUG, "Took " + ((stop - start)  /  1000000.0D)+ "ms to process the request.");
            }

            catch (Exception ex)
            {
                Environment.getLogger().printOut(LogLevel.CRITICAL, sIdentifier + " has thrown an exception.", ex);
            }
        }
    }

    @Override
    public void destruct()
    {
        this.mQueue.clear();
        this.bIsActive = false;
        this.mExecutor.destruct();

        this.mQueue = null;
        this.mExecutor = null;
        this.sIdentifier = null;
    }

    public EventDispatcher()
    {
        this.bIsActive = true;
        this.iQueueLimit = Properties.DISPATCHER_QUEUE_LIMIT;
        this.sIdentifier = "DISPATCHER-" + DISPATCHER_ID.incrementAndGet();
        this.mExecutor = new Executor().construct(Properties.DISPATCHER_EXECUTOR_SIZE);
        this.mQueue = new ArrayBlockingQueue(Properties.DISPATCHER_QUEUE_LIMIT, false);

        for (int i = 0; i < Properties.DISPATCHER_EXECUTOR_SIZE; i++) this.mExecutor.execute(this);
    }

    @Override
    public int getLoadFactor()
    {
        return (this.mQueue.size()
        / this.iQueueLimit) * 100;
    }

    @Override
    public int getEventQueueSize()
    {
        return this.mQueue.size();
    }

    @Override
    public int getEventQueueLimit()
    {
        return this.iQueueLimit;
    }

    @Override
    public int getExecutorPoolSize()
    {
        return Properties.DISPATCHER_EXECUTOR_SIZE;
    }

    @Override
    public void resizeEventQueueSize(int size)
    {
        if (size <= this.iQueueLimit) { return; }
        BlockingQueue queue = new ArrayBlockingQueue(size, false);
        this.iQueueLimit = size; this.mQueue.drainTo(queue); this.mQueue = queue;
    }

    @Override
    public void resizeExecutorPoolSize(int size)
    {
        this.mExecutor.resizePool(size);
    }

    @Override
    public void enqueue(Session session, MessageRequest request)
    {
        request.setSession(session);
        if (this.iQueueLimit > this.mQueue.size()) { this.mQueue.add(request); return; } request.destruct();
        Environment.getLogger().printOut(LogLevel.WARNING, "The request queue is full, dropping incoming request...");
    }
}