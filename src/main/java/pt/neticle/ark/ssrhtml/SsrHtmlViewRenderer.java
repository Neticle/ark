package pt.neticle.ark.ssrhtml;

import pt.neticle.ark.data.output.Html;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.presentation.ViewRenderer;

import java.io.IOException;

public abstract class SsrHtmlViewRenderer implements ViewRenderer<Html>
{
    protected abstract Renderable assemble ();

    @Override
    public final Html render ()
    {
        Html out = Html.buffered();

        try
        {
            assemble().render(out);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }

        return out;
    }
}
