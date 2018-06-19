package pt.neticle.ark.ssrhtml;

import java.io.IOException;

public class NodeList implements Renderable
{
    private final Renderable[] list;

    public NodeList (Renderable... children)
    {
        this.list = children;
    }

    @Override
    public void render (Appendable out) throws IOException
    {
        for(Renderable c : list)
        {
            c.render(out);
        }
    }
}
