package pt.neticle.ark.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import pt.neticle.ark.base.WebApplication;

public class DefaultUndertowServer
{
    private final int inboundPort;
    private final Undertow server;

    public DefaultUndertowServer (int inboundPort, WebApplication app)
    {
        this(inboundPort, new UndertowApplicationHandler(app));
    }

    public DefaultUndertowServer (int inboundPort, HttpHandler handler)
    {
        this.inboundPort = inboundPort;

        server = Undertow.builder()
            .addHttpListener(inboundPort, "0.0.0.0")
            .setHandler(handler)
            .build();
    }

    public void serve ()
    {
        server.start();
    }
}
