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

package pt.neticle.ark.data.structured.builder;

import pt.neticle.ark.data.structured.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ListObject implements Node.List
{
    private final ArrayList<Node> nodes;

    ListObject ()
    {
        nodes = new ArrayList<>();
    }

    public <TLiteralValue> ListObject add (Node.Literal<TLiteralValue>... literals)
    {
        for(Node.Literal literal: literals)
        {
            add(literal);
        }
        return this;
    }

    public <TLiteralValue> ListObject add (TLiteralValue... lVals)
    {
        for(TLiteralValue lVal : lVals)
        {
            add(new LiteralObject<>(lVal), null);
        }
        return this;
    }

    public ListObject addObject (Consumer<AssociativeObject> initializer)
    {
        return add(new AssociativeObject(), initializer);
    }

    public ListObject addObject (Node.Associative object)
    {
        return add(object, (o) -> {});
    }

    public ListObject addList (Consumer<ListObject> initializer)
    {
        return add(new ListObject(), initializer);
    }

    public ListObject addList (Node.List list)
    {
        return add(list, (o) -> {});
    }

    private <TChildNode extends Node> ListObject add
        (TChildNode child, Consumer<TChildNode> initializer)
    {
        nodes.add(child);

        if(initializer != null)
        {
            initializer.accept(child);
        }

        return this;
    }

    @Override
    public int count ()
    {
        return nodes.size();
    }

    @Override
    public Node get (int i)
    {
        return nodes.get(i);
    }

    @Override
    public Stream<Node> stream ()
    {
        return nodes.stream();
    }

    @Override
    public Iterator<Node> iterator ()
    {
        return nodes.iterator();
    }

    @Override
    public void forEach (Consumer<? super Node> action)
    {
        nodes.forEach(action);
    }

    @Override
    public Spliterator<Node> spliterator ()
    {
        return nodes.spliterator();
    }
}
