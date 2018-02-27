package pt.neticle.ark.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TemplateObject
{
    /**
     * The template only applies to views originating from controllers that inherit or are of the class specified here.
     * @return
     */
    Class matchOrigin() default Object.class;

    /**
     * Name of the template, used to match views to templates.
     * @return
     */
    String name();
}
