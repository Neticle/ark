package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.http.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class Html extends Text<Html>
{
    public Html (OutputStream os, Charset charset)
    {
        super(os, new ContentType(MediaType.Text.HTML, charset));
    }

    public Html (OutputStream os, InputStream is, Charset charset, Runnable bufferFlipper)
    {
        super(os, is, new ContentType(MediaType.Text.HTML, charset), bufferFlipper);
    }

    public Html append (String text)
    {
        appendString(text);
        return this;
    }

    public static Html buffered (Charset encoding)
    {
        DynamicByteBuffer b = new DynamicByteBuffer(4096);

        return new Html
        (
            new DynamicByteBufferOutputStream(b),
            new DynamicByteBufferInputStream(b),
            encoding,
            b::flip
        );
    }

    public static Html buffered ()
    {
        return Html.buffered(ArkDataUtils.UTF_8_CHARSET);
    }

    public static Html buffered (String initialContent)
    {
        return Html.buffered().append(initialContent);
    }

    public static Html direct (HttpResponse response)
    {
        return new Html(response.contentOutput(), ArkDataUtils.UTF_8_CHARSET);
    }
}
