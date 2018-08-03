package de.fforw.skat.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Util
{
    private Util()
    {

    }

    public final static String joinWithComma(Collection<?> c)
    {
        return join(c, ", ");
    }
    public final static String join(Collection<?> collection, String sep)
    {
        if (collection == null)
        {
            throw new IllegalArgumentException("collection can't be null");
        }

        if (sep == null)
        {
            throw new IllegalArgumentException("sep can't be null");
        }

        final StringBuilder buff = new StringBuilder();
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); )
        {
            Object o = iterator.next();

            buff.append(o);

            if (iterator.hasNext())
            {
                buff.append(sep);
            }
        }
        return buff.toString();
    }

    private final static int[] BASE_VALUES = new int[] {
        9,
        10,
        11,
        12,
        24
    };

    public final static List<Integer> GAME_VALUES;
    static
    {
        final ArrayList<Integer> values = new ArrayList<>();

        values.add(23);
        values.add(35);
        values.add(46);
        values.add(59);

        for(int i=2; i < 24; i++)
        {
            for (int BASE_VALUE : BASE_VALUES)
            {
                values.add(i * BASE_VALUE);
            }
        }

        Collections.sort(values);

        GAME_VALUES = Collections.unmodifiableList(values);
    }


    public static int getNextGameValue(int value)
    {
        for (Integer curr : GAME_VALUES)
        {
            if (curr > value)
            {
                return curr;
            }
        }

        throw new MaximumGameValueExceedException("Maximum game value reached.");
    }


    public static void main(String[] args)
    {
        int nextValue, value = 18;
        for (int i = 0; i < 100; i++)
        {
            nextValue = getNextGameValue(value);
            if (nextValue == value)
            {
                break;
            }
            else
            {
                value = nextValue;
                System.out.println(i + ": " + value);
            }
        }
    }
}
