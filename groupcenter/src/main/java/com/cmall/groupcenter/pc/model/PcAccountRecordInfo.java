package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 账户明细列表内容
 * @author GaoYang
 *
 */
public class PcAccountRecordInfo {
	@ZapcomApi(value = "时间", remark = "时间")
	private String createTime="";
	@ZapcomApi(value = "金额", remark = "金额")
	private String money="0.00";
	@ZapcomApi(value = "类型", remark = "类型")
	private String type="";
	@ZapcomApi(value = "备注", remark = "备注")
	private String remark="";
	@ZapcomApi(value = "标签", remark = "标签")
	private String label="";
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
