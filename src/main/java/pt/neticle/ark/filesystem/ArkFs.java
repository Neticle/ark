package pt.neticle.ark.filesystem;

import pt.neticle.ark.exceptions.ExternalConditionException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class ArkFs
{
    private static FileSystem archiveFileSystem;
    private static URI archiveBaseUri;
    private static Path workingDir;

    /**
     * Resolves a usable path to a resource bundled with the application's JAT/WAR archive
     *
     * @param path The path, relative to the root of the archive
     * @return
     */
    public static Path resolveBundled (Path path)
    {
        if(archiveBaseUri == null)
        {
            URL url = ClassLoader.getSystemResource(".");

            if(url == null)
            {
                url = ClassLoader.getSystemResource("META-INF");

                if(url == null)
                {
                    throw new ExternalConditionException("Unable to find META-INF directory");
                }

                try
                {
                    url = new URL(url.toString().substring(0, url.toString().length() - "META-INF".length()));
                } catch(MalformedURLException e)
                {
                    throw new ExternalConditionException(e);
                }
            }

            URI resolved;

            try
            {
                resolved = url.toURI();
            } catch(URISyntaxException e)
            {
                throw new ExternalConditionException(e);
            }

            if(url.toString().startsWith("jar") || url.toString().startsWith("war"))
            {
                try
                {
                    archiveFileSystem = FileSystems.newFileSystem(resolved, Collections.emptyMap());
                } catch(IOException e)
                {
                    throw new ExternalConditionException(e);
                }
            }
            else
            {
                archiveFileSystem = null;
            }

            archiveBaseUri = resolved;
        }

        return archiveFileSystem == null ? Paths.get(archiveBaseUri).resolve(path.toString()) :
            archiveFileSystem.getPath(path.toString());
    }

    /**
     * Resolves a path to a resource contained within the current working directory.
     *
     * @param path The path, relative to the current working directory.
     * @return
     */
    public static Path resolve (Path path)
    {
        if(workingDir == null)
        {
            workingDir = Paths.get(System.getProperty("user.dir"));
        }

        return workingDir.resolve(path);
    }

    public static Path resolveBundled (String first, String... more)
    {
        return resolveBundled(Paths.get(first, more));
    }

    public static Path resolve (String first, String... more)
    {
        return resolve(Paths.get(first, more));
    }
}
