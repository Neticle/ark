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
 * Used when there was an error caused by an unexpected and external occurrence.
 *
 * External condition exceptions are applicable when:
 *  - The error wasn't caused by mismatched, unexpected or incomplete input given by a client
 *  - The error wasn't caused by faulty internal implementation, missing or misconfigured policies, badly constructed
 *  injection points, etc.
 *
 *  Some examples of external conditions may be:
 *  - Failure to connect to a database (not configuration related)
 *  - Failure to write to disk (access denied, lack of space, etc)
 *  - Failure to reach a remote server
 */
public class ExternalConditionException extends ArkRuntimeException
{
    public ExternalConditionException ()
    {
        super();
    }

    public ExternalConditionException (String message)
    {
        super(message);
    }

    public ExternalConditionException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ExternalConditionException (Throwable cause)
    {
        super(cause);
    }
}
