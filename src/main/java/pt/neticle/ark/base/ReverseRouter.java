package pt.neticle.ark.base;

import java.net.URI;

public interface ReverseRouter
{
    String pathTo (Class controller, String actionName);

    default URI uriTo (Class controller, String actionName)
    {
        return URI.create(pathTo(controller, actionName));
    }
}
