package pt.neticle.ark.servlet;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServletHttpRequest implements HttpRequest
{
    private final HttpServletRequest underlyingObject;
    private final ContentType contentType;
    private final List<HttpCookie> cookies;
    private final Method method;
    private final String host;
    private final String path;
    private final String queryString;
    private final String uri;

    ServletHttpRequest (HttpServletRequest svReq) throws ParseException
    {
        underlyingObject = svReq;

        contentType = svReq.getContentType() != null ?
            ContentType.parse(svReq.getContentType()) :
            null;

        cookies = Arrays.stream(svReq.getCookies())
            .map((svCookie) -> HttpCookie.parse(svCookie.toString()))
            .flatMap((cookies) -> cookies.stream())
            .collect(Collectors.toList());

        method = Method.valueOf(svReq.getMethod());

        host = svReq.getHeader("Host");

        path = svReq.getPathTranslated();

        queryString = svReq.getQueryString();

        uri = svReq.getRequestURI();
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
        return uri;
    }

    @Override
    public InputStream getBody ()
    {
        try
        {
            return underlyingObject.getInputStream();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
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
        return underlyingObject.getHeader(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return contentType;
    }
}
