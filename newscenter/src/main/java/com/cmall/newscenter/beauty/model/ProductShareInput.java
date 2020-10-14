package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 * 试用商品_分享Api
 * @author yangrong
 *  date: 2014-09-23
 * @version1.0
 */
public class ProductShareInput extends RootInput{
	
	@ZapcomApi(value="sku编码",remark="123456",demo="123456",require=1,verify="minlength=6")
	private String sku_code="";
	
	@ZapcomApi(value="结束时间",demo="2014-12-01 20:20:20",require=0)
	private String end_time="";
	
	@ZapcomApi(value="分享平台",remark="微信=449746850001  QQ=449746850002  朋友圈=449746850003  微博=449746850004",demo="449746850001")
	private String share_type="";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getShare_type() {
		return share_type;
	}

	public void setShare_type(String share_type) {
		this.share_type = share_type;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	

}

