package net.luotti.engine.communication.protocol;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.luotti.engine.communication.sessions.Session;

public class MessageRequest extends IMessage {

    private Session mSession;

    // region #Methods
    @Override
    public void destruct()
    {
        this.mBuffer.release();

        this.iOPCode = 0;
        this.mBuffer = null;
        this.mSession = null;
    }

    public int readInt()
    {
        return this.mBuffer.readInt();
    }

    public String readUTF()
    {
        return
        this.mBuffer.readBytes
        (this.readShort()).toString
        (Charset.defaultCharset());
    }

    public short readShort()
    {
        return this.mBuffer.readShort();
    }

    public boolean readBoolean()
    {
        return (this.mBuffer.readByte() == 1);
    }

    public boolean  isReadable()
    {
        return this.mBuffer.isReadable();
    }
    // endregion

    // region #Accessors
    public short getOPCode()
    {
        return this.iOPCode;
    }

    public Session getSession( )
    {
        return this.mSession;
    }

    public void setSession(Session session)
    {
        this.mSession = session;
    }
    // endregion

    // region #Constructors
    public MessageRequest(ByteBuf buffer)
    {
        super(buffer.readShort(), buffer);
    }
    // endregion
}