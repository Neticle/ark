package pt.neticle.ark.base;

import pt.neticle.ark.cli.ConsoleDispatchContext;
import pt.neticle.ark.data.Converter;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class CliApplication extends Application
{
    private OutputStream outputStream;

    public CliApplication ()
    {
        super();
    }

    public CliApplication (TwoWayRouter _router, Converter ioConverter)
    {
        super(_router, ioConverter);
    }

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
        dispatch
        (
            arguments.length > 0 ? arguments[0] : "",
            arguments.length > 1 ? Arrays.copyOfRange(arguments, 1, arguments.length) : new String[0]
        );
    }

    public final void dispatch (String path, String... arguments)
    {
        dispatch(prepareContext(path, arguments));
    }

    public final void dispatch (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        dispatch(prepareContext(path, parameters));
    }

    protected final ConsoleDispatchContext prepareContext (String path, String... arguments)
    {
        return new ConsoleDispatchContext(this, path, arguments);
    }

    protected final ConsoleDispatchContext prepareContext
        (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        return new ConsoleDispatchContext(this, path, parameters);
    }
}
