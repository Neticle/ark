package pt.neticle.ark.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class ArkReflectionUtils
{
    /**
     * Creates a new object instance of the provided class, if the class has an accessible default constructor
     * (public, with no arguments).
     *
     * @param classType The object class to instantiate
     * @param <T> The object type
     * @return Optional containing the instance, or empty if the class has no suitable constructor.
     */
    public static <T> Optional<T> createInstanceNoArgs (Class<T> classType)
    {
        try
        {
            Constructor<T> constr = classType.getConstructor();

            return Modifier.isPublic(constr.getModifiers()) ? Optional.of(constr.newInstance()) : Optional.empty();
        } catch(NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
        {
            return Optional.empty();
        }
    }
}
