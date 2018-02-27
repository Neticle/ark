package pt.neticle.ark.base;

/**
 * A component owned by the application, that is aware of it's owner.
 */
public interface ApplicationComponent
{
    default void setParentApplication (Application app) {};

    default void activate () {};
}
