package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PlainText extends Text<PlainText>
{
    public PlainText (OutputStream os, ContentType contentType)
    {
        super(os, contentType);
    }

    public PlainText (OutputStream os, InputStream is, ContentType contentType, Runnable bufferFlipper)
    {
        super(os, is, contentType, bufferFlipper);
    }

    public PlainText append (String text)
    {
        appendString(text);
        return this;
    }

    public static PlainText buffered (ContentType contentType)
    {
        DynamicByteBuffer b = new DynamicByteBuffer(4096);

        return new PlainText
        (
            new DynamicByteBufferOutputStream(b),
            new DynamicByteBufferInputStream(b),
            contentType,
            b::flip
        );
    }

    public static PlainText buffered ()
    {
        return buffered(new ContentType(MediaType.Text.PLAIN, StandardCharsets.UTF_8));
    }

    public static PlainText buffered (String initialContent)
    {
        return buffered().append(initialContent);
    }

    public static PlainText direct (HttpResponse response)
    {
        return new PlainText(response.contentOutput(), new ContentType(MediaType.Text.PLAIN, StandardCharsets.UTF_8));
    }
}