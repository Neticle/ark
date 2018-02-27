package pt.neticle.ark.http;

import com.google.common.io.ByteStreams;
import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.Application;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.ArkDataUtils;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.data.output.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A context that holds http request and response objects.
 *
 * The request's query string is used to populate the dispatch context parameters.
 */
public class HttpDispatchContext extends DispatchContext
{
    private final HttpRequest request;
    private final HttpResponse response;

    public HttpDispatchContext (Application parent, HttpRequest request, HttpResponse response)
    {
        super
        (
            parent,
            request.getPath(),

            Arrays.stream(request.getQueryString().split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .map(parts -> new DispatchParameter(parts[0], ArkDataUtils.decodeUrl(parts[1])))
                .collect(Collectors.toList())
        );

        this.request = request;
        this.response = response;
    }

    public HttpRequest getRequest ()
    {
        return request;
    }

    public HttpResponse getResponse ()
    {
        return response;
    }

    @Override
    public void handleActionOutput (Output output)
    {
        if(output instanceof ContentOutput)
        {
            ((ContentOutput<?>) output).contentType()
                .ifPresent((contentType) -> response.setHeader("Content-Type", contentType.toString()));
        }

        if(output instanceof BufferedOutput &&
            ((BufferedOutput<?>) output).hasInternalBuffer())
        {
            try
            {
                ((BufferedOutput<?>) output).writeTo(response.contentOutput());
            } catch(IOException e)
            {
                response.setStatusCode(HttpResponse.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public boolean handleHaltedAction (ActionHandler action, Throwable cause)
    {
        // TODO
        printException(response, cause);
        return true;
    }

    private final void printException (HttpResponse response, Throwable e)
    {
        response.writeString("Exception: " + e.getClass().getName() + " - " + e.getMessage() + "\n");
        response.writeString
        (
            Arrays.stream(e.getStackTrace())
                .map(st -> st.toString())
                .collect(Collectors.joining("\n"))
        );

        response.writeString("\n");

        if(e.getCause() != null)
        {
            response.writeString("\nCaused by ");
            printException(response, e.getCause());
        }
    }
}
