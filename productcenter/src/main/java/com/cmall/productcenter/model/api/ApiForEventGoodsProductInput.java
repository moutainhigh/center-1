package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForEventGoodsProductInput extends RootInput {
  
	@ZapcomApi(value="活动编号",remark="加载单个商品的时间调用方法,传递CX开头的编号。加载全部的拼好货商品该字段传递为空")
	private String eventCoe="";

	@ZapcomApi(value="活动编号",remark="加载用户购买拼好货历史数据方法调用参数，传递CX开头的编号。改方法不走缓存，不是查看历史尽量不要调用")
	private List<String> listEventCode= new ArrayList<String>();
	
	/**
	 * @return the eventCoe
	 */
	public String getEventCoe() {
		return eventCoe;
	}

	/**
	 * @param eventCoe the eventCoe to set
	 */
	public void setEventCoe(String eventCoe) {
		this.eventCoe = eventCoe;
	}

	/**
	 * @return the listEventCode
	 */
	public List<String> getListEventCode() {
		return listEventCode;
	}

	/**
	 * @param listEventCode the listEventCode to set
	 */
	public void setListEventCode(List<String> listEventCode) {
		this.listEventCode = listEventCode;
	}
	
	
}
