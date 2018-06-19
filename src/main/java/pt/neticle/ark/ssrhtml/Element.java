package pt.neticle.ark.ssrhtml;

import java.io.IOException;

public class Element<T extends Element<T>> implements Renderable
{
    private final String name;
    private final Renderable[] children;

    public Element (String name)
    {
        this(name, new Renderable[0]);
    }

    public Element (String name, Renderable... children)
    {
        this.name = name;
        this.children = children;
    }

    public Element merge (Renderable... additional)
    {
        return new Element(this.name, merge(this.children, additional));
    }

    public static Renderable[] merge (Renderable[] initial, Renderable... additional)
    {
        Renderable[] children = new Renderable[initial.length + additional.length];

        System.arraycopy(initial, 0, children, 0, initial.length);
        System.arraycopy(additional, 0, children, initial.length, additional.length);

        return children;
    }

    @Override
    public final void render (Appendable out) throws IOException
    {
        out.append("<");
        out.append(this.name);

        for(Renderable c : children)
        {
            if(c instanceof Attribute)
            {
                out.append(" ");
                c.render(out);
            }
        }

        out.append(">");

        for(Renderable c : children)
        {
            if(!(c instanceof Attribute) && c != null)
            {
                c.render(out);
            }
        }

        out.append("</");
        out.append(this.name);
        out.append(">");
    }
}
