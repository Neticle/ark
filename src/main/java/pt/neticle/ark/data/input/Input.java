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
import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.exceptions.InputException;

/**
 * An input object matches dispatch context parameters. If a parameter is matched, it's text value will be converted
 * to the appropriate and expected type.
 *
 * If no parameter is matched, a {@link pt.neticle.ark.exceptions.InputException.MissingParameters} will be raised.
 *
 * @param <T> The expected value type for the input
 */
public class Input<T>
{
    private final String name;
    private final Class<T> dataType;
    private final T data;
    private final String originalTextValue;

    public Input (Converter ioConverter, DispatchContext context, String name, Class<T> dataType) throws InjectionException.NoSuitableInjector
    {
        originalTextValue = context
            .parameters()
            .filter((p) -> p.getKey().equals(name))
            .map((p) -> p.getValue())
            .findFirst().orElseThrow(() -> new InputException.MissingParameters(name));

        if(dataType.isAssignableFrom(String.class))
        {
            data = (T)originalTextValue;
        }
        else
        {
            data = ioConverter.transform(originalTextValue, String.class).into(dataType)
                .orElseThrow(() -> new InputException.MalformedData("Invalid format for supplied value of " + name + " parameter"));
        }

        this.dataType = dataType;
        this.name = name;
    }

    public T get ()
    {
        return data;
    }

    public String getOriginalTextValue ()
    {
        return originalTextValue;
    }

    public Class<T> getDataType ()
    {
        return dataType;
    }

    public String getName ()
    {
        return name;
    }
}
