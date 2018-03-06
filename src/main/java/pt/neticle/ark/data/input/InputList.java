package pt.neticle.ark.data.input;

import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.Converter;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.exceptions.InputException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An input list object matches multiple dispatch context parameters with the same name as it's own name followed by "[]".
 *
 * If no parameter is matched, the list will be empty.
 *
 * @param <T> The expected value type for the input values
 */
public class InputList<T>
{
    private final List<T> list;
    private final String name;

    public InputList (Converter ioConverter, DispatchContext context, String name, Class<T> itemDataType) throws InjectionException.NoSuitableInjector
    {
        this.name = name;

        Converter.TypeConverter<String, T> typeConverter;

        if(!itemDataType.isAssignableFrom(String.class))
        {
            typeConverter = ioConverter.getConverter(String.class, itemDataType)
                .orElseThrow(() -> new ImplementationException("No converter available for String -> " + itemDataType.getName()));
        }
        else
        {
            typeConverter = null;
        }

        list = context.parameters()
            .filter((p) -> p.getKey().equals(name + "[]"))
            .map((p) -> convertItem(typeConverter, p.getValue()))
            .collect(Collectors.toList());
    }

    private T convertItem (Converter.TypeConverter<String, T> typeConverter, String itemValue)
    {
        // null means the target type is string, so no conversion needed.

        return typeConverter == null ? (T)itemValue : typeConverter.convert(itemValue)
            .orElseThrow(() -> new InputException.MalformedData("Invalid format for supplied value of " + this.name + " parameter"));
    }

    /* List methods */
    public T get (int index)
    {
        return list.get(index);
    }

    public int size ()
    {
        return list.size();
    }

    public boolean isEmpty ()
    {
        return list.isEmpty();
    }

    public boolean contains (T o)
    {
        return list.contains(o);
    }

    public Iterator<T> iterator ()
    {
        return list.iterator();
    }

    public T[] toArray ()
    {
        return (T[]) list.toArray();
    }

    public <T> T[] toArray (T[] a)
    {
        return list.toArray(a);
    }

    public boolean containsAll (Collection<T> c)
    {
        return list.containsAll(c);
    }

    public Stream<T> stream ()
    {
        return list.stream();
    }

    public Stream<T> parallelStream ()
    {
        return list.parallelStream();
    }
}
