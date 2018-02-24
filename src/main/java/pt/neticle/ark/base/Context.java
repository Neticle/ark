package pt.neticle.ark.base;

import java.util.HashMap;
import java.util.Map;

/**
 * The context serves the purpose of encapsulating various usages of the application.
 *
 * Derivatives of this class exist, this being the base and the most simplistic one.
 *
 * The context may be used to support background tasks. With it being the central object that
 * keeps track of resources and object instances and injections.
 *
 * For tasks other than background work that require more functionality and/or input/output
 * between a client and server, subclasses such as {@link pt.neticle.ark.base.DispatchContext}
 * and {@link pt.neticle.ark.http.HttpDispatchContext} exist.
 */
public class Context
{
    /**
     * The application that created this context
     */
    private final Application parent;

    /**
     * Whenever an object is injected, if it's injection policy states that there should be only one instance
     * of it per context, that instance will be stored here for future injections.
     */
    private final Map<Class, Object> savedInstances;

    /**
     * Creates a new context, given the creator application.
     *
     * @param parent The parent/creator application
     */
    public Context (Application parent)
    {
        this.parent = parent;
        this.savedInstances = new HashMap<>();
    }

    /**
     * Gets the application instance that originated this context
     *
     * @return The parent application
     */
    public Application getParent ()
    {
        return parent;
    }

    /**
     * Get a saved instance of the specified type.
     *
     * @param c The class of the instance to retrieve
     * @return The saved instance, if present, null otherwise
     */
    protected Object getSavedInstance(Class c)
    {
        return savedInstances.get(c);
    }

    /**
     * Saves an object of the given type for future use.
     *
     * @param c The class of the object to save
     * @param o The object to save
     */
    protected void saveInstance (Class c, Object o)
    {
        savedInstances.put(c, o);
    }

    /**
     * Get the current applicational environment
     *
     * @return The current applicational environment
     */
    public Environment getEnvironment ()
    {
        return this.parent.getEnvironment();
    }
}
