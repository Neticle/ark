package pt.neticle.ark.ssrhtml.elements;

import pt.neticle.ark.ssrhtml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Elements
{
    public static <T> NodeList $each (Iterable<T> items, Function<T, Renderable> fn)
    {
        List<Renderable> list = new ArrayList<>();

        for(T item : items)
        {
            list.add(fn.apply(item));
        }

        return new NodeList(list.toArray(new Renderable[0]));
    }

    public static <T> NodeList $each (Iterable<T> items, BiFunction<T, Integer, Renderable> fn)
    {
        int i = 0;
        List<Renderable> list = new ArrayList<>();

        for(T item : items)
        {
            list.add(fn.apply(item, i));
            i++;
        }

        return new NodeList(list.toArray(new Renderable[0]));
    }

    public static <T> NodeList $each (Stream<T> items, Function<T, Renderable> fn)
    {
        return new NodeList(items.map(fn::apply).toArray(Renderable[]::new));
    }

    public static <T> T $if (boolean cond, Supplier<T> supl)
    {
        return cond ? supl.get() : null;
    }

    public static Element $ (String tag, Renderable... children)
    {
        return new Element(tag, children);
    }

    public static NodeList $ (Renderable... children)
    {
        return new NodeList(children);
    }

    public static NodeList $ ()
    {
        return new NodeList();
    }

    public static Attribute attr (String key, String value)
    {
        return new Attribute(key, value);
    }

    public static Text raw (String text)
    {
        return new Text(text);
    }

    public static Text raw (String first, String... more)
    {
        StringBuilder sb = new StringBuilder(first);

        for(int i = 0; i < more.length; i++)
        {
            sb.append(more[i]);
        }

        return new Text(sb.toString());
    }

    public static Text text (String text)
    {
        return new Text(text);
    }

    public static Text text (String first, String... more)
    {
        StringBuilder sb = new StringBuilder(first);

        for(int i = 0; i < more.length; i++)
        {
            sb.append(more[i]);
        }

        return new Text(sb.toString());
    }

    public static Element html (Renderable... children)
    {
        return new Element("html", children);
    }

    public static Element head (Renderable... children)
    {
        return new Element("head", children);
    }

    public static Element title (String title)
    {
        return new Element("title", text(title));
    }

    public static Element body (Renderable... children)
    {
        return new Element("html", children);
    }

    public static Element link (Renderable... children)
    {
        return new Element("link", Element.merge(new Renderable[]{ attr("rel", "stylesheet") }, children));
    }

    public static class link
    {
        public static Element toStylesheet (String href)
        {
            return new Element("link", attr("rel", "stylesheet"), attr("href", href), attr("type", "text/css"));
        }

        public static Element toStylesheet (String href, Renderable... children)
        {
            return new Element("link", Element.merge(new Renderable[]{ attr("rel", "stylesheet"), attr("href", href), attr("type", "text/css") }, children));
        }
    }

    public static Element style (Renderable... children)
    {
        return new Element("style", children);
    }

    public static Element inlineCssStyle (String css)
    {
        return style(raw(css));
    }

    public static Element meta ()
    {
        return new Element("meta");
    }

    public static Element meta (Renderable... children)
    {
        return new Element("meta", children);
    }

    public static Element div (Renderable... children)
    {
        return new Element("div", children);
    }

    public static Element span (Renderable... children)
    {
        return new Element("span", children);
    }

    public static Element span (String text)
    {
        return new Element("span", text(text));
    }

    public static Element a (String href, Renderable... children)
    {
        Element a = new Element("a", children).merge(attr("href", href));

        return a;
    }

    public static Element a (String href, String text)
    {
        return a(href, text(text));
    }

    public static Element a (Renderable... children)
    {
        return new Element("a", children);
    }

    public static Element address (Renderable... children)
    {
        return new Element("address", children);
    }

    public static Element article (Renderable... children)
    {
        return new Element("article", children);
    }

    public static Element aside (Renderable... children)
    {
        return new Element("aside", children);
    }

    public static Element footer (Renderable... children)
    {
        return new Element("footer", children);
    }

    public static Element header (Renderable... children)
    {
        return new Element("header", children);
    }

    public static Element h (int size, Renderable... children)
    {
        return new Element("h" + size, children);
    }

    public static Element h (int size, String text)
    {
        return h(size, text(text));
    }

    public static Element h1 (Renderable... children)
    {
        return new Element("h1", children);
    }

    public static Element h2 (Renderable... children)
    {
        return new Element("h2", children);
    }

    public static Element h3 (Renderable... children)
    {
        return new Element("h3", children);
    }

    public static Element h4 (Renderable... children)
    {
        return new Element("h4", children);
    }

    public static Element h5 (Renderable... children)
    {
        return new Element("h5", children);
    }

    public static Element h6 (Renderable... children)
    {
        return new Element("h6", children);
    }

    public static Element nav (Renderable... children)
    {
        return new Element("nav", children);
    }

    public static Element section (Renderable... children)
    {
        return new Element("section", children);
    }

    public static Element blockquote (Renderable... children)
    {
        return new Element("blockquote", children);
    }

    public static Element dd (Renderable... children)
    {
        return new Element("dd", children);
    }

    public static Element dl (Renderable... children)
    {
        return new Element("dl", children);
    }

    public static Element dt (Renderable... children)
    {
        return new Element("dt", children);
    }

    public static Element figcaption (Renderable... children)
    {
        return new Element("figcaption", children);
    }

    public static Element figure (Renderable... children)
    {
        return new Element("figure", children);
    }

    public static Element hr ()
    {
        return new Element("hr");
    }

    public static Element ul (Renderable... children)
    {
        return new Element("ul", children);
    }

    public static Element li (Renderable... children)
    {
        return new Element("li", children);
    }

    public static Element li (String text)
    {
        return new Element("li", text(text));
    }

    public static Element main (Renderable... children)
    {
        return new Element("main", children);
    }

    public static Element ol (Renderable... children)
    {
        return new Element("ol", children);
    }

    public static Element p (Renderable... children)
    {
        return new Element("p", children);
    }

    public static Element p (String text)
    {
        return new Element("p", text(text));
    }

    public static Element pre (Renderable... children)
    {
        return new Element("pre", children);
    }

    public static Element pre (String text)
    {
        return new Element("pre", text(text));
    }

    public static Element b (String text)
    {
        return new Element("b", text(text));
    }

    public static Element br ()
    {
        return new Element("br");
    }

    public static Element code (Renderable... children)
    {
        return new Element("code", children);
    }

    public static Element code (String text)
    {
        return new Element("code", text(text));
    }

    public static Element i (String text)
    {
        return new Element("i", text(text));
    }

    public static Element s (String text)
    {
        return new Element("s", text(text));
    }

    public static Element small (Renderable... children)
    {
        return new Element("small", children);
    }

    public static Element small (String text)
    {
        return new Element("small", text(text));
    }

    public static Element strong (String text)
    {
        return new Element("strong", text(text));
    }

    public static Element sub (String text)
    {
        return new Element("sub", text(text));
    }

    public static Element sup (String text)
    {
        return new Element("sup", text(text));
    }

    public static Element wbr ()
    {
        return new Element("wbr");
    }

    public static Element table (Renderable... children)
    {
        return new Element("table", children);
    }

    public static Element thead (Renderable... children)
    {
        return new Element("thead", children);
    }

    public static Element tbody (Renderable... children)
    {
        return new Element("tbody", children);
    }

    public static Element tfoot (Renderable... children)
    {
        return new Element("tfoot", children);
    }

    public static Element tr (Renderable... children)
    {
        return new Element("tr", children);
    }

    public static Element td (Renderable... children)
    {
        return new Element("td", children);
    }

    public static Element th (Renderable... children)
    {
        return new Element("th", children);
    }

    public static Element td (String text)
    {
        return new Element("td", text(text));
    }

    public static Element th (String text)
    {
        return new Element("th", text(text));
    }

    public static Element input (Renderable... children)
    {
        return new Element("input", children);
    }

    public static class input
    {
        public static Element text (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("text")));
        }

        public static Element password (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("password")));
        }

        public static Element hidden (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("hidden")));
        }

        public static Element button (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("button")));
        }

        public static Element checkbox (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("checkbox")));
        }

        public static Element file (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("file")));
        }

        public static Element radio (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("radio")));
        }

        public static Element reset (Renderable... children)
        {
            return new Element("input", Element.merge(children, Attributes.type("reset")));
        }
    }
}
