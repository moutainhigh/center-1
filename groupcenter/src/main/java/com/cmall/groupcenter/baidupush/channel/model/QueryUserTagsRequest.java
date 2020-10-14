package com.cmall.groupcenter.baidupush.channel.model;

import com.cmall.groupcenter.baidupush.channel.constants.BaiduChannelConstants;
import com.cmall.groupcenter.baidupush.core.annotation.HttpParamKeyName;
import com.cmall.groupcenter.baidupush.core.annotation.R;

public class QueryUserTagsRequest extends ChannelRequest {

    @HttpParamKeyName(name = BaiduChannelConstants.USER_ID, param = R.REQUIRE)
    private String userId = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
