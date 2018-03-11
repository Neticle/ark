package pt.neticle.ark.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.http.HttpRequest;
import pt.neticle.ark.http.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UndertowExchange implements HttpRequest, HttpResponse
{
    private final HttpServerExchange exchange;
    private final List<HttpCookie> cookies;
    private final ContentType contentType;

    public UndertowExchange (HttpServerExchange exchange) throws ParseException
    {
        this.exchange = exchange;

        cookies = exchange.getRequestHeaders().contains("Cookies") ?
            HttpCookie.parse(exchange.getRequestHeaders().get("Cookies").getFirst()) :
            new ArrayList<>();

        contentType = exchange.getRequestHeaders().contains("Content-Type") ?
            ContentType.parse(exchange.getRequestHeaders().getFirst("Content-Type")) : null;
    }

    /* Http Request */

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
        return exchange.getHostName();
    }

    @Override
    public Method getMethod ()
    {
        return Method.valueOf(exchange.getRequestMethod().toString());
    }

    @Override
    public boolean is (Method method)
    {
        return getMethod() == method;
    }

    @Override
    public String getUri ()
    {
        return exchange.getRequestURI();
    }

    @Override
    public InputStream getBody ()
    {
        return exchange.getInputStream();
    }

    @Override
    public String getPath ()
    {
        return exchange.getRequestPath();
    }

    @Override
    public String getQueryString ()
    {
        return exchange.getQueryString();
    }

    @Override
    public String getHeader (String header)
    {
        return exchange.getRequestHeaders().getFirst(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return contentType;
    }

    /* Http Response */

    @Override
    public int getStatusCode ()
    {
        return exchange.getStatusCode();
    }

    @Override
    public void setStatusCode (int statusCode)
    {
        exchange.setStatusCode(statusCode);
    }

    @Override
    public OutputStream contentOutput ()
    {
        exchange.startBlocking();

        return exchange.getOutputStream();
    }

    @Override
    public void setHeader (String header, String value)
    {
        exchange.getResponseHeaders().put(HttpString.tryFromString(header), value);
    }
}
