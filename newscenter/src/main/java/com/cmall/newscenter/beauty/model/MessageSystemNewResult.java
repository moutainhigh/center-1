package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 系统 -  姐妹圈首页获取是否有最新消息 输出类
 * @author houwen	
 * date 2014-9-29
 * @version 1.0
 */
public class MessageSystemNewResult extends RootResultWeb{
	
		
	@ZapcomApi(value = "是否有最新消息",remark="有:1；没有：0")
	private String is_read = "";
	
	@ZapcomApi(value = "新消息数量",remark="10")
	private String count = "";

	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

}
