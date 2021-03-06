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

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Used when an exception occurred based solely on input provided by the client.
 *
 * Implementation exceptions are applicable when:
 * - The exception depends only on input given
 *
 * Some examples of implementation exceptions may be:
 * - Missing required input
 * - Received input with an invalid format (trying to pass text in an integer parameter)
 * - Requested resource wasn't found
 *
 * For more specific exception types see subclasses of this class.
 */
public class InputException extends ArkRuntimeException
{
    public InputException ()
    {
        super();
    }

    public InputException (String message)
    {
        super(message);
    }

    public InputException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public InputException (Throwable cause)
    {
        super(cause);
    }

    public static class MissingParameters extends InputException
    {
        private final String[] missingParameters;

        public MissingParameters (String... parameters)
        {
            super("Missing required parameters: " + Arrays.asList(parameters).stream().collect(Collectors.joining(", ")));
            missingParameters = parameters;
        }

        public String[] getMissingParameters ()
        {
            return missingParameters;
        }
    }

    public static class MalformedData extends InputException
    {
        public MalformedData ()
        {
        }

        public MalformedData (String message)
        {
            super(message);
        }

        public MalformedData (String message, Throwable cause)
        {
            super(message, cause);
        }

        public MalformedData (Throwable cause)
        {
            super(cause);
        }
    }

    public static class PathNotFound extends InputException
    {
        public PathNotFound ()
        {
            super("Specified request path doesn't exist");
        }
    }

    public static class RequestedResourceNotFound extends InputException
    {
        public RequestedResourceNotFound ()
        {
        }

        public RequestedResourceNotFound (String message)
        {
            super(message);
        }

        public RequestedResourceNotFound (String message, Throwable cause)
        {
            super(message, cause);
        }

        public RequestedResourceNotFound (Throwable cause)
        {
            super(cause);
        }
    }

    public static class PreconditionFailed extends InputException
    {
        public PreconditionFailed ()
        {
        }

        public PreconditionFailed (String message)
        {
            super(message);
        }

        public PreconditionFailed (String message, Throwable cause)
        {
            super(message, cause);
        }

        public PreconditionFailed (Throwable cause)
        {
            super(cause);
        }
    }

    public static class UnauthorizedAccess extends InputException
    {
        public UnauthorizedAccess ()
        {
        }

        public UnauthorizedAccess (String message)
        {
            super(message);
        }

        public UnauthorizedAccess (String message, Throwable cause)
        {
            super(message, cause);
        }

        public UnauthorizedAccess (Throwable cause)
        {
            super(cause);
        }
    }
}
