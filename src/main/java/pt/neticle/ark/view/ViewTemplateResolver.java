package pt.neticle.ark.view;

import pt.neticle.ark.annotations.TemplateObject;
import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.ApplicationComponent;
import pt.neticle.ark.base.DispatchContext;

/**
 * The view template resolver is in charge of determining the correct Template object given a context, action and
 * view data.
 *
 * Typically you'll want to extract the view's specified name (which should match the template name) and the action's
 * parent controller class (so you can check if any of the available templates match that origin).
 */
public interface ViewTemplateResolver extends ApplicationComponent
{
    default void foundTemplateObject (Class<? extends Template> tClass, TemplateObject annotation) {};

    Template resolve (Class<? extends DispatchContext> contextType, ActionHandler origin, View view);
}
