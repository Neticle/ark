package pt.neticle.ark.failsafe;

import pt.neticle.ark.base.DispatchContext;

public interface ErrorHandlingController
{
    <T extends DispatchContext> ErrorHandler<T> getErrorHandlerFor (Class<T> contextType);

    <T extends DispatchContext> InternalErrorHandler<T> getInternalErrorHandlerFor (Class<T> contextType);
}
