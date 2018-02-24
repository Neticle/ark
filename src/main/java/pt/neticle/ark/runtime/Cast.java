package pt.neticle.ark.runtime;

import java.util.Optional;

public class Cast
{
    /**
     * Attempts to cast provided object to provided class
     *
     * @param desired
     * @param o
     * @param <TDesired>
     * @return
     */
    public static <TDesired> Optional<TDesired> attempt (Class<TDesired> desired, Object o)
    {
        if(desired.isAssignableFrom(o.getClass()))
        {
            return Optional.of((TDesired) o);
        }

        return Optional.empty();
    }
}
