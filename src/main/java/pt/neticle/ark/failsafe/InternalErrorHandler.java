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

package pt.neticle.ark.failsafe;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ArkRuntimeException;

public interface InternalErrorHandler<TCtx extends DispatchContext>
{
    /**
     * Handles an internal error that halted during dispatch.
     *
     * Exceptions such as {@type pt.neticle.ark.exceptions.ExternalConditionException} and
     * {@type pt.neticle.ark.exceptions.ImplementationException} are considered internal errors.
     *
     * @param context
     * @param matchedHandler
     * @param exception
     * @return
     */
    default Output<?> handleInternalError (TCtx context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return null;
    }
}
