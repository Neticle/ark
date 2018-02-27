package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.http.HttpResponse;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Html extends Text<Html>
{
    private Html (OutputStream os, Charset charset)
    {
        super(os, new ContentType(MediaType.Text.HTML, charset));
    }

    private Html (Charset charset)
    {
        super(new ContentType(MediaType.Text.HTML, charset));
    }

    public Html append (String text)
    {
        appendString(text);
        return this;
    }

    public static Html buffered (Charset encoding)
    {
        return new Html(encoding);
    }

    public static Html buffered ()
    {
        return Html.buffered(StandardCharsets.UTF_8);
    }

    public static Html buffered (String initialContent)
    {
        return Html.buffered().append(initialContent);
    }

    public static Html direct (HttpResponse response)
    {
        return new Html(response.contentOutput(), StandardCharsets.UTF_8);
    }
}
