package pt.neticle.ark.servlet;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.http.HttpRequest;
import pt.neticle.ark.http.HttpResponse;

import javax.xml.ws.spi.http.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class HttpHandlerExchange implements HttpRequest, HttpResponse
{
    private final HttpExchange underlyingObject;
    private final List<HttpCookie> cookies;
    private final Method method;
    private final ContentType contentType;

    public HttpHandlerExchange (HttpExchange exchange) throws ParseException
    {
        underlyingObject = exchange;

        cookies = exchange.getRequestHeader("Cookies") != null ?
            HttpCookie.parse(exchange.getRequestHeader("Cookies")) :
            new ArrayList<>();

        method = Method.valueOf(exchange.getRequestMethod());

        contentType = exchange.getRequestHeader("Content-Type") != null ?
            ContentType.parse(exchange.getRequestHeader("Content-Type")) : null;
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
        return underlyingObject.getRequestHeader("Host");
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
        return underlyingObject.getRequestURI();
    }

    @Override
    public InputStream getBody ()
    {
        try
        {
            return underlyingObject.getRequestBody();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    @Override
    public String getPath ()
    {
        return underlyingObject.getPathInfo();
    }

    @Override
    public String getQueryString ()
    {
        return underlyingObject.getQueryString();
    }

    @Override
    public String getHeader (String header)
    {
        return underlyingObject.getRequestHeader(header);
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
        return 0;
    }

    @Override
    public void setStatusCode (int statusCode)
    {
        underlyingObject.setStatus(statusCode);
    }

    @Override
    public OutputStream contentOutput ()
    {
        try
        {
            return underlyingObject.getResponseBody();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    @Override
    public void setHeader (String header, String value)
    {
        underlyingObject.addResponseHeader(header, value);
    }
}
