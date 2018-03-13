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

package pt.neticle.ark.http;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.ArkDataUtils;
import pt.neticle.ark.data.output.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A context that holds http request and response objects.
 *
 * The request's query string is used to populate the dispatch context parameters.
 */
public class HttpDispatchContext extends DispatchContext
{
    private static final Logger Log = Logger.getLogger(HttpDispatchContext.class.getName());

    private final HttpRequest request;
    private final HttpResponse response;

    public HttpDispatchContext (Context parent, HttpRequest request, HttpResponse response)
    {
        super
        (
            parent,
            request.getPath(),

            Arrays.stream(request.getQueryString().split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .map(parts -> new DispatchParameter(parts[0], ArkDataUtils.decodeUrl(parts[1])))
                .collect(Collectors.toList())
        );

        this.request = request;
        this.response = response;
    }

    public HttpRequest getRequest ()
    {
        return request;
    }

    public HttpResponse getResponse ()
    {
        return response;
    }

    @Override
    public void handleActionOutput (Output output)
    {
        Log.fine(() -> "Handling action output: " + output.getClass().getName());

        if(output instanceof ContentOutput)
        {
            ((ContentOutput<?>) output).contentType()
                .ifPresent((contentType) -> response.setHeader("Content-Type", contentType.toString()));
        }

        if(output instanceof BufferedOutput &&
            ((BufferedOutput<?>) output).hasInternalBuffer())
        {
            try
            {
                ((BufferedOutput<?>) output).writeTo(response.contentOutput());
            } catch(IOException e)
            {
                response.setStatusCode(HttpResponse.Status.INTERNAL_SERVER_ERROR);

                Log.warning("Unable to write response: " + e.toString());
            }
        }
    }
}
