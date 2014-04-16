package net.luotti.engine.communication.sessions;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

import com.luotti.engine.Environment;
import com.luotti.engine.avatars.IAvatar;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.utilities.memory.IDisposable;

import net.luotti.engine.communication.ChannelGroup;
import net.luotti.engine.communication.protocol.MessageResponse;

public class SessionController implements Runnable, IDisposable {

    private List<Session> lJunk;
    private AtomicInteger iPlayers;
    private ScheduledFuture mScheduler;
    private Map<Long, Session> mResolver;
    private Map<Channel, Session> mSessions;

    public static ChannelGroup CHANNELS;
    private static final long SESSION_TIMEOUT = 0x7530l;

    static {
        SessionController.CHANNELS =  new ChannelGroup().construct(
            SessionController.class.getCanonicalName()
        );
    }

    // region #Accessors

    public short getPlayerAmount()
    {
        return this.iPlayers.shortValue();
    }

    public boolean isOnline(long ID)
    {
        return this.mResolver.containsKey(ID);
    }

    public Session getSession(long ID)
    {
        return this.mResolver.get(ID);
    }

    private Session getSession(Channel channel)
    {
        return this.mSessions.get(channel);
    }

    // endregion

    // region #Methods
    private void purge()
    {
        // Recollect all timed out sessions
        for (Session session : this.mSessions.values())
        {
            if ((Environment.traceMilliTime() - session.getLastReadTime()) > SESSION_TIMEOUT)
            {
                this.lJunk.add(session);
            }
        }

        // Iterate through all dead sessions
        int amount = 0; for (Session session : this.lJunk)
        {
            session.getChannel().close(); amount++;
        }

        // clear junk list
        this.lJunk.clear();

        // Print out the amount of disposed sessions
        Environment.getLogger().printOut(LogLevel.DEBUG, "SessionController disconnected " + amount + " timed out sessions successfully.");
    }

    @Override
    public void run()
    {
        this.purge();
    }

    @Override
    public void destruct()
    {
        // Release pointers
        this.lJunk.clear();
        this.mResolver.clear();
        this.mSessions.clear();
        this.mScheduler.cancel(true);

        // Point to null
        this.lJunk = null;
        this.iPlayers = null;
        this.mResolver = null;
        this.mSessions = null;
        this.mScheduler = null;
    }

    public void markOnline(Session session)
    {
        IAvatar avatar =
        session.getAvatar();
        if (avatar == null) return;

        if (this.isOnline(avatar.getID())) {
            Session clone =
            this.getSession(avatar.getID());
            clone.getChannel().disconnect();
        }

        session.fireOnlineTriggers();
        this.iPlayers.incrementAndGet();
        this.mResolver.put(avatar.getID(), session);
    }

    public void markOffline(Session session)
    {
        IAvatar avatar =
        session.getAvatar();
        if (avatar == null) return;

        session.fireOfflineTriggers();
        this.iPlayers.decrementAndGet();
        this.mResolver.remove(avatar.getID());
    }

    public Session addConnection(Channel channel)
    {
        Session session =
        new Session().construct(channel);
        this.mSessions.put(channel, session);
        Environment.getLogger().printOut(LogLevel.DEBUG, "Accepted " +
        "connection #" + session.getID() + " from [" + session.getIPAddress() + "]");

        return session;
    }

    public void removeConnection(Channel channel)
    {
        Session session =
        this.getSession(channel);
        this.markOffline(session);
        this.mSessions.remove(channel);

        Environment.getLogger().printOut(LogLevel.DEBUG, "Removed " +
        "connection #" + session.getID() + " from [" + session.getIPAddress() + "]");
    }

    public void broadcast(MessageResponse response) {
        SessionController.CHANNELS.writeAndFlush(response.getPayload());
    }

    // endregion

    // region #Constructors
    public SessionController()
    {
        this.lJunk = new ArrayList<>();
        this.iPlayers = new AtomicInteger();
        this.mResolver = new ConcurrentHashMap<>();
        this.mSessions = new ConcurrentHashMap<>();
        this.mScheduler = Environment.getThreadController().scheduleAtFixedRate(
            this, SessionController.SESSION_TIMEOUT, SessionController.SESSION_TIMEOUT
        );
    }
    // endregion

}
