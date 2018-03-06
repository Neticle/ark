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

package pt.neticle.ark.exceptions;

public class InjectionException extends ArkRuntimeException
{
    protected InjectionException ()
    {
    }

    protected InjectionException (String message)
    {
        super(message);
    }

    protected InjectionException (String message, Throwable cause)
    {
        super(message, cause);
    }

    protected InjectionException (Throwable cause)
    {
        super(cause);
    }

    public static class NoSuitableInjector extends InjectionException
    {
        private final Class<?> requestedType;

        public NoSuitableInjector (Class<?> requestedType)
        {
            super("No injector available for " + requestedType.getName() + " type");
            this.requestedType = requestedType;
        }

        public Class<?> getRequestedType ()
        {
            return requestedType;
        }
    }
}
