package pt.neticle.ark.data.structured.builder;

import pt.neticle.ark.data.structured.Node;

public class LiteralObject<TVal> implements Node.Literal<TVal>
{
    private final TVal value;

    LiteralObject (TVal _value)
    {
        value = _value;
    }

    @Override
    public TVal get ()
    {
        return value;
    }
}
