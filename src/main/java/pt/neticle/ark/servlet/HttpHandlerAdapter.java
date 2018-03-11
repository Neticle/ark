package pt.neticle.ark.servlet;

import pt.neticle.ark.base.WebApplication;
import pt.neticle.ark.exceptions.ExternalConditionException;

import javax.xml.ws.spi.http.HttpExchange;
import javax.xml.ws.spi.http.HttpHandler;
import java.io.IOException;
import java.text.ParseException;

public class HttpHandlerAdapter extends HttpHandler
{
    private final WebApplication application;

    public HttpHandlerAdapter (WebApplication application)
    {
        this.application = application;
    }

    @Override
    public void handle (HttpExchange exchange) throws IOException
    {
        HttpHandlerExchange wrapper;

        try
        {
            wrapper = new HttpHandlerExchange(exchange);
        } catch(ParseException e)
        {
            throw new ExternalConditionException(e);
        }

        application.dispatch(wrapper, wrapper);
    }
}
