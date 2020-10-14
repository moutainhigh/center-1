package com.cmall.groupcenter.mq.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.mq.model.ActivityListenModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForHandlerMqActivityInput extends RootInput {

	@ZapcomApi(value = "活动消息", remark = "活动消息json串")
	private List<ActivityListenModel> modeList = new ArrayList<ActivityListenModel>();

	public List<ActivityListenModel> getModeList() {
		return modeList;
	}

	public void setModeList(List<ActivityListenModel> modeList) {
		this.modeList = modeList;
	}
}
