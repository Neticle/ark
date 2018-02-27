package pt.neticle.ark.cli;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.CliApplication;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ArkRuntimeException;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InputException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleDispatchContext extends DispatchContext
{
    public ConsoleDispatchContext (CliApplication parent, String path, String[] args)
    {
        this
        (
            parent,
            path,
            Arrays.stream(args)
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .map(parts -> new DispatchParameter(parts[0], parts[1]))
                .collect(Collectors.toList())
        );
    }

    public ConsoleDispatchContext (CliApplication parent, String path, List<DispatchParameter> parameters)
    {
        super(parent, path, parameters);
    }

    @Override
    public void handleActionOutput (Output output)
    {
        if(!output.hasInternalBuffer())
        {
            return;
        }

        ContentType contentType = output.getContentType();

        if(contentType != null)
        {
            // TODO: Some Application content types are also represented as text, such as application/xml,
            // and application/json. We need to add flags to the media types so we can know which ones
            // can be printed, instead of accepting only text/* types.
            if(!(contentType.getMediaType() instanceof MediaType.Text))
            {
                try
                {
                    // If content isn't text based, don't display it, just display it's type
                    ((CliApplication) this.getParent()).getOutputStream().write(("[" + contentType.toString() + "]").getBytes());
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
                return;
            }
        }

        try
        {
            output.writeTo(((CliApplication) this.getParent()).getOutputStream());
        } catch(IOException e)
        {
            System.err.println("Failed to output to console");
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleHaltedAction (ActionHandler action, Throwable cause)
    {
        if(cause instanceof ArkRuntimeException)
        {
            if(cause instanceof InputException)
            {
                System.err.println("Input error: " + cause.getMessage());

                return true;
            }

            else if(cause instanceof ImplementationException)
            {
                System.err.println("An internal application error occurred");

                if(getEnvironment().inDeveloperMode())
                {
                    cause.printStackTrace();
                }

                return true;
            }

            else if(cause instanceof ExternalConditionException)
            {
                System.err.print("An internal application error ocurred.");

                if(cause.getMessage() != null)
                {
                    System.err.print(" " + cause.getMessage());
                }

                System.err.print('\n');

                if(getEnvironment().inDeveloperMode())
                {
                    cause.printStackTrace();
                }

                return true;
            }
        }

        handleUnknownException(action, cause);
        return true;
    }

    private void handleUnknownException (ActionHandler action, Throwable cause)
    {
        System.err.println("An unexpected internal application error occurred");

        if(getEnvironment().inDeveloperMode())
        {
            cause.printStackTrace();
        }
    }
}
