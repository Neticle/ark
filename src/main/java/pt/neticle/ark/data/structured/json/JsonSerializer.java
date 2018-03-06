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

package pt.neticle.ark.data.structured.json;

import com.google.common.escape.CharEscaper;
import pt.neticle.ark.data.structured.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Serializes a data structure into JSON.
 */
public class JsonSerializer
{
    private static final CharEscaper nonAsciiEscaper = new CharEscaper()
    {
        @Override
        protected char[] escape (char c)
        {
            if(c >= 32 && c <= 127)
            {
                return new char[]{c};
            }
            else
            {
                return String.format("\\u%04x", (int) c).toCharArray();
            }
        }
    };

    public String serialize (Node node)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try
        {
            serialize(node, os);
        } catch(IOException e)
        {
            e.printStackTrace();
        }

        return os.toString();
    }

    public void serialize (Node node, OutputStream os) throws IOException
    {
        Node.Literal literal = getNodeIfMatches(node, Node.Literal.class);
        Node.Associative assoc = getNodeIfMatches(node, Node.Associative.class);
        Node.List list = getNodeIfMatches(node, Node.List.class);

        if(literal != null)
        {
            os.write(serializeLiteralValue(literal.get()).getBytes(StandardCharsets.US_ASCII));
            return;
        }

        if(assoc != null)
        {
            os.write('{');
            int i = 0;
            for(Map.Entry<String, Node> entry : assoc.entrySet())
            {
                os.write(serializeLiteralValue(entry.getKey()).getBytes(StandardCharsets.US_ASCII));
                os.write(':');

                serialize(entry.getValue(), os);

                if(i < assoc.count()-1)
                {
                    os.write(',');
                }

                i++;
            }
            os.write('}');
            return;
        }

        if(list != null)
        {
            os.write('[');
            int i = 0;
            for(Node child : list)
            {
                serialize(child, os);

                if(i < list.count() - 1)
                {
                    os.write(',');
                }

                i++;
            }
            os.write(']');
        }
    }

    private String serializeLiteralValue (Object value)
    {
        if(value instanceof String)
        {

            return '"' + nonAsciiEscaper.escape((String)value) + '"';
        }

        if(value instanceof Number)
        {
            return value.toString();
        }

        if(value instanceof Boolean)
        {
            return ((Boolean)value) ? "true" : "false";
        }

        return serializeLiteralValue(value.toString());
    }

    private <T> T getNodeIfMatches (Node node, Class<T> type)
    {
        if(type.isAssignableFrom(node.getClass()))
        {
            return (T) node;
        }

        return null;
    }
}
