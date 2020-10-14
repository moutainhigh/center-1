package com.cmall.groupcenter.baidupush.channel.model;

import com.cmall.groupcenter.baidupush.core.annotation.HttpPathKeyName;
import com.cmall.groupcenter.baidupush.core.annotation.R;

public class QueryDeviceTypeRequest extends ChannelRequest {

    @HttpPathKeyName(param = R.OPTIONAL)
    private Long channelId = null;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

}
