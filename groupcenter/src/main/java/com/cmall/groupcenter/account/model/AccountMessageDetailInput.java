package com.cmall.groupcenter.account.model;

import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 消息模块-设置消息为已读状态接口
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageDetailInput extends RootInput {
	
	@ZapcomApi(value = "消息类型", remark = "消息类型(1:好消息，2:坏消息 ，3：新好友加入),如果不传该参数或者传空字符串，则返回所有消息列表", demo = "1",require = 0)
	private String messageType="";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}
