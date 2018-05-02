package pt.neticle.ark.undertow;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import org.webjars.WebJarAssetLocator;

import java.io.IOException;
import java.util.logging.Logger;

public class WebjarResourceManager implements ResourceManager
{
    private static final Logger Log = Logger.getLogger(WebjarResourceManager.class.getName());

    private final WebJarAssetLocator locator = new WebJarAssetLocator();

    @Override
    public Resource getResource (String s) throws IOException
    {
        String[] path = (s.startsWith("/") ? s.substring(1) : s).split("/");

        if(path.length != 2)
        {
            return null;
        }

        String fullPath;

        try
        {
            fullPath = locator.getFullPath(path[0], path[1]);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }

        return new URLResource(ClassLoader.getSystemClassLoader().getResource(fullPath), path[0] + "/" + path[1]);
    }

    @Override
    public boolean isResourceChangeListenerSupported ()
    {
        return false;
    }

    @Override
    public void registerResourceChangeListener (ResourceChangeListener resourceChangeListener)
    {
    }

    @Override
    public void removeResourceChangeListener (ResourceChangeListener resourceChangeListener)
    {
    }

    @Override
    public void close () throws IOException
    {
    }
}
