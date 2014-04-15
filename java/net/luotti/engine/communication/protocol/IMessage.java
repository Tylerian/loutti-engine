package net.luotti.engine.communication.protocol;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class IMessage {

    protected short iOPCode;
    protected ByteBuf mBuffer;

    // region #Methods
    public void destruct()
    {
        // Overridden by
        // implementation class!
    }

    @Override
    public String toString()
    {
        return this.mBuffer.toString(Charset.defaultCharset());
    }
    // endregion

    // region #Accessors
    public short getOPCode()
    {
        return this.iOPCode;
    }

    public ByteBuf getBuffer()
    {
        return this.mBuffer;
    }
    // endregion

    // region #Constructors
    public IMessage(short OPCode, ByteBuf buffer)
    {
        this.iOPCode = OPCode;
        this.mBuffer = buffer;
    }
    // endregion
}