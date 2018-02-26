package pt.neticle.ark.base;

public interface ReverseRouter
{
    String pathTo (Class controller, String actionName, String... pathParameters);
}
