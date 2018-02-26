package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ArkDataUtils;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.exceptions.ExternalConditionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class Text<T> extends Output<T> implements CharsetEncoded
{
    private final OutputStreamWriter writter;

    /* For usage with a provided existing buffer */
    Text (OutputStream os, ContentType contentType)
    {
        super(os);

        this.contentType = contentType;
        writter = new OutputStreamWriter(output(), this.contentType.getCharset());
    }

    /* For usage with a new buffer */
    Text (OutputStream os, InputStream is, ContentType contentType, Runnable bufferFlipper)
    {
        super(os, is, bufferFlipper);

        this.contentType = contentType;
        writter = new OutputStreamWriter(output(), this.contentType.getCharset());

        if(this.contentType.getCharset().name().equals(StandardCharsets.UTF_8.name()))
        {
            try
            {
                output().write(0xef);
                output().write(0xbb);
                output().write(0xbf);
            } catch(IOException e)
            {
                throw new ExternalConditionException(e);
            }
        }
    }

    protected void appendString (String text)
    {
        try
        {
            writter.append(text);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    @Override
    public void ready ()
    {
        try
        {
            writter.flush();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }

        super.ready();
    }

    @Override
    public Charset getCharsetEncoding ()
    {
        return contentType.getCharset();
    }
}
