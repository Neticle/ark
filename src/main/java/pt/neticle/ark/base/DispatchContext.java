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

import pt.neticle.ark.data.output.Output;

import java.util.List;
import java.util.stream.Stream;

/**
 * A context that holds two extra pieces of data compared to it's base class: a path and a list of parameters.
 * It is the context that provides the bare minimum pieces of information necessary to receive and route commands
 * into actions.
 */
public abstract class DispatchContext extends Context
{
    /**
     * The requested path
     */
    private final String path;

    /**
     * List of provided parameters
     */
    private final List<DispatchParameter> parameters;

    public DispatchContext (Context parent, String path, List<DispatchParameter> parameters)
    {
        super(parent);
        this.path = path;
        this.parameters = parameters;
    }

    public String getPath ()
    {
        return path;
    }

    public List<DispatchParameter> getParameters ()
    {
        return parameters;
    }

    public Stream<DispatchParameter> parameters ()
    {
        return parameters.stream();
    }

    public abstract void handleActionOutput (Output output);

    /**
     * A helper class that groups together a key and a value, forming a parameter.
     *
     * All parameter values are strings because on almost all use cases, such as receiving requests via an
     * http server, or taking commands via a CLI, the input will be text.
     *
     * Data type conversion is handled down the line by the input handler classes.
     */
    public static class DispatchParameter
    {
        private String key;
        private String value;

        public DispatchParameter (String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey ()
        {
            return key;
        }

        public String getValue ()
        {
            return value;
        }
    }
}
