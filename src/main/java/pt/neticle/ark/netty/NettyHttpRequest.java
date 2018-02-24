package pt.neticle.ark.netty;

import io.netty.handler.codec.http.*;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.http.HttpRequest;

import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NettyHttpRequest implements HttpRequest
{
    private final FullHttpRequest underlyingObject;
    private final ContentType contentType;
    private final List<HttpCookie> cookies;
    private final Method method;
    private final String host;
    private final String path;
    private final String queryString;

    public NettyHttpRequest (FullHttpRequest nettyRequest) throws ParseException
    {
        underlyingObject = nettyRequest;

        contentType = underlyingObject.headers().contains("Content-Type") ?
            ContentType.parse(underlyingObject.headers().get("Content-Type")) : null;

        cookies = underlyingObject.headers().contains("Cookies") ?
            HttpCookie.parse(underlyingObject.headers().get("Cookies")) : new ArrayList<>();

        method = Method.valueOf(underlyingObject.method().name());

        host = underlyingObject.headers().get("Host");

        String uri = underlyingObject.uri();
        int qsMarker = uri.indexOf('?');

        if(qsMarker == -1)
        {
            path = uri;
            queryString = "";
        }
        else
        {
            path = uri.substring(0, qsMarker);
            queryString = uri.substring(qsMarker+1);
        }
    }


    @Override
    public List<HttpCookie> getCookies ()
    {
        return cookies;
    }

    @Override
    public Stream<HttpCookie> cookies ()
    {
        return cookies.stream();
    }

    @Override
    public String getHost ()
    {
        return host;
    }

    @Override
    public Method getMethod ()
    {
        return method;
    }

    @Override
    public boolean is (Method method)
    {
        return this.method == method;
    }

    @Override
    public String getUri ()
    {
        return underlyingObject.uri();
    }

    @Override
    public ByteBuffer getBody ()
    {
        return underlyingObject.content().nioBuffer();
    }

    @Override
    public String getPath ()
    {
        return path;
    }

    @Override
    public String getQueryString ()
    {
        return queryString;
    }

    @Override
    public String getHeader (String header)
    {
        return underlyingObject.headers().get(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return contentType;
    }
}
