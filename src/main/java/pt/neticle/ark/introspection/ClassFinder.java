package pt.neticle.ark.introspection;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.util.function.Consumer;

/**
 * Basically a wrapper for FastClasspathScanner
 */
public class ClassFinder
{
    private final FastClasspathScanner scanner;

    public ClassFinder (String basePackage)
    {
        scanner = new FastClasspathScanner(basePackage);
    }

    public void scan ()
    {
        scanner.scan();
    }

    public void handleClassesAnnotatedWith (Class annotation, Consumer<Class> consumer)
    {
        this.scanner.matchClassesWithAnnotation(annotation, c -> consumer.accept(c));
    }

    public <T> void handleSubclassesOf (Class<T> base, Consumer<Class<? extends T>> consumer)
    {
        this.scanner.matchSubclassesOf(base, c -> consumer.accept(c));
    }
}
