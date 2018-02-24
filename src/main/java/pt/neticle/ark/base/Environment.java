package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * An environment holds a list of policies pertaining to the injection of objects during run-time. It also provides a
 * method to perform such injections.
 *
 * Other than that, the environment serves as a central place to hold various utilitary objects that can be shared
 * across contexts. It is important that routines accessed through here are thread-safe.
 */
public interface Environment
{
    Optional<InjectionPolicy> getPolicyFor (Class desiredType);
    <TDesired> Optional<TDesired> inject (Class<TDesired> desiredType, Context context,
                                          Parameter parameter, ArkTypeUtils.ParameterType parameterType);

    /**
     * Gets the data converter used for treating data to and from application clients.
     * @return
     */
    Converter getIOConverter ();

    default boolean inDeveloperMode ()
    {
        // TODO: This needs to be pulled from some sort of configuration utility
        return true;
    }
}
