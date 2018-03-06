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

package pt.neticle.ark.data.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class BufferedOutput<T> implements ContentOutput<T>
{
    private final OutputStream ostream;
    private final boolean internalBuffer;

    /* internal buffer */
    protected BufferedOutput ()
    {
        ostream = new ByteArrayOutputStream(1024*2);
        internalBuffer = true;
    }

    /* externally provided buffer */
    protected BufferedOutput (OutputStream os)
    {
        ostream = os;
        internalBuffer = false;
    }

    protected final OutputStream output ()
    {
        return ostream;
    }

    public final void writeTo (OutputStream out) throws IOException
    {
        if(internalBuffer && ostream instanceof ByteArrayOutputStream)
        {
            ((ByteArrayOutputStream) ostream).writeTo(out);
        }
    }

    public boolean hasInternalBuffer ()
    {
        return internalBuffer;
    }
}
