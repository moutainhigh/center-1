package com.cmall.groupcenter.account.model;

import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 消息模块-设置消息为已读状态接口
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageDetailListResult extends RootResultWeb{
	
	
	@ZapcomApi(value = "消息详情list", remark = "消息详情的列表，均为同一类型的消息详情", demo = "")
	List<AccountMessageDetailResult> detailList;

	@ZapcomApi(value = "消息类型", remark = "消息类型(1:好消息，2:坏消息 3:新好友加入),如果输入参数的messageType不传或者为空时，此时的messageType返回空字符串", demo = "1")
	private String messageType;
	
	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	

	public List<AccountMessageDetailResult> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<AccountMessageDetailResult> detailList) {
		this.detailList = detailList;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
	
	
	
}
