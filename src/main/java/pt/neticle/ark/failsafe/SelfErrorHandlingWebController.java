package pt.neticle.ark.failsafe;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ArkRuntimeException;
import pt.neticle.ark.http.HttpDispatchContext;

public interface SelfErrorHandlingWebController extends ErrorHandlingController,
        ErrorHandler<HttpDispatchContext>, InternalErrorHandler<HttpDispatchContext>
{
    @Override
    default <T extends DispatchContext> ErrorHandler<T> getErrorHandlerFor (Class<T> contextType)
    {
        return contextType.equals(HttpDispatchContext.class) ? (ErrorHandler<T>)this : null;
    }

    @Override
    default <T extends DispatchContext> InternalErrorHandler<T> getInternalErrorHandlerFor (Class<T> contextType)
    {
        return contextType.equals(HttpDispatchContext.class) ? (InternalErrorHandler<T>)this : null;
    }

    @Override
    default Output<?> handleError (HttpDispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return null;
    }

    @Override
    default Output<?> handleInternalError (HttpDispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return null;
    }
}
