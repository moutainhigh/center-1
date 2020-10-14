package com.cmall.groupcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AccountTaskInput extends RootInput{
	
	@ZapcomApi(value = "判断节点的顺序",remark = "判断节点的顺序")
	private String stepNum = "";
	
	@ZapcomApi(value = "判断用户是否有任务、判断用户等级是否升级",remark = "1：若任务执行到最后一步，将用户等级升级,2:判断用户是否有任务或初次登录")
	private String flag = "";
	
	@ZapcomApi(value = "任务id",remark="升级用户奖励时候，参入任务id参数")
	private String tid = "";
	
	

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getStepNum() {
		return stepNum;
	}

	public void setStepNum(String stepNum) {
		this.stepNum = stepNum;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}
	
	

}
