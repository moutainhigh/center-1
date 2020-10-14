package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 查询老推新活动入参
 * @author AngelJoy
 * @date 2020-03-18
 * @time 14:49
 */
public class RsyncRequestSyncRegularToNewRelation implements IRsyncRequest {
	
	private List<RsyncModelRegularToNewRelation> paramList = new ArrayList<RsyncModelRegularToNewRelation>();

    public List<RsyncModelRegularToNewRelation> getParamList() {
        return paramList;
    }

    public void setParamList(List<RsyncModelRegularToNewRelation> paramList) {
        this.paramList = paramList;
    }
	
}
