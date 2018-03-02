package pt.neticle.ark.data.structured.builder;

import pt.neticle.ark.data.structured.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AssociativeObject implements Node.Associative
{
    private final Map<String, Node> nodes;

    AssociativeObject ()
    {
        nodes = new HashMap<>();
    }

    public <TLiteralValue> AssociativeObject with (String key, Node.Literal<TLiteralValue> literal)
    {
        return with(key, literal, null);
    }

    public <TLiteralValue> AssociativeObject with (String key, TLiteralValue lVal)
    {
        return with(key, new LiteralObject<>(lVal), null);
    }

    public AssociativeObject withObject (String key, Consumer<AssociativeObject> initializer)
    {
        return with(key, new AssociativeObject(), initializer);
    }

    public AssociativeObject withList (String key, Consumer<ListObject> initializer)
    {
        return with(key, new ListObject(), initializer);
    }

    private <TChildNode extends Node> AssociativeObject with
        (String key, TChildNode child, Consumer<TChildNode> initializer)
    {
        nodes.put(key, child);

        if(initializer != null)
        {
            initializer.accept(child);
        }

        return this;
    }

    @Override
    public int count ()
    {
        return nodes.size();
    }

    @Override
    public Node get (String key)
    {
        return nodes.get(key);
    }

    @Override
    public Set<Map.Entry<String, Node>> entrySet ()
    {
        return nodes.entrySet();
    }

    @Override
    public Stream<Map.Entry<String, Node>> streamEntries ()
    {
        return nodes.entrySet().stream();
    }

    @Override
    public Stream<String> keys ()
    {
        return nodes.keySet().stream();
    }

    @Override
    public Stream<Node> values ()
    {
        return nodes.values().stream();
    }
}
