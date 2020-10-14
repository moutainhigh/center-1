package com.cmall.groupcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AccountCouponInput extends RootInput{
	
	@ZapcomApi(value = "优惠卷类型",remark = "优惠卷类型   历史：1 | 未使用：0")
	private String type = "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();
	
	@ZapcomApi(value = "操作标识 ",remark = "操作标识  query | remove,仅操作单一优惠卷是传入")
	private String operating = "";

	@ZapcomApi(value = "优惠卷唯一编码",remark = "优惠卷唯一编码,仅操作单一优惠卷是传入")
	private String couponId = "";

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getOperating() {
		return operating;
	}

	public void setOperating(String operating) {
		this.operating = operating;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
	
	
}
