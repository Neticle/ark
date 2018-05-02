package pt.neticle.ark.view.arktemplating;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.ContentOutput;
import pt.neticle.ark.data.output.Html;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.filesystem.ArkFs;
import pt.neticle.ark.templating.TemplatingEngine;
import pt.neticle.ark.templating.exception.LoaderException;
import pt.neticle.ark.templating.renderer.MainScope;
import pt.neticle.ark.templating.structure.ReadableElement;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.Template;
import pt.neticle.ark.view.View;
import pt.neticle.ark.view.arktemplating.functions.RouteFn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ArkTemplatingResolver extends DefaultViewTemplateResolver
{
    private static final Logger Log = Logger.getLogger(ArkTemplatingResolver.class.getName());

    private final TemplatingEngine templatingEngine;

    public ArkTemplatingResolver ()
    {
        TemplatingEngine.Initializer init = TemplatingEngine.initializer();

        Path bundledTemplatesFolder = ArkFs.resolveBundled(ArkTemplatingSettings.templatesBasePath.getValue());
        if(bundledTemplatesFolder != null && Files.exists(bundledTemplatesFolder) && Files.isDirectory(bundledTemplatesFolder))
        {
            if(ArkTemplatingSettings.hotReload.getValue())
            {
                Log.info("Enabling ark-templating hot reload feature");

                init.withHotloadErrorHandler((path, e) ->
                {
                    String message = e.getMessage();

                    Throwable t = e.getCause();
                    while(t != null)
                    {
                        message += "\n" + t.getMessage();
                        t = t.getCause();
                    }

                    Log.severe(message);
                });
            }

            init.withSearchDirectory(bundledTemplatesFolder, ArkTemplatingSettings.hotReload.getValue());
        }
        else
        {
            Log.warning("Specified templates base directory doesn't exist or is not a directory. " +
                ((bundledTemplatesFolder != null) ? bundledTemplatesFolder.toString() : ""));
        }

        try
        {
            templatingEngine = init.build();
        } catch(LoaderException e)
        {
            throw new ImplementationException(e);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    @Override
    public void activate ()
    {
        super.activate();

        templatingEngine.getExpressionMatcher().getFunctionCatalog()
            .registerHandler(new RouteFn(context().getReverseRouter()));
    }

    @Override
    public Template resolve (Class<? extends DispatchContext> contextType, ActionHandler origin, View view)
    {
        // template objects take precedence over html templates, so let's attempt to get a matching one
        Template tpl = super.resolve(contextType, origin, view);

        if(tpl == null)
        {
            // no template object found, let's attempt an html template

            ReadableElement tplElement = templatingEngine.getTemplate(view.getName());

            if(tplElement != null)
            {
                tpl = new TemplateWrapper(this, tplElement);
            }
        }

        return tpl;
    }

    public TemplatingEngine getTemplatingEngine ()
    {
        return templatingEngine;
    }

    private static class TemplateWrapper implements Template
    {
        private final ReadableElement templateElement;
        private final ArkTemplatingResolver parent;

        public TemplateWrapper (ArkTemplatingResolver parent, ReadableElement templateElement)
        {
            this.parent = parent;
            this.templateElement = templateElement;
        }

        @Override
        public ContentOutput<?> render (DispatchContext context, ActionHandler origin, View view)
        {
            Html content = Html.buffered();

            content.append("<!DOCTYPE html>\n");

            try
            {
                parent.templatingEngine.render(templateElement, new MainScope(view.getData()), content);
            } catch(Throwable e)
            {
                throw new ImplementationException(e);
            }

            return content;
        }
    }
}
