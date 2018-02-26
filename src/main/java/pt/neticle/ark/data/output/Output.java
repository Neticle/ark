package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ContentType;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Output<T>
{
    protected ContentType contentType = null;
    private final OutputStream ostream;
    private final InputStream istream;
    private final Runnable bufferFlipper;

    Output (OutputStream os, InputStream is, Runnable bufferFlipper)
    {
        ostream = os;
        istream = is;
        this.bufferFlipper = bufferFlipper;
    }

    Output (OutputStream os)
    {
        ostream = os;
        istream = null;
        this.bufferFlipper = () -> {};
    }

    public ContentType getContentType ()
    {
        return contentType;
    }

    public boolean hasInputStream ()
    {
        return istream != null;
    }

    protected final OutputStream output ()
    {
        return ostream;
    }

    public final InputStream inputStream ()
    {
        return istream;
    }

    public void ready ()
    {
        this.bufferFlipper.run();
    }
}
