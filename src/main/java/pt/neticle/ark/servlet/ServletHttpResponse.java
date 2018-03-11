package pt.neticle.ark.servlet;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ServletHttpResponse implements HttpResponse
{
    private final HttpServletResponse underlyingObject;

    ServletHttpResponse (HttpServletResponse svRes)
    {
        underlyingObject = svRes;
    }

    @Override
    public int getStatusCode ()
    {
        return underlyingObject.getStatus();
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
            return underlyingObject.getOutputStream();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    @Override
    public void setHeader (String header, String value)
    {
        underlyingObject.setHeader(header, value);
    }

    @Override
    public String getHeader (String header)
    {
        return underlyingObject.getHeader(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return null;
    }
}
