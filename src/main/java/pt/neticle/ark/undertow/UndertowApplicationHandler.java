package pt.neticle.ark.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import pt.neticle.ark.base.WebApplication;
import pt.neticle.ark.exceptions.ExternalConditionException;

import java.text.ParseException;

public class UndertowApplicationHandler implements HttpHandler
{
    private final WebApplication application;

    public UndertowApplicationHandler (WebApplication application)
    {
        this.application = application;
    }

    @Override
    public void handleRequest (HttpServerExchange exchange) throws Exception
    {
        if(exchange.isInIoThread())
        {
            exchange.dispatch(this);
            return;
        }

        UndertowExchange wrapper;

        try
        {
            wrapper = new UndertowExchange(exchange);
        } catch(ParseException e)
        {
            throw new ExternalConditionException(e);
        }

        application.dispatch(wrapper, wrapper);
    }
}
