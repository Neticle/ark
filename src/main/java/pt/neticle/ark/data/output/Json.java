package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.data.structured.Node;
import pt.neticle.ark.data.structured.json.JsonSerializer;
import pt.neticle.ark.http.HttpResponse;

import java.io.OutputStream;
import java.nio.charset.Charset;

public class Json extends Text<Json>
{
    public Json (OutputStream os)
    {
        super(os, new ContentType(MediaType.Application.JSON, Charset.defaultCharset()));
    }

    public Json ()
    {
        super(new ContentType(MediaType.Application.JSON, Charset.defaultCharset()));
    }

    public static Json buffered ()
    {
        return new Json();
    }

    public static Json buffered (String content)
    {
        Json o = buffered();
        o.appendString(content);
        return o;
    }

    public static Json buffered (Node sdNode)
    {
        return buffered(new JsonSerializer().serialize(sdNode));
    }

    public static Json direct (HttpResponse response)
    {
        return new Json(response.contentOutput());
    }
}
