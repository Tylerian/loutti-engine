package com.luotti.engine;

import com.luotti.engine.logging.Logger;
import com.luotti.engine.settings.Properties;
import com.luotti.engine.settings.PropertiesBox;
import com.luotti.engine.storage.DatabaseController;
import com.luotti.engine.threading.ThreadController;
import com.luotti.engine.utilities.timestamp.TimeHelper;
import net.luotti.engine.communication.CommunicationController;

public class Environment {

    private static Logger mLogger;
    private static PropertiesBox mPropertiesBox;
    private static ThreadController mThreadController;
    private static DatabaseController mDatabaseController;
    private static CommunicationController mCommunicationController;

    public static long START_UP_TIME = 0x00000000;
    private static final byte NORMAL_TERMINATION = 0x01;
    private static final byte ABNORMAL_TERMINATION = 0x00;

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

    public static Logger getLogger()
    {
        return Environment.mLogger;
    }

    public static long traceNanoTime()
    {
        return System.nanoTime();
    }

    public static long traceMilliTime()
    {
        return System.currentTimeMillis();
    }

    public static PropertiesBox getProperties()
    {
        return Environment.mPropertiesBox;
    }

    public static ThreadController getThreadController()
    {
        return Environment.mThreadController;
    }

    public static DatabaseController getDatabaseController() { return Environment.mDatabaseController; }

    public static CommunicationController getCommunication() { return Environment.mCommunicationController; }

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
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT INFO] -- " + event);
    }

    public static void printOutBootError(String event)
    {
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- " + event);
        System.err.println("[" + TimeHelper.getCurrentDate() + "][BOOT ERROR] -- Core bootstrap will now exit.");

        Environment.terminate(true);
    }


    public static boolean bootstrap(String properties)
    {
        try
        {
            // Track booting time
            Environment.START_UP_TIME =
            Environment.traceNanoTime();

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
                else { Environment.printOutBootInfo("CommunicationFactory has been successfully initialized with: " + 1 + " i/o boss and " + 4 + " i/o workers."); }

                // All right
                return true;
            }

        }

        catch (Exception ex)
        {
            Environment.printOutBootError("Environment.bootstrap() has thrown an exception while bootstrapping!");
        }

        // W00t!?
        return false;
    }
}
