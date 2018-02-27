package pt.neticle.ark.data.output;

import java.io.*;

public abstract class BufferedOutput<T> implements ContentOutput<T>
{
    private final OutputStream ostream;
    private final boolean internalBuffer;

    /* internal buffer */
    protected BufferedOutput ()
    {
        ostream = new ByteArrayOutputStream(1024*2);
        internalBuffer = true;
    }

    /* externally provided buffer */
    protected BufferedOutput (OutputStream os)
    {
        ostream = os;
        internalBuffer = false;
    }

    protected final OutputStream output ()
    {
        return ostream;
    }

    public final void writeTo (OutputStream out) throws IOException
    {
        if(internalBuffer && ostream instanceof ByteArrayOutputStream)
        {
            ((ByteArrayOutputStream) ostream).writeTo(out);
        }
    }

    public boolean hasInternalBuffer ()
    {
        return internalBuffer;
    }
}
