package pt.neticle.ark.view.arktemplating;

import org.xml.sax.SAXException;
import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.ContentOutput;
import pt.neticle.ark.data.output.Html;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.filesystem.ArkFs;
import pt.neticle.ark.templating.TemplatingEngine;
import pt.neticle.ark.templating.renderer.MainScope;
import pt.neticle.ark.templating.structure.ReadableElement;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.Template;
import pt.neticle.ark.view.View;
import pt.neticle.ark.view.arktemplating.functions.RouteFn;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArkTemplatingResolver extends DefaultViewTemplateResolver
{
    private final TemplatingEngine templatingEngine;

    public ArkTemplatingResolver ()
    {
        TemplatingEngine.Initializer init = TemplatingEngine.initializer();

        Path bundledTemplatesFolder = ArkFs.resolveBundled(Paths.get("templates"));
        if(bundledTemplatesFolder != null && Files.exists(bundledTemplatesFolder) && Files.isDirectory(bundledTemplatesFolder))
        {
            init.withSearchDirectory(bundledTemplatesFolder);
        }

        try
        {
            templatingEngine = init.build();
        } catch(ParserConfigurationException | SAXException | IOException e)
        {
            throw new ImplementationException(e);
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
