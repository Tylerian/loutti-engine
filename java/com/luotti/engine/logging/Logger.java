package com.luotti.engine.logging;

import java.io.*;

import com.luotti.engine.settings.Properties;
import com.luotti.engine.utilities.memory.IDisposable;
import com.luotti.engine.utilities.timestamp.TimeHelper;

public class Logger implements IDisposable {

    private boolean bTrackTrace;
    private boolean bTrackDebug;
    private boolean bTrackWarning;
    private boolean bTrackCritical;

    private FileWriter mFileWriter;
    private BufferedWriter mMemoryWriter;

    public Logger()
    {
        String file = ("com.luotti.errors-" + TimeHelper.getCurrentDate().substring(0, 9).replace("-", "") + ".log");

        try
        {
            this.mFileWriter = new FileWriter(file);
            this.mMemoryWriter = new BufferedWriter(this.mFileWriter);
        }

        catch (IOException ex) { };
    }

    public void enable()
    {
        this.bTrackTrace = Properties.TRACK_TRACE;
        this.bTrackDebug = Properties.TRACK_DEBUG;
        this.bTrackWarning = Properties.TRACK_WARNING;
        this.bTrackCritical = Properties.TRACK_CRITICAL;
    }

    public void disable()
    {
        this.bTrackTrace = false;
        this.bTrackDebug = false;
        this.bTrackWarning = false;
        this.bTrackCritical = false;
    }

    @Override
    public void destruct()
    {
        try
        {
            this.mFileWriter.close();
            this.mMemoryWriter.close();
        }

        catch (IOException ex) { }
    }

    public boolean bootstrap()
    {
        return(
            !this.bTrackDebug   &
            !this.bTrackTrace   &
            !this.bTrackWarning &
            !this.bTrackCritical
        );
    }

    private void printOutTrace(String event)
    {
        if (this.bTrackTrace)
            System.out.println("[" + TimeHelper.getCurrentDate() + "][TRACE] -- " + event);
    }

    private void printOutDebug(String event)
    {
        if (this.bTrackDebug)
            System.out.println("[" + TimeHelper.getCurrentDate() + "][DEBUG] -- " + event);
    }

    private void printOutWarning(String event)
    {
        if (this.bTrackWarning)
            System.out.println("[" + TimeHelper.getCurrentDate() + "][WARNING] -- " + event);
    }

    private void printOutCritical(String event)
    {
        if (this.bTrackCritical)
            System.err.println("[" + TimeHelper.getCurrentDate() + "][CRITICAL] -- " + event);


    }

    public void printOut(LogLevel level, String event)
    {
        switch (level)
        {
            case TRACE: {
                this.printOutTrace(event);
                break;
            }

            case DEBUG: {
                this.printOutDebug(event);
                break;
            }

            case WARNING: {
                this.printOutWarning(event);
                break;
            }

            case CRITICAL: {
                this.printOutCritical(event);
                break;
            }
        }
    }

    public void printOut(LogLevel level, String event, Object stacktrace)
    {
        if (LogLevel.CRITICAL == level)
        {
            StringWriter writer = null;

            if (stacktrace instanceof Exception)
            {
                writer = new StringWriter();
                ((Exception) stacktrace).printStackTrace(new PrintWriter(writer));
            }

            else if (stacktrace instanceof Throwable)
            {
                writer = new StringWriter();
                ((Throwable) stacktrace).getCause().printStackTrace(new PrintWriter(writer));
            }

            if (this.bTrackCritical)
            {
                System.err.println("[" + TimeHelper.getCurrentDate() + "][CRITICAL] -- " + event + "\nStack trace:\n" + writer.toString());
            }

            try
            {
                this.mFileWriter.append(
                    writer.toString()
                );

                this.mFileWriter.flush();
            }

            catch (IOException ex) { }
        }
    }
}
