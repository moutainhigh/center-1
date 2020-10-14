package com.cmall.groupcenter.mq.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.mq.model.ActivityTypeListenModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForHandlerMqActivityTypeInput extends RootInput {

	@ZapcomApi(value = "活动类型消息", remark = "活动类型消息json串")
	private List<ActivityTypeListenModel> modeList = new ArrayList<ActivityTypeListenModel>();

	public List<ActivityTypeListenModel> getModeList() {
		return modeList;
	}

	public void setModeList(List<ActivityTypeListenModel> modeList) {
		this.modeList = modeList;
	}

}
