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

package pt.neticle.ark.data;

import com.google.common.collect.HashBasedTable;
import pt.neticle.ark.exceptions.ImplementationException;

import java.util.Optional;

/**
 * A converter holds multiple {@link pt.neticle.ark.data.Converter.TypeConverter} objects linked with
 * source and target types for each.
 *
 * The point of this object is to find a suitable type converter given a source and target type.
 */
public class Converter
{
    private final HashBasedTable<Class, Class, TypeConverter> typeConverters;

    public Converter ()
    {
        this.typeConverters = HashBasedTable.create();
    }

    /**
     * Takes a "source" object for conversion.
     *
     * Use this version if you want to explicitly specify the source object's class. Useful if you're dealing with a
     * instance that might be a subclass of the class you're expecting. If that's the case, the source's getClass method
     * will return a subclass that might not have an handler
     *
     * @param source The object to be converted
     * @param sourceClass The source object's class
     * @param <TSource> The type of the object to be converted
     * @return
     */
    public final <TSource> ConvertSource<TSource> transform (TSource source, Class<TSource> sourceClass)
    {
        // TODO: To solve this problem maybe loop through objects class hierarchy until a suitable handler is found?
        return new ConvertSource<>(this, sourceClass, source);
    }

    /**
     * Takes a "source" object for conversion.
     *
     * @param source The object to be converted
     * @param <TSource> The type of the object to be converted
     * @return
     */
    public final <TSource> ConvertSource<TSource> transform (TSource source)
    {
        return transform(source, (Class<TSource>)source.getClass());
    }

    /**
     * Gets a type converter object for the specified types.
     *
     * @param sourceType The type of the objects to be converted
     * @param targetType The type of the objects to be returned
     * @param <TSource> The type of the objects to be converted
     * @param <TTarget> The type of the objects to be returned
     * @return
     */
    public final <TSource, TTarget> Optional<TypeConverter<TSource, TTarget>> getConverter (Class<TSource> sourceType, Class<TTarget> targetType)
    {
        if(sourceType == targetType)
        {
            return Optional.of((o) -> Optional.of((TTarget) o));
        }

        return Optional.ofNullable(typeConverters.get(sourceType, targetType));
    }

    /**
     * Adds a type converter for the given types. If a type converter for these types already exists, it will be
     * replaced.
     *
     * @param source
     * @param target
     * @param converter
     * @param <TSource>
     * @param <TTarget>
     */
    protected final <TSource, TTarget> void addConverter (Class<TSource> source, Class<TTarget> target, TypeConverter<TSource, TTarget> converter)
    {
        typeConverters.put(source, target, converter);
    }

    /**
     * Helper class that takes a source object and contains a method to attempt conversions on it.
     *
     * @param <TSource>
     */
    public static final class ConvertSource<TSource>
    {
        private final Converter converter;
        private final TSource source;
        private final Class<TSource> sourceClass;

        public ConvertSource (Converter converter, Class<TSource> sourceClass, TSource source)
        {
            this.converter = converter;
            this.sourceClass = sourceClass;
            this.source = source;
        }

        /**
         * Attempts to convert the source object into the given target type
         *
         * @param targetType
         * @param <TTarget>
         * @return
         */
        public <TTarget> Optional<TTarget> into(Class<TTarget> targetType)
        {
            TypeConverter<TSource, TTarget> tc = converter.getConverter(sourceClass, targetType)
                .orElseThrow(() -> new ImplementationException("No converter available for " + source.getClass().getName() + " -> " + targetType.getName()));

            return tc.convert(source);
        }
    }

    @FunctionalInterface
    public interface TypeConverter<TSource, TTarget>
    {
        Optional<TTarget> convert (TSource sourceObject);
    }
}
