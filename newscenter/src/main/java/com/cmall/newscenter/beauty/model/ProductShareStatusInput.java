package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 商品_分享状态查询输入类
 * @author yangrong
 *  date: 2014-09-23
 * @version1.0
 */
public class ProductShareStatusInput extends RootInput{
	
	@ZapcomApi(value="sku编码",remark="123456",demo="123456",require=1,verify="minlength=6")
	private String sku_code="";
	
	@ZapcomApi(value="结束时间",demo="2014-12-01 20:20:20")
	private String end_time="";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	

}
