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

public class LiteralObject<TVal> implements Node.Literal<TVal>
{
    private final TVal value;

    LiteralObject (TVal _value)
    {
        value = _value;
    }

    @Override
    public TVal get ()
    {
        return value;
    }
}
