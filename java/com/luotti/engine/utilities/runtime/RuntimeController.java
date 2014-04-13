package com.luotti.engine.utilities.runtime;

import java.lang.management.ManagementFactory;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;

import com.sun.management.OperatingSystemMXBean;

public class RuntimeController implements Runnable {

    private Thread tMonitor;
    private OperatingSystemMXBean mBean;

    private volatile int iCPUPeak;
    private volatile int iRAMPeak;
    private volatile int iRAMFree;
    private volatile int iRAMHeap;
    private volatile int iCPUUsage;
    private volatile int iRAMUsage;
    private volatile int iSpacePeak;
    private volatile int iPlayerPeak;
    private volatile int iThreadPeak;
    private volatile int iThreadUsage;
    private volatile int iDaemonThreads;
    private volatile int iIncomingTraffic;
    private volatile int iOutgoingTraffic;
    private volatile int iConcurrentSpaces;
    private volatile int iConcurrentPlayers;
    private volatile int iIncomingTrafficPeak;
    private volatile int iOutgoingTrafficPeak;

    public RuntimeController()
    {
        this.tMonitor = new Thread(this);
        this.tMonitor.setPriority(Thread.MIN_PRIORITY);
        this.tMonitor.setName("RUNTIME-MONITOR-THREAD-0");
        this.mBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    private String getUptime()
    {
        // CALCULATE
        long stamp =
        Environment.traceNanoTime() -
        Environment.START_UP_TIME;

        // CALCULATE DATE
        long modul = stamp % 3600000;
        long days = stamp / 86400000;
        long hours = stamp / 3600000;
        long minutes = modul / 60000;

        return  days + " day(s), " + hours + " hour(s) and " + minutes + " minute(s)";
    }

    private void setCPUUsage()
    {
        if (this.iCPUPeak < this.iCPUUsage)
        {
            this.iCPUPeak = this.iCPUUsage;
        }

        this.iCPUUsage = (int) (this.mBean.getProcessCpuLoad() * 10);
    }

    private void setRAMUsage()
    {
        if (this.iRAMPeak < this.iRAMUsage)
        {
            this.iRAMPeak = this.iRAMUsage;
        }

        this.iRAMFree = (int) Runtime.getRuntime().freeMemory() >> 10;
        this.iRAMHeap = (int) Runtime.getRuntime().totalMemory() >> 10;
        this.iRAMUsage = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 10;
    }

    private void setThreadUsage()
    {
        if (this.iThreadPeak < this.iThreadUsage)
        {
            this.iThreadPeak = this.iThreadUsage;
        }

        this.iDaemonThreads = ManagementFactory.getThreadMXBean().getDaemonThreadCount();
        this.iThreadUsage = ManagementFactory.getThreadMXBean().getThreadCount() - this.iDaemonThreads;
    }

    public void setConcurrentSpaces(int amount)
    {
        if (this.iSpacePeak < amount)
        {
            this.iSpacePeak = amount;
        }

        this.iConcurrentSpaces = amount;
    }

    private void setConcurrentPlayers(int amount)
    {
        if (this.iPlayerPeak < amount)
        {
            this.iPlayerPeak = amount;
        }

        this.iConcurrentPlayers = amount;
    }

    private void setIncomingTraffic(long amount)
    {
        if (this.iIncomingTrafficPeak < amount)
        {
            this.iIncomingTrafficPeak = (int) amount;
        }

        this.iIncomingTraffic = (int) amount;
    }

    private void setOutgoingTraffic(long amount)
    {
        if (this.iOutgoingTrafficPeak < amount)
        {
            this.iOutgoingTrafficPeak = (int) amount;
        }

        this.iOutgoingTraffic = (int) amount;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(1000L);
                this.setCPUUsage(); this.setRAMUsage(); this.setThreadUsage();
                this.setConcurrentPlayers(Environment.getCommunication().getSessions().getPlayerAmount());
                this.setIncomingTraffic(Environment.getCommunication().getNetworkProfiler().getIncomingTraffic());
                this.setOutgoingTraffic(Environment.getCommunication().getNetworkProfiler().getOutgoingTraffic());
            }

            catch (Exception ex)
            {
                Environment.getLogger().printOut(LogLevel.CRITICAL, "RuntimeMonitor has thrown an exception!", ex);
            }
        }
    }

    public void stop()
    {
        // TODO...
    }

    public void start()
    {
        this.tMonitor.start();
        Environment.getCommunication().initializeNetworkProfiler();
    }

    public String getStats()
    {
        // INSTANTIATE STRING
        String stats = new String();

        // RUNTIME STATISTICS
        stats += "GC Ratio: " + "0.9%\n"; // TODO...
        stats += "CPU Usage: " + this.iCPUUsage + "%\n";
        stats += "RAM Usage: " + (this.iRAMUsage >> 10) + " MB\n";
        stats += "NET I/O Ratio: " + (this.iIncomingTraffic >> 10) + " KB/s\n";
        stats += "NET O/I Ratio: " + (this.iOutgoingTraffic >> 10) + " KB/s\n";
        stats += "DB CONN. Usage: " + Environment.getDatabaseController().getActiveConnections() + " connection(s)\n\n";

        // OS. INFORMATION
        stats += "Operating System: " + System.getProperty("os.name") + "\n";
        stats += "JVM  Information: " + System.getProperty("java.vm.name") + "\n\n";

        // UPTIME STATISTICS
        stats += "Server uptime is " + this.getUptime() + "\n";
        stats += "Currently there are " + this.iConcurrentPlayers + "/";
        stats += Properties.MAX_PLAYER_AMOUNT + " connections in use and ";
        stats += this.iConcurrentSpaces + "/" + Properties.MAX_SPACES_AMOUNT + " spaces in use.";

        return stats;
    }

    @Override
    public String toString() {
        return
                "<runtime-stats>"							+
                    "<cpu>"									+
                        "<peak>"							+
                            this.iCPUPeak					+
                        "</peak>"							+
                        "<current>"							+
                            this.iCPUUsage					+
                        "</current>"						+
                    "</cpu>"								+
                    "<ram>"									+
                         "<peak>"							+
                            this.iRAMPeak					+
                        "</peak>"							+
                        "<free>" 							+
                            this.iRAMFree					+
                        "</free>"							+
                        "<heap>"							+
                            this.iRAMHeap					+
                        "</heap>"							+
                        "<current>"							+
                            this.iRAMUsage					+
                        "</current>"						+
                    "</ram>"								+
                    "<game>"								+
                        "<Spaces>"							+
                            "<peak>"						+
                                this.iSpacePeak				+
                            "</peak>"						+
                            "<current>"						+
                                this.iConcurrentSpaces		+
                            "</current>"					+
                        "</Spaces>"							+
                        "<players>"							+
                            "<peak>"						+
                                this.iPlayerPeak			+
                            "</peak>"						+
                            "<current>"						+
                                this.iConcurrentPlayers		+
                            "</current>"					+
                        "</players>"						+
                    "</game>"								+
                    "<uptime>"								+
                        Environment.START_UP_TIME	    	+
                    "</uptime>"								+
                    "<traffic>"								+
                        "<incoming>"						+
                            "<peak>"						+
                                this.iIncomingTrafficPeak 	+
                            "</peak>"						+
                            "<current>"						+
                                this.iIncomingTraffic		+
                            "</current>"					+
                        "</incoming>"						+
                        "<outgoing>"						+
                            "<peak>"						+
                                this.iOutgoingTrafficPeak	+
                            "</peak>"						+
                            "<current>"						+
                                this.iOutgoingTraffic		+
                            "</current>"					+
                        "</outgoing>"						+
                    "</traffic>"							+
                    "<threads>"                             +
                        "<peak>"							+
                            this.iThreadPeak				+
                        "</peak>"							+
                        "<daemon>"							+
                            this.iDaemonThreads				+
                        "</daemon>"							+
                        "<current>"							+
                            this.iThreadUsage				+
                        "</current>"						+
                    "</threads>"                            +
                "</runtime-stats>"							;
    }
}
