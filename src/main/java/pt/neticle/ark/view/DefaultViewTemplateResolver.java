package pt.neticle.ark.view;

import com.google.common.collect.HashBasedTable;
import pt.neticle.ark.annotations.TemplateObject;
import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.Application;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkReflectionUtils;

/**
 * The default resolver is only aware of template objects defined within the source code and annotated with
 * the @TemplateObject.
 *
 * You can extend this class to implement your own view resolving logic to allow for view templates stored as
 * text files and possibly parsed/rendered by a template engine of your choosing.
 */
public class DefaultViewTemplateResolver implements ViewTemplateResolver
{
    private Application parent;
    private boolean active;
    private final HashBasedTable<Class<?>, String, Template> templates;

    public DefaultViewTemplateResolver ()
    {
        templates = HashBasedTable.create();
    }

    @Override
    public void foundTemplateObject (Class<? extends Template> tClass, TemplateObject annotation)
    {
        Template tmpl = ArkReflectionUtils.createInstanceNoArgs(tClass)
            .orElseThrow(() -> new ImplementationException("Template must have accessible default constructor"));

        templates.put(annotation.matchOrigin(), annotation.name(), tmpl);
    }

    @Override
    public Template resolve (Class<? extends DispatchContext> contextType, ActionHandler origin, View view)
    {
        Template tmpl = null;
        Class<?> controllerClass = origin.getControllerHandler().getControllerClass();

        while(tmpl == null && controllerClass.getSuperclass() != null)
        {
            tmpl = templates.get(controllerClass, view.getName());

            controllerClass = controllerClass.getSuperclass();
        }

        return tmpl;
    }

    @Override
    public void setParentApplication (Application app)
    {
        parent = app;
    }

    @Override
    public void activate ()
    {
        active = true;
    }
}
