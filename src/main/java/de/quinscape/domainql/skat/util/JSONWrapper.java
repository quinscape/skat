package de.quinscape.domainql.skat.util;

import de.quinscape.spring.jsview.util.JSONUtil;
import org.svenson.JSONable;

/**
 * Wraps a JSON string as object which jsonifies to that JSON string when jsonified.
 */
public final class JSONWrapper
    implements JSONable
{
    private final String json;


    private JSONWrapper(String json)
    {
        this.json = json;
    }

    public static JSONWrapper wrap(Object o)
    {
        return new JSONWrapper(
            JSONUtil.DEFAULT_GENERATOR.forValue(o)
        );
    }

    @Override
    public String toJSON()
    {
        return json;
    }
}
