package pt.neticle.ark.base;

import pt.neticle.ark.annotations.Action;
import pt.neticle.ark.annotations.Controller;
import pt.neticle.ark.annotations.TemplateObject;
import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.DefaultConverter;
import pt.neticle.ark.data.output.ContentOutput;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InputException;
import pt.neticle.ark.http.*;
import pt.neticle.ark.injection.*;
import pt.neticle.ark.introspection.ArkReflectionUtils;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.introspection.ClassFinder;
import pt.neticle.ark.routing.DefaultRouter;
import pt.neticle.ark.runtime.Cast;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.Template;
import pt.neticle.ark.view.View;
import pt.neticle.ark.view.ViewTemplateResolver;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base Ark Application class contains all functionality necessary to run and serve requests/commands
 * but only accepts prepared DispatchContext objects.
 *
 * Subclasses such as {@link pt.neticle.ark.base.CliApplication} and {@link pt.neticle.ark.base.WebApplication}
 * exist and contain the logic necessary to convert console arguments or http requests respectively, into usable
 * dispatch contexts.
 *
 * Other than this, all applicational logic is contained here.
 */
public abstract class Application
{
    private final Context mainContext;
    private final ApplicationContext context;
    private final List<ApplicationComponent> components;

    public Application ()
    {
        this(new DefaultMainContext());
    }

    public Application (PolicyHoldingContext mainContext)
    {
        this.mainContext = mainContext;
        this.context = new ApplicationContext(this.mainContext);
        this.components = new ArrayList<>();

        component(context().getViewTemplateResolver());
        component(context().getRouter());

        initialize();

        ClassFinder classFinder = new ClassFinder(ArkTypeUtils.getPackageName(this.getClass()));
        classFinder.handleClassesAnnotatedWith(Controller.class, this::visitController);
        classFinder.handleClassesAnnotatedWith(TemplateObject.class, this::visitTemplateObject);
        classFinder.scan();

        activate();
    }

    public ApplicationContext context ()
    {
        return context;
    }

    protected void component (ApplicationComponent component)
    {
        components.add(component);
    }

    protected void initialize ()
    {
        components.forEach((c) -> c.initialize(context));
    }

    protected void activate ()
    {
        components.forEach(ApplicationComponent::activate);
    }

    private void visitController (Class<?> controllerClass)
    {
        Object controller;

        if(Application.class.isAssignableFrom(controllerClass))
        {
            if(controllerClass == this.getClass())
            {
                controller = this;
            }
            else
            {
                throw new ImplementationException("Only one implementation of the application class must exist within this package tree");
            }
        }
        else
        {
            controller = ArkReflectionUtils.createInstanceNoArgs(controllerClass)
                .orElseThrow(() -> new ImplementationException("Controller must have accessible default constructor"));
        }

        ControllerHandler controllerHandler =
            new ControllerHandler(this, controllerClass, controller, controllerClass.getAnnotation(Controller.class));

        Arrays.stream((controllerClass).getMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()) && method.getAnnotation(Action.class) != null)
            .map(method -> new ActionHandler(controllerHandler, method))
            .forEach(this::registerAction);
    }

    private void visitTemplateObject (Class<?> tClass)
    {
        if(!Template.class.isAssignableFrom(tClass))
        {
            return;
        }

        context().getViewTemplateResolver().foundTemplateObject((Class<? extends Template>) tClass, tClass.getAnnotation(TemplateObject.class));
    }

    private void registerAction (ActionHandler actionHandler)
    {
        context().getRouter().register(actionHandler);
    }

    protected final void dispatch (DispatchContext context)
    {
        ActionHandler action = context().getRouter().route(context);

        if(action == null)
        {
            context.handleHaltedAction(null, new InputException.PathNotFound());
            return;
        }

        Output<?> output = null;

        try {
            output = action.dispatch(context);
        } catch(Throwable e)
        {
            if(!context.handleHaltedAction(action, e))
            {
                new ImplementationException("Unhandled expection reached application dispatcher", e)
                .printStackTrace();
                return;
            }
        }

        if(action.hasOutputReturnType() && output != null)
        {
            output.ready();

            if(output instanceof View)
            {
                Template tmpl = context().getViewTemplateResolver().resolve(context.getClass(), action, (View) output);

                if(tmpl == null)
                {
                    ImplementationException e = new ImplementationException("Couldn't resolve view template for " +
                        action.getControllerHandler().getControllerClass().getName() + "/" + action.getMethodName());

                    if(!context.handleHaltedAction(action, e))
                    {
                        new ImplementationException("Unhandled expection reached application dispatcher", e)
                                .printStackTrace();
                    }

                    return;
                }

                output = tmpl.render(context, action, (View) output);
                output.ready();
            }

            context.handleActionOutput(output);
        }
    }

    protected final void dispatchWithoutErrorHandling (DispatchContext context) throws Throwable
    {
        ActionHandler action = context().getRouter().route(context);

        if(action == null)
        {
            throw new InputException.PathNotFound();
        }

        Output<?> output = action.dispatch(context);

        if(action.hasOutputReturnType() && output != null)
        {
            output.ready();

            if(output instanceof View)
            {
                Template tmpl = context().getViewTemplateResolver().resolve(context.getClass(), action, (View)output);

                if(tmpl == null)
                {
                    throw new ImplementationException("Couldn't resolve view template for " +
                        action.getControllerHandler().getControllerClass().getName() + "/" + action.getMethodName());
                }

                output = tmpl.render(context, action, (View)output);
            }

            context.handleActionOutput(output);
        }
    }
}
