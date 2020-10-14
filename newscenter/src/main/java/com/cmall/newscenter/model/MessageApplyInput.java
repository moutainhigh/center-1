package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 消息 - 报名输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageApplyInput extends RootInput{
	
	@ZapcomApi(value = "消息类型",remark = "0" ,demo= "0,1,2",require = 1)
	private int message_type ;
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
	
	public int getMessage_type() {
		return message_type;
	}

	public void setMessage_type(int message_type) {
		this.message_type = message_type;
	}
	
	
}
