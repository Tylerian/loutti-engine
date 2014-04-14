package com.luotti.engine;

import com.luotti.engine.logging.Logger;
import com.luotti.engine.settings.Properties;
import com.luotti.engine.settings.PropertiesBox;
import com.luotti.engine.storage.DatabaseController;
import com.luotti.engine.threading.ThreadController;
import com.luotti.engine.utilities.timestamp.TimeHelper;
import com.luotti.engine.utilities.runtime.GarbageController;
import com.luotti.engine.utilities.runtime.RuntimeController;

import net.luotti.engine.communication.CommunicationBootstrap;
import net.luotti.engine.communication.CommunicationController;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Environment {

    private static Logger mLogger;
    private static PropertiesBox mPropertiesBox;
    private static ThreadController mThreadController;
    private static GarbageController mGarbageController;
    private static RuntimeController mRuntimeController;
    private static DatabaseController mDatabaseController;
    private static CommunicationController mCommunicationController;

    public  static long START_UP_TIME = 0x00000000;
    private static final byte NORMAL_TERMINATION = 0x01;
    private static final byte ABNORMAL_TERMINATION = 0x00;

    // region #Accessors
    public static long traceNanoTime()
    {
        return System.nanoTime();
    }

    public static long traceMilliTime()
    {
        return System.currentTimeMillis();
    }

    public static Logger getLogger()
    {
        return Environment.mLogger;
    }

    public static PropertiesBox getProperties()
    {
        return Environment.mPropertiesBox;
    }

    public static ThreadController getThreadController()
    {
        return Environment.mThreadController;
    }

    public static GarbageController getGarbageController()
    {
        return Environment.mGarbageController;
    }

    public static RuntimeController getRuntimeController()
    {
        return Environment.mRuntimeController;
    }

    public static DatabaseController getDatabaseController() { return Environment.mDatabaseController; }

    public static CommunicationController getCommunication() { return Environment.mCommunicationController; }
    // endregion

    // region #Methods
    private static void printOutBootBanner()
    {
        System.out.println();
        System.out.println("######################################");
        System.out.println("##      LUOTTI GAME FRAMEWORK       ##");
        System.out.println("##      WRITTEN BY: JAIRO EÖG       ##");
        System.out.println("######################################");
        System.out.println("##      HTTP://WWW.LUOTTI.COM       ##");
        System.out.println("######################################");
        System.out.println("##      ENGINE BUILD: 1.0-dev       ##");
        System.out.println("######################################");
        System.out.println("##      JDK VERSION: " + System.getProperty("java.version") + "      ##");
        System.out.println("######################################");
        System.out.println();
    }

    public static void terminate(boolean force)
    {
        if (force == false)
        {
            Environment.mLogger.destruct();
            Environment.mPropertiesBox.destruct();
        }

        System.exit(!force ? NORMAL_TERMINATION : ABNORMAL_TERMINATION);
    }

    public static void printOutBootInfo(String event)
    {
        System.out.println("[" + TimeHelper.getCurrentDate() + "][BOOT INFO] -- " + event);
    }

    public static void printOutBootError(String event)
    {
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- " + event);
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- Core bootstrap will now exit.");

        Environment.terminate(true);
    }

    public static void printOutBootError(String event, Exception ex)
    {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));

        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- " + event + "\nStack trace: " + writer.toString());
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- Core startup will now exit."); Environment.terminate(true);
    }
    // endregion

    // region #Constructor
    public static boolean bootstrap(String properties)
    {
        try
        {
            // Track booting time
            Environment.START_UP_TIME =
            Environment.traceMilliTime();

            // PrintOut some shoutouts
            Environment.printOutBootBanner();

            Environment.mPropertiesBox = new PropertiesBox();

            if (Environment.mPropertiesBox.bootstrap(properties))
            {
                Environment.printOutBootInfo("Loaded " + Environment.mPropertiesBox.size() + " properties from " + properties);

                Environment.mLogger = new Logger();
                if (Environment.mLogger.bootstrap() == false) { return false; }
                else { Environment.printOutBootInfo("System logging interface has been successfully initialized."); }


                Environment.mDatabaseController = new DatabaseController();
                if (Environment.mDatabaseController.bootstrap() == false) { return false; }
                else { Environment.printOutBootInfo("Database and connection pool has been successfully initialized."); }


                Environment.mThreadController = new ThreadController();
                if (Environment.mThreadController.bootstrap() == false) { return false; }
                else { Environment.printOutBootInfo("ThreadController has been successfully initialized with: " + Properties.EXECUTOR_POOL_SIZE + " executors and " + Properties.SCHEDULER_POOL_SIZE + " schedulers."); }

                Environment.mCommunicationController = new CommunicationController();
                if (Environment.mCommunicationController.bootstrap() == false) { return false; }
                else { Environment.printOutBootInfo("CommunicationController has been successfully initialized with: " + CommunicationBootstrap.BOSS_POOL_SIZE + " i/o boss and " + CommunicationBootstrap.WORKER_POOL_SIZE + " i/o workers."); }

                // All is initialized... so start runtime monitors!
                Environment.mGarbageController = new GarbageController();
                Environment.mRuntimeController = new RuntimeController();

                Environment.printOutBootInfo("Luotti Framework has been successfully initialized in: " + (Environment.traceMilliTime() - Environment.START_UP_TIME) / 1000 + " seconds!"); System.out.println();

                // All right
                return true;
            }
        }

        catch (Exception ex)
        {
            Environment.printOutBootError("Environment.bootstrap() has thrown an exception while bootstrapping!", ex);
        }

        // W00t!?
        return false;
    }
    // endregion
}
