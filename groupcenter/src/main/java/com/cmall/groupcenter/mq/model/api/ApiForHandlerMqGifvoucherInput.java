package com.cmall.groupcenter.mq.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.mq.model.GiftVoucherDetailListenModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForHandlerMqGifvoucherInput extends RootInput {

	@ZapcomApi(value = "礼金券详情消息", remark = "礼金券详情消息json串")
	private List<GiftVoucherDetailListenModel> modeList = new ArrayList<GiftVoucherDetailListenModel>();

	public List<GiftVoucherDetailListenModel> getModeList() {
		return modeList;
	}

	public void setModeList(List<GiftVoucherDetailListenModel> modeList) {
		this.modeList = modeList;
	}
	
	
}
