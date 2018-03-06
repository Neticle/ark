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

package pt.neticle.ark.cli;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.data.output.BufferedOutput;
import pt.neticle.ark.data.output.Output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleDispatchContext extends DispatchContext
{
    private final OutputStream response;

    public ConsoleDispatchContext (Context parent, String path, String[] args, OutputStream response)
    {
        this
        (
            parent,
            path,
            Arrays.stream(args)
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .map(parts -> new DispatchParameter(parts[0], parts[1]))
                .collect(Collectors.toList()),
            response
        );
    }

    public ConsoleDispatchContext (Context parent, String path, List<DispatchParameter> parameters, OutputStream response)
    {
        super(parent, path, parameters);
        this.response = response;
    }

    @Override
    public void handleActionOutput (Output output)
    {
        if(!(output instanceof BufferedOutput))
        {
            return;
        }

        BufferedOutput<?> bout = (BufferedOutput<?>) output;

        ContentType contentType = bout.getContentType();

        if(contentType != null)
        {
            // TODO: Some Application content types are also represented as text, such as application/xml,
            // and application/json. We need to add flags to the media types so we can know which ones
            // can be printed, instead of accepting only text/* types.
            if(!(contentType.getMediaType() instanceof MediaType.Text))
            {
                try
                {
                    // If content isn't text based, don't display it, just display it's type
                    response.write(("[" + contentType.toString() + "]").getBytes());
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
                return;
            }
        }

        try
        {
            bout.writeTo(response);
        } catch(IOException e)
        {
            System.err.println("Failed to output to console");
            e.printStackTrace();
        }
    }
}
