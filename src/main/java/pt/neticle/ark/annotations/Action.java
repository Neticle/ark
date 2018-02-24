package pt.neticle.ark.annotations;

import java.lang.annotation.*;

/**
 * Denotes that the method annotated is an action and thus is available to be requested by clients.
 *
 * It is required that the owning class is also annotated with {@link pt.neticle.ark.annotations.Controller}, otherwise
 * the action won't get picked up.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action
{
}
