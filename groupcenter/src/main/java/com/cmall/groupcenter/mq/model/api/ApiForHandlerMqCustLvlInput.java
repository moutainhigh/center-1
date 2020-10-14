package com.cmall.groupcenter.mq.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.mq.model.CustLvlListenModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForHandlerMqCustLvlInput extends RootInput {

	@ZapcomApi(value = "客户等级消息", remark = "客户等级消息json串")
	private List<CustLvlListenModel> modeList = new ArrayList<CustLvlListenModel>();

	public List<CustLvlListenModel> getModeList() {
		return modeList;
	}

	public void setModeList(List<CustLvlListenModel> modeList) {
		this.modeList = modeList;
	}

}
