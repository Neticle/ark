package pt.neticle.ark.ssrhtml;

import java.io.IOException;

public class Attribute implements Renderable
{
    private final String self;

    public Attribute (String name, String value)
    {
        self = name + "=\"" + value + "\"";
    }

    public Attribute (String name)
    {
        self = name;
    }

    @Override
    public void render (Appendable out) throws IOException
    {
        out.append(self);
    }
}
