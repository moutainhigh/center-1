package com.cmall.systemcenter.model;

public class FlowNextOperator {
	
	/**
	 * 下一审批人
	 */
	private String nextOperator="";
	/**
	 * 下一审批人权限
	 */
	private String nextOperatorStatus = "";
	
	public String getNextOperator() {
		return nextOperator;
	}
	public void setNextOperator(String nextOperator) {
		this.nextOperator = nextOperator;
	}
	public String getNextOperatorStatus() {
		return nextOperatorStatus;
	}
	public void setNextOperatorStatus(String nextOperatorStatus) {
		this.nextOperatorStatus = nextOperatorStatus;
	}
	
	

}
