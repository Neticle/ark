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

import pt.neticle.ark.cli.ConsoleDispatchContext;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class CliApplication extends Application
{
    private OutputStream outputStream;

    public CliApplication ()
    {
        super();
    }

    public CliApplication (PolicyHoldingContext mainContext)
    {
        super(mainContext);
    }

    public OutputStream getOutputStream ()
    {
        return outputStream == null ? System.out : outputStream;
    }

    protected final void setOutputStream (OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    public void dispatch (String... arguments)
    {
        dispatchWithPath
        (
            arguments.length > 0 ? arguments[0] : "",
            arguments.length > 1 ? Arrays.copyOfRange(arguments, 1, arguments.length) : new String[0]
        );
    }

    public final void dispatchWithPath (String path, String... arguments)
    {
        dispatch(prepareContext(path, arguments), ConsoleDispatchContext.class);
    }

    public final void dispatch (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        dispatch(prepareContext(path, parameters), ConsoleDispatchContext.class);
    }

    protected final ConsoleDispatchContext prepareContext (String path, String... arguments)
    {
        return new ConsoleDispatchContext(context(), path, arguments, getOutputStream());
    }

    protected final ConsoleDispatchContext prepareContext
        (String path, List<DispatchContext.DispatchParameter> parameters)
    {
        return new ConsoleDispatchContext(context(), path, parameters, getOutputStream());
    }
}
