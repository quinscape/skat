package de.quinscape.domainql.skat.model.channel;

import org.svenson.JSONTypeHint;

import java.util.List;

public class ChannelListings
{

    private List<ChannelListing> channels;

    private int rowCount;


    public void setChannels(List<ChannelListing> channels)
    {
        this.channels = channels;
    }


    @JSONTypeHint(Channel.class)
    public List<ChannelListing> getChannels()
    {
        return channels;
    }


    public int getRowCount()
    {
        return rowCount;
    }


    public void setRowCount(int rowCount)
    {
        this.rowCount = rowCount;
    }

}
