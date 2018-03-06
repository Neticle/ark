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

package pt.neticle.ark.data.input;

import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.Pair;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.exceptions.InputException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An input map object matches multiple dispatch context parameters that are associative. In order for matching to occur
 * the passed parameters must be named as (name)[(key)], with name being the name of this object. The key and value are
 * extracted, converted to the appropriate types and then stored into the map.
 *
 * If no parameter is matched, the map will be empty.
 *
 * @param <K> The type of the map keys
 * @param <V> The type of the map values
 */
public class InputMap<K, V>
{
    private static Pattern pattern = Pattern.compile("\\w+\\[(.+)\\]$");

    private final Map<K,V> map;
    private final String name;

    public InputMap(Converter ioConverter, DispatchContext context, String name, Class<K> keyDataType, Class<V> valueDataType) throws InjectionException.NoSuitableInjector
    {
        this.map = new HashMap<>();
        this.name = name;

        Converter.TypeConverter<String, K> keyConverter =
            keyDataType.isAssignableFrom(String.class) ? null :
                ioConverter.getConverter(String.class, keyDataType)
                .orElseThrow(() -> new ImplementationException("No converter available for String -> " + keyDataType.getName()));

        Converter.TypeConverter<String, V> valueConverter =
            keyDataType == valueDataType ? (Converter.TypeConverter<String, V>) keyConverter :
                valueDataType.isAssignableFrom(String.class) ? null :
                    ioConverter.getConverter(String.class, valueDataType)
                    .orElseThrow(() -> new ImplementationException("No converter available for String -> " + valueDataType.getName()));

        context.parameters()
            .map((p) -> new Pair<Matcher, String>(pattern.matcher(p.getKey()), p.getValue()))
            .filter((p) -> p.A.matches())
            .forEach((p) -> map.put(convert(keyConverter, p.A.group(1)), convert(valueConverter, p.B)));
    }

    private <T> T convert (Converter.TypeConverter<String, T> converter, String value)
    {
        return converter == null ?
                (T) value :
                converter.convert(value)
                    .orElseThrow(() -> new InputException.MalformedData("Invalid format for supplied value of " + this.name + " parameter"));
    }

    public int size ()
    {
        return map.size();
    }

    public boolean isEmpty ()
    {
        return map.isEmpty();
    }

    public boolean containsKey (K key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue (V value)
    {
        return map.containsValue(value);
    }

    public V get (K key)
    {
        return map.get(key);
    }

    public Set<K> keySet ()
    {
        return map.keySet();
    }

    public Collection<V> values ()
    {
        return map.values();
    }

    public Set<Map.Entry<K,V>> entrySet ()
    {
        return map.entrySet();
    }

    public Object getOrDefault (K key, V defaultValue)
    {
        return map.getOrDefault(key, defaultValue);
    }

    public void forEach (BiConsumer<K,V> action)
    {
        map.forEach(action);
    }
}
