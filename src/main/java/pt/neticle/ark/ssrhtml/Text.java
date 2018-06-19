package pt.neticle.ark.ssrhtml;

import java.io.IOException;

public class Text implements Renderable
{
    private final String text;

    public Text (String text)
    {
        this.text = text;
    }

    @Override
    public final void render (Appendable out) throws IOException
    {
        out.append(text);
    }

    public static Text text (String text)
    {
        return new Text(text);
    }
}
