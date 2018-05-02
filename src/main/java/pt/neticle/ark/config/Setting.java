package pt.neticle.ark.config;

import java.util.Optional;
import java.util.function.Supplier;

public class Setting<T>
{
    private T value;
    private Supplier<T> defaultValue;
    private Class<T> valueType;
    private String qualifiedName;
    private boolean processed;

    public Setting ()
    {
        defaultValue = null;
    }

    public Setting (Supplier<T> defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public T getValue ()
    {
        return isSet() ? value : defaultValue.get();
    }

    public Optional<T> value ()
    {
        return Optional.ofNullable(value);
    }

    public String getQualifiedName ()
    {
        return qualifiedName;
    }

    public Class<T> getValueType ()
    {
        return valueType;
    }

    public T getDefaultValue ()
    {
        return defaultValue.get();
    }

    public boolean isProcessed ()
    {
        return processed;
    }

    public boolean isSet ()
    {
        return value != null;
    }

    void setValue (T newValue)
    {
        value = newValue;
    }

    void process (String qualifiedName, Class<T> valueType)
    {
        this.qualifiedName = qualifiedName;
        this.valueType = valueType;
        this.processed = true;
    }
}
