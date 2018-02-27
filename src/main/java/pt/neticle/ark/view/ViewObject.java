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
