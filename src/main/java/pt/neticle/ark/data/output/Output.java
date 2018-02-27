package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ContentType;

import java.io.*;

public abstract class Output<T>
{
    protected ContentType contentType = null;
    private final OutputStream ostream;
    private final boolean internalBuffer;

    /* internal buffer */
    Output ()
    {
        ostream = new ByteArrayOutputStream(1024*2);
        internalBuffer = true;
    }

    /* externally provided buffer */
    Output (OutputStream os)
    {
        ostream = os;
        internalBuffer = false;
    }

    public ContentType getContentType ()
    {
        return contentType;
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

    public void ready ()
    {
    }
}
