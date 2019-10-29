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

package pt.neticle.ark.base;

import pt.neticle.ark.exceptions.InputException;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.http.HttpRequest;
import pt.neticle.ark.http.HttpResponse;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.runtime.Cast;

public class WebApplication extends Application
{
    public WebApplication ()
    {
        super();
        configure();
    }

    public WebApplication (PolicyHoldingContext mainContext)
    {
        super(mainContext);
        configure();
    }

    private void configure ()
    {
        context().addPolicy(new InlineInjectionPolicy<>(
            HttpRequest.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                HttpDispatchContext context = Cast.attempt(HttpDispatchContext.class, requestingContext)
                    .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return context.getRequest();
            }
        ));

        context().addPolicy(new InlineInjectionPolicy<>(
            HttpResponse.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                HttpDispatchContext context = Cast.attempt(HttpDispatchContext.class, requestingContext)
                        .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return context.getResponse();
            }
        ));
    }

    public void dispatch (HttpRequest request, HttpResponse response)
    {
        dispatch(new HttpDispatchContext(context(), request, response), HttpDispatchContext.class);
    }
}
