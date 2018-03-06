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

/**
 * Used when an implementation issue was detected during runtime.
 *
 * Implementation exceptions are applicable when:
 *  - The error originated from an issue pertaining to faulty implementation
 *  - There was no other factor leading to the error other than the way the code was originally implemented, and thus
 *  the error can only be fixed by a new revision.
 *
 *  Some examples of implementation exceptions may be:
 *  - Tried to convert two data-types, but never provided a type converter for such types.
 *  - Tried to inject a type that is non-injectable / doesn't have an injection policy
 *  - Marked a class as a Controller, but didn't create a default constructor with no arguments
 *
 *  Implementation exceptions are mostly meant to be raised by the framework itself, and not by the end-product. Although
 *  their use isn't exclusive. They serve as indicators for problems that must be solved and couldn't be detected during
 *  compile time.
 */
public class ImplementationException extends ArkRuntimeException
{
    public ImplementationException ()
    {
        super();
    }

    public ImplementationException (String message)
    {
        super(message);
    }

    public ImplementationException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ImplementationException (Throwable cause)
    {
        super(cause);
    }

    public static class InjectionFailed extends ImplementationException
    {
        public InjectionFailed ()
        {
        }

        public InjectionFailed (String message)
        {
            super(message);
        }

        public InjectionFailed (String message, Throwable cause)
        {
            super(message, cause);
        }

        public InjectionFailed (Throwable cause)
        {
            super(cause);
        }
    }

    public static class InvalidRoute extends ImplementationException
    {
        public InvalidRoute (Class controller, String actionName)
        {
            super("Attempt to find reverse route to " + controller.getName() + " / " + actionName + " has failed.");
        }
    }
}
