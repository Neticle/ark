package pt.neticle.ark.failsafe.handlers;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.data.output.PlainText;
import pt.neticle.ark.exceptions.ArkRuntimeException;
import pt.neticle.ark.exceptions.InputException;
import pt.neticle.ark.failsafe.ErrorHandler;
import pt.neticle.ark.failsafe.InternalErrorHandler;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.http.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultWebErrorHandler implements ErrorHandler<HttpDispatchContext>, InternalErrorHandler<HttpDispatchContext>
{
    protected void printException (Appendable out, Throwable e) throws IOException
    {
        out.append("Exception: " + e.getClass().getName() + " - " + e.getMessage() + "\n");

        out.append
        (
            Arrays.stream(e.getStackTrace())
                .map(st -> st.toString())
                .collect(Collectors.joining("\n"))
        );

        out.append("\n");

        if(e.getCause() != null)
        {
            out.append("\n---\nCaused by ");
            printException(out, e.getCause());
        }
    }

    protected void setErrorStatusCode (HttpResponse response, ArkRuntimeException exception)
    {
        if(exception instanceof InputException.UnauthorizedAccess)
        {
            response.setStatusCode(HttpResponse.Status.UNAUTHORIZED);
        } else if(exception instanceof InputException.PreconditionFailed)
        {
            response.setStatusCode(HttpResponse.Status.PRECONDITION_FAILED);
        } else if(exception instanceof InputException.PathNotFound ||
                exception instanceof InputException.MalformedData ||
                exception instanceof InputException.MissingParameters)
        {
            response.setStatusCode(HttpResponse.Status.BAD_REQUEST);
        } else if(exception instanceof InputException.RequestedResourceNotFound)
        {
            response.setStatusCode(HttpResponse.Status.NOT_FOUND);
        } else
        {
            response.setStatusCode(HttpResponse.Status.BAD_REQUEST);
        }
    }

    @Override
    public Output<?> handleError (HttpDispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        PlainText out = PlainText.buffered("An error ocurred. " + exception.getClass().getName() + "\n");

        if(exception.getMessage() != null)
        {
            out.append("Message: " + exception.getMessage() + "\n");
        }

        setErrorStatusCode(context.getResponse(), exception);

        return out;
    }

    @Override
    public Output<?> handleInternalError (HttpDispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        PlainText out = PlainText.buffered("An internal error ocurred." + exception.getClass().getName() + "\n");

        if(exception.getMessage() != null)
        {
            out.append("Message: " + exception.getMessage() + "\n");
        }

        context.getResponse().setStatusCode(HttpResponse.Status.INTERNAL_SERVER_ERROR);

        return out;
    }
}
