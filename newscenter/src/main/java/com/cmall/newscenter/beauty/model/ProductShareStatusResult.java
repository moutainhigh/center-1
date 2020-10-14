package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品_分享状态查询输出类
 * @author yangrong
 *  date: 2014-09-23
 * @version1.0
 */
public class ProductShareStatusResult extends RootResultWeb{

	@ZapcomApi(value="分享状态",remark="1是已分享   0是未分享")
	private String status="";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
