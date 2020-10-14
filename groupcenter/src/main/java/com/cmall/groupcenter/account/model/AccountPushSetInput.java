package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取账户推送信息输入参数
 * @author GaoYang
 *
 */
public class AccountPushSetInput extends RootInput{
	
	@ZapcomApi(value = "接收推送通知总开关", remark = "总开关时传入1或是2(1 开启  2 关闭), 修改某个具体类型时传空", demo = "449747100001开 449747100002 关闭", require = 0)
	private String pushTypeMasterOnoff = "";

	@ZapcomApi(value = "类型ID",remark = "类型ID 修改某个具体类型时必传值" ,demo = "03ad602d623d4c0c97469245292e75f5", require = 0)
	private String pushTypeID = "";
	
	@ZapcomApi(value = "范围",remark = "范围    推送范围的类型pushRangeType为 1(范围选择)时 必传值" ,demo = "449747220001 一度,449747220002 二度,449747220003 自己,449747220004 关闭", require = 0)
	private String pushRange = "";
	
	@ZapcomApi(value = "开关状态",remark = "开关状态   推送范围的类型pushRangeType为 0(独立开关)时 必传值 " ,demo = "449747100001开 449747100002 关闭", require = 0)
	private String pushOnoff = "";
	
	@ZapcomApi(value = "推送范围的类型",remark = "推送范围的类型(0 :独立开关 1 :范围选择) 修改某个具体类型时必传值" ,demo = "0", require = 0)
	private String pushRangeType = "";

	public String getPushTypeMasterOnoff() {
		return pushTypeMasterOnoff;
	}

	public void setPushTypeMasterOnoff(String pushTypeMasterOnoff) {
		this.pushTypeMasterOnoff = pushTypeMasterOnoff;
	}

	public String getPushTypeID() {
		return pushTypeID;
	}

	public void setPushTypeID(String pushTypeID) {
		this.pushTypeID = pushTypeID;
	}

	public String getPushRange() {
		return pushRange;
	}

	public void setPushRange(String pushRange) {
		this.pushRange = pushRange;
	}

	public String getPushOnoff() {
		return pushOnoff;
	}

	public void setPushOnoff(String pushOnoff) {
		this.pushOnoff = pushOnoff;
	}

	public String getPushRangeType() {
		return pushRangeType;
	}

	public void setPushRangeType(String pushRangeType) {
		this.pushRangeType = pushRangeType;
	}
	
	
}
