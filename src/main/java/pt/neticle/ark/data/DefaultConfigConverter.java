package pt.neticle.ark.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class DefaultConfigConverter extends DefaultConverter
{
    public DefaultConfigConverter ()
    {
        super();

        addConverter(String.class, Path.class, (s) -> Optional.of(Paths.get(s)));
    }
}
