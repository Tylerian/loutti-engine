package com.luotti.engine.settings;

import com.luotti.engine.Environment;

public class Properties {

    static {
        int size = (Properties.BASE_THREAD_POOL_SIZE * Properties.EXTRA_THREADS_PER_CORE);
        Properties.THREAD_POOL_SIZE = size > Properties.THREAD_POOL_SIZE  ? size : Properties.THREAD_POOL_SIZE;
    }

    // Longs
    public static final long SESSION_CHECK_INTERVAL = 30000;
    public static final long SCHEDULER_PURGATION_INTERVAL = 60000;

    // Bytes
    public static final byte DATABASE_POOL_SIZE = (byte) Environment.getProperties().getInt("sql.connections.average", 10);

    // Shorts
    public static final short NIO_GAME_PORT = Environment.getProperties().getShort("nio.game.port", (short) 10110);
    public static final short NIO_RCON_PORT = Environment.getProperties().getShort("nio.rcon.port", (short) 10111);
    public static final short DATABASE_SERVER_PORT = Environment.getProperties().getShort("sql.server.port", (short) 3306);

    // Booleans
    public static final boolean TRACK_TRACE = Environment.getProperties().getBoolean("logger.track.trace", false);
    public static final boolean TRACK_DEBUG = Environment.getProperties().getBoolean("logger.track.debug", false);
    public static final boolean TRACK_WARNING = Environment.getProperties().getBoolean("logger.track.warning", false);
    public static final boolean TRACK_CRITICAL = Environment.getProperties().getBoolean("logger.track.critical", false);
    public static final boolean MESSAGE_REQUEST_PROFILING = Environment.getProperties().getBoolean("profiling.message.requests", false);
    public static final boolean MESSAGE_RESPONSE_PROFILING = Environment.getProperties().getBoolean("profiling.message.responses", false);

    // Integers
    public static int THREAD_POOL_SIZE = 4;
    public static final int EXECUTOR_POOL_SIZE = Environment.getProperties().getInt("executor.pool.size", 1);
    public static final int EXECUTOR_QUEUE_SIZE = Environment.getProperties().getInt("executor.queue.size", 50);
    public static final int SCHEDULER_POOL_SIZE = Environment.getProperties().getInt("scheduler.pool.size",  1);
    public static final int SCHEDULER_QUEUE_SIZE = Environment.getProperties().getInt("scheduler.queue.size",50);
    public static final int DISPATCHER_QUEUE_LIMIT = Environment.getProperties().getInt("dispatcher.queue.limit", 5000);
    public static final int DISPATCHER_EXECUTOR_SIZE = Environment.getProperties().getInt("dispatcher.executor.size", 2);

    public static final int BASE_THREAD_POOL_SIZE = Environment.getProperties().getInt("thread.pool.size",  4);
    public static final int EXTRA_THREADS_PER_CORE = Environment.getProperties().getInt("thread.pool.core", 1);
    public static final int MAXIMUM_THREAD_RUNTIME_WITHOUT_WARNING = Environment.getProperties().getInt("thread.pool.runtime", 500);



    // Strings
    public static final String DATABASE_NAME = Environment.getProperties().get("sql.database.name", "{non-defined}");
    public static final String DATABASE_SERVER_NAME = Environment.getProperties().get("sql.server.name", "{non-defined}");
    public static final String DATABASE_CREDENTIALS_NAME = Environment.getProperties().get("sql.credentials.name", "{non-defined}");
    public static final String DATABASE_CREDENTIALS_PASSWORD = Environment.getProperties().get("sql.credentials.password", "{non-defined}");

    public static final String EVENT_LISTENER_QUALIFIED_CLASS = Environment.getProperties().get("event.listener.class", "{non-defined}");
}
