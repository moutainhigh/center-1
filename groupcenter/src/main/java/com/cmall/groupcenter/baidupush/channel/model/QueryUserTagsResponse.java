package com.cmall.groupcenter.baidupush.channel.model;

import java.util.LinkedList;
import java.util.List;

import com.cmall.groupcenter.baidupush.core.annotation.JSonPath;

public class QueryUserTagsResponse extends ChannelResponse {

    @JSonPath(path = "response_params\\tag_num")
    private int tagNum;

    @JSonPath(path = "response_params\\tags")
    private List<TagInfo> tags = new LinkedList<TagInfo>();

    public int getTagNum() {
        return tagNum;
    }

    public void setTagNum(int tagNum) {
        this.tagNum = tagNum;
    }

    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }

}
