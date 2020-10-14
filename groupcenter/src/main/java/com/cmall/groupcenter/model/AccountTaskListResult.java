package com.cmall.groupcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AccountTaskListResult  extends RootResultWeb {

	@ZapcomApi(value = "账户任务信息", remark = "账户任务信息")
	List<AccountTaskInfo> accountTaskList = new ArrayList<AccountTaskInfo>();
	
	@ZapcomApi(value = "任务完成时步骤",remark = "任务完成时步骤")
	private int maxStepNum;
	
	@ZapcomApi(value = "判断用户是否有任务",remark = "0：无任务，1：有任务")
	private int isTask;
	
	@ZapcomApi(value = "任务id",remark="升级用户奖励时候，参入任务id参数")
	private String tid = "";
	
	@ZapcomApi(value = "任务等级",remark = "任务等级")
	private String level;
	
	@ZapcomApi(value = "任务标识",remark = "任务标识")
	private String taskMark;

	public int getMaxStepNum() {
		return maxStepNum;
	}

	public void setMaxStepNum(int maxStepNum) {
		this.maxStepNum = maxStepNum;
	}

	public List<AccountTaskInfo> getAccountTaskList() {
		return accountTaskList;
	}

	public void setAccountTaskList(List<AccountTaskInfo> accountTaskList) {
		this.accountTaskList = accountTaskList;
	}

	public int getIsTask() {
		return isTask;
	}

	public void setIsTask(int isTask) {
		this.isTask = isTask;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTaskMark() {
		return taskMark;
	}

	public void setTaskMark(String taskMark) {
		this.taskMark = taskMark;
	}
	
	
	
	
}
