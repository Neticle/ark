package pt.neticle.ark.data.input;

import pt.neticle.ark.http.HttpDispatchContext;

public abstract class InputBody
{
    public InputBody (HttpDispatchContext context)
    {
        // does nothing for now, but might be useful later as a base class to all inputs that pull
        // from the request body
    }
}
