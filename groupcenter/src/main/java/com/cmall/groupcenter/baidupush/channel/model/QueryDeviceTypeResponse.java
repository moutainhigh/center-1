package com.cmall.groupcenter.baidupush.channel.model;

import com.cmall.groupcenter.baidupush.core.annotation.JSonPath;

public class QueryDeviceTypeResponse extends ChannelResponse {

    @JSonPath(path = "response_params\\device_type")
    private long deviceType;

    public long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(long deviceType) {
        this.deviceType = deviceType;
    }

}
