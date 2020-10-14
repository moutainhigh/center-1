package com.cmall.groupcenter.baidupush.channel.model;

import com.cmall.groupcenter.baidupush.channel.constants.BaiduChannelConstants;
import com.cmall.groupcenter.baidupush.core.annotation.HttpParamKeyName;
import com.cmall.groupcenter.baidupush.core.annotation.R;
import com.cmall.groupcenter.baidupush.core.annotation.RangeRestrict;

public class SetTagRequest extends ChannelRequest {

    @HttpParamKeyName(name = BaiduChannelConstants.TAG_NAME, param = R.REQUIRE)
    @RangeRestrict(minLength = 1, maxLength = 128)
    private String tag;

    @HttpParamKeyName(name = BaiduChannelConstants.USER_ID, param = R.OPTIONAL)
    @RangeRestrict(minLength = 1, maxLength = 256)
    private String userId;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
