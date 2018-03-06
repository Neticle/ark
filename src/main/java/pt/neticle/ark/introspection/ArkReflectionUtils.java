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

package pt.neticle.ark.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class ArkReflectionUtils
{
    /**
     * Creates a new object instance of the provided class, if the class has an accessible default constructor
     * (public, with no arguments).
     *
     * @param classType The object class to instantiate
     * @param <T> The object type
     * @return Optional containing the instance, or empty if the class has no suitable constructor.
     */
    public static <T> Optional<T> createInstanceNoArgs (Class<T> classType)
    {
        try
        {
            Constructor<T> constr = classType.getConstructor();

            return Modifier.isPublic(constr.getModifiers()) ? Optional.of(constr.newInstance()) : Optional.empty();
        } catch(NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
        {
            return Optional.empty();
        }
    }
}
