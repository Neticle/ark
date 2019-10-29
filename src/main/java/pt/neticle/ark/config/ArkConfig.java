package pt.neticle.ark.config;

import com.google.common.base.Preconditions;
import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.DefaultConfigConverter;
import pt.neticle.ark.exceptions.ConfigurationException;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ClassFinder;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ArkConfig
{
    private static final Logger Log = Logger.getLogger(ArkConfig.class.getName());

    private static final String prefix = "ark";
    private static final Map<String, Setting<?>> settingsStore = new HashMap<>();
    private static final Converter converter = new DefaultConfigConverter();
    private static boolean populated = false;

    static
    {
        populateSettingsStore();
    }

    private static void populateSettingsStore ()
    {
        Log.info("Populating ArkConfig settings store");

        ClassFinder cf = new ClassFinder();

        cf.handleSubclassesOf(SettingsBundle.class, (bundleClass) ->
        {
            settingsStore.putAll
            (
                Arrays.stream(bundleClass.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()) &&
                        Modifier.isStatic(field.getModifiers()) &&
                        Setting.class.isAssignableFrom(field.getType()))
                .map(field ->
                {
                    Setting setting;

                    try
                    {
                        setting = (Setting) field.get(null);
                    } catch(IllegalAccessException e)
                    {
                        // we already checked for public access...
                        return null;
                    }

                    Type genericType = field.getGenericType();

                    if(genericType instanceof ParameterizedType &&
                            ((ParameterizedType) genericType).getActualTypeArguments().length == 1 &&
                            ((ParameterizedType) genericType).getActualTypeArguments()[0] instanceof Class)
                    {
                        setting.process
                        (
                            prefix + "." + field.getDeclaringClass().getSimpleName() + "." + field.getName(),
                            (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0]
                        );

                        Log.info("Registered setting " + setting.getQualifiedName());
                    } else
                    {
                        throw new ImplementationException("Unable to determine Setting type argument: " + field.getDeclaringClass().getName() + "." + field.getName());
                    }

                    return setting;
                })
                .filter(setting -> setting != null)
                .collect(Collectors.toMap(setting -> setting.getQualifiedName(), setting -> setting))
            );
        });

        cf.scan();

        populated = true;
    }

    public static Setting getSetting (String qName)
    {
        return settingsStore.get(qName);
    }

    public static Optional<Setting> setting (String qName)
    {
        return Optional.ofNullable(getSetting(qName));
    }

    public static void accept (Map<String, String> settings)
    {
        settings.entrySet().stream()
            .filter(e -> settingsStore.containsKey(e.getKey()))
            .forEach(e -> set(settingsStore.get(e.getKey()), e.getValue()));
    }

    public static void accept (Properties settings)
    {
        settings.entrySet().stream()
            .filter(e -> settingsStore.containsKey(e.getKey()))
            .forEach(e -> set(settingsStore.get(e.getKey()), e.getValue().toString()));
    }

    public static <T> void set (Setting<T> setting, String value)
    {
        Objects.requireNonNull(setting);
        Preconditions.checkArgument(setting.isProcessed());

        Converter.TypeConverter<String, T> tc = converter.getConverter(String.class, setting.getValueType())
            .orElseThrow(() -> new ImplementationException("Unable to obtain a converter for String -> " + setting.getValueType().getSimpleName()));

        T conv = tc.convert(value)
            .orElseThrow(() -> new ConfigurationException("Unable to convert provided value '" + value + "' into a " + setting.getValueType().getSimpleName()));

        setting.setValue(conv);
    }

    public static <T> T get (Setting<T> setting)
    {
        return setting.getValue();
    }

    public static boolean isPopulated ()
    {
        return populated;
    }
}
