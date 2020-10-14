package com.cmall.groupcenter.favorites.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 添加帖子收藏接口输出类
 * @author guz
 *
 */
public class ApiCollectionsAddResult extends RootResultWeb{
	@ZapcomApi(value = "状态", remark = "4497472000020001可用4497472000020002移除")
	private String flag = "";
	
	@ZapcomApi(value = "收藏数量", remark = "收藏数量")
	private String collectNum = "";

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(String collectNum) {
		this.collectNum = collectNum;
	}
	
}
