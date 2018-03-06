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

package pt.neticle.ark.view;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class ViewObject implements View
{
    private Map<String, Object> data;
    private final String name;

    private ViewObject (String _name)
    {
        super();
        name = _name;
        data = new HashMap<>();
    }

    public void set (String key, Object value)
    {
        data.put(key, value);
    }

    public ViewObject with (String key, Object value)
    {
        set(key, value);
        return this;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    @Override
    public Map<String, Object> getData ()
    {
        return data;
    }

    @Override
    public void ready ()
    {
        data = ImmutableMap.copyOf(data);
    }

    public static ViewObject named (String name)
    {
        return new ViewObject(name);
    }
}
