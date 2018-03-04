package pt.neticle.ark.base;

public interface ReverseRouter extends ApplicationComponent
{
    String pathTo (Class controller, String actionName, String... pathParameters);

    void precompute ();
}
