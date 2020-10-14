package com.cmall.ordercenter.model;

import java.util.List;
import java.util.ArrayList;

/**
 * @author yanzj
 *
 */
public class CardRequestObject {

	public List<CardInfoModel> list = new ArrayList<CardInfoModel>();

	//使用人
	private String creator="";
	//使用备注
	private String changeRemark="";
	//使用单号
	private String changeCode="";
	//使用原因
	private String changeSource="";
	//任务号
	private String taskId="";
	
	/**
	 *	408118110002:商城消费 （网上、手机可用） 
	 *	408118110003:电话订购消费 （电话订购可用）
	 */
	private String cardLimit = "408118110002";	
	
	public String getCardLimit() {
		return cardLimit;
	}

	public void setCardLimit(String cardLimit) {
		this.cardLimit = cardLimit;
	}
	
	/** 
	 * 取得 CardInfoModel List
	 * @return List<CardInfoModel>
	 */
	public List<CardInfoModel> getList() {
		return list;
	}

	/**
	 * 设置  CardInfoModel List
	 * @param list
	 */
	public void setList(List<CardInfoModel> list) {
		this.list = list;
	}

	/**
	 * 取得 任务号
	 * @return 任务号
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 设置任务号
	 * @param taskId 任务号
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * 取得 使用人
	 * @return 使用人
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * 设置 使用人
	 * @param creator 使用人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 取得 使用备注
	 * @return 使用备注
	 */
	public String getChangeRemark() {
		return changeRemark;
	}

	/**
	 * 设置 使用备注
	 * @param changeRemark 使用备注
	 */
	public void setChangeRemark(String changeRemark) {
		this.changeRemark = changeRemark;
	}

	/**
	 * 获取 使用单号
	 * @return 使用单号
	 */
	public String getChangeCode() {
		return changeCode;
	}

	/**
	 * 设置 使用单号
	 * @param changeCode 使用单号
	 */
	public void setChangeCode(String changeCode) {
		this.changeCode = changeCode;
	}

	/**
	 * 获取 使用原因
	 * @return 使用原因
	 */
	public String getChangeSource() {
		return changeSource;
	}

	/**
	 * 设置 使用原因
	 * @param changeSource 使用原因
	 */
	public void setChangeSource(String changeSource) {
		this.changeSource = changeSource;
	}
	
	
	
}
