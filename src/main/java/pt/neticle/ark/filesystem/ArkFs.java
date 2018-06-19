// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pt.neticle.ark.filesystem;

import pt.neticle.ark.exceptions.ExternalConditionException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Collections;
import java.util.logging.Logger;

public class ArkFs
{
    private static final Logger Log = Logger.getLogger(ArkFs.class.getName());

    private static FileSystem archiveFileSystem;
    private static URI archiveBaseUri;
    private static Path workingDir;

    public static String readTextFile (Path path, Charset charset)
    {
        try
        {
            return new String(Files.readAllBytes(path), charset);
        } catch(IOException e)
        {
            return null;
        }
    }

    public static String readTextFile (Path path)
    {
        return readTextFile(path, Charset.defaultCharset());
    }

    /**
     * Resolves a usable path to a resource bundled with the application's JAR/WAR archive
     *
     * @param path The path, relative to the root of the archive
     * @return
     */
    public static Path resolveBundled (Path path)
    {
        resolveBasePath();

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

    public static boolean bundledResourcesInArchive ()
    {
        resolveBasePath();

        return archiveFileSystem != null;
    }

    private static void resolveBasePath ()
    {
        if(archiveBaseUri == null)
        {
            Log.fine(() -> "Attempting to resolve archive base path");

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
                Log.info(() -> "Determined bundled resources are located inside jar/war archive");

                try
                {
                    archiveFileSystem = FileSystems.newFileSystem(resolved, Collections.emptyMap());
                } catch(IOException e)
                {
                    throw new ExternalConditionException(e);
                }
            } else
            {
                archiveFileSystem = null;
            }

            archiveBaseUri = resolved;
            Log.info(() -> "Determined bundled resources are located at " + archiveBaseUri.toString());
        }
    }
}
