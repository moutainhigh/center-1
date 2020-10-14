package com.cmall.groupcenter.hserver;

import com.srnpr.zapweb.webmodel.MWebResult;

public class AcceptResult extends MWebResult {

	
	/**
	 * 预期处理数量
	 */
	private int processNum = 0;

	/**
	 * 处理成功数量
	 */
	private int successNum = 0;

	public int getProcessNum() {
		return processNum;
	}

	public void setProcessNum(int processNum) {
		this.processNum = processNum;
	}

	public int getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	
	
	
}
