package pt.neticle.ark.base;

import pt.neticle.ark.cli.ConsoleDispatchContext;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class CliApplication extends Application
{
    private OutputStream outputStream;

    public OutputStream getOutputStream ()
    {
        return outputStream == null ? System.out : outputStream;
    }

    protected final void setOutputStream (OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    public void dispatch (String... arguments)
    {
        dispatchWithPath
        (
            arguments.length > 0 ? arguments[0] : "",
            arguments.length > 1 ? Arrays.copyOfRange(arguments, 1, arguments.length) : new String[0]
        );
    }

    public final void dispatchWithPath (String path, String... arguments)
    {
        dispatch(prepareContext(path, arguments));
    }

    public final void dispatch (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        dispatch(prepareContext(path, parameters));
    }

    protected final ConsoleDispatchContext prepareContext (String path, String... arguments)
    {
        return new ConsoleDispatchContext(context(), path, arguments, getOutputStream());
    }

    protected final ConsoleDispatchContext prepareContext
        (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        return new ConsoleDispatchContext(context(), path, parameters, getOutputStream());
    }
}
