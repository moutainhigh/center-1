package com.cmall.groupcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AccountTaskInfo {

	@ZapcomApi(value = "任务描述", remark = "任务详情")
	private String taskDescription="";
	
	@ZapcomApi(value = "任务跳转信息", remark = "任务跳转信息")
	private String taskBtn="";

	@ZapcomApi(value = "任务步骤", remark = "任务步骤")
	private String taskStepNum="";
	
	@ZapcomApi(value = "该任务步骤是否为节点", remark = "否:449747110001,是:449747110002")
	private String isNode="";
	
	@ZapcomApi(value = "该任务步骤是否有动作", remark = "否:0,是:1")
	private String isPlay="";


	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskBtn() {
		return taskBtn;
	}

	public void setTaskBtn(String taskBtn) {
		this.taskBtn = taskBtn;
	}

	public String getIsNode() {
		return isNode;
	}

	public void setIsNode(String isNode) {
		this.isNode = isNode;
	}

	public String getIsPlay() {
		return isPlay;
	}

	public void setIsPlay(String isPlay) {
		this.isPlay = isPlay;
	}

	public String getTaskStepNum() {
		return taskStepNum;
	}

	public void setTaskStepNum(String taskStepNum) {
		this.taskStepNum = taskStepNum;
	}
	
	
	
	
}
