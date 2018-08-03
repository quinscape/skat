package de.fforw.skat.runtime;

import de.fforw.skat.model.channel.ChannelListing;

/**
 * Sort channels by increasing user count and name.
 */
public class ChannelComparator
    implements java.util.Comparator<ChannelListing>
{
    @Override
    public int compare(ChannelListing o1, ChannelListing o2)
    {
        final int numUserA = o1.getUsers().size();
        final int numUserB = o2.getUsers().size();

        if (numUserA != numUserB)
        {
            return numUserA - numUserB;
        }
        else
        {
            return o1.getId().compareTo(o2.getId());
        }
    }
}
