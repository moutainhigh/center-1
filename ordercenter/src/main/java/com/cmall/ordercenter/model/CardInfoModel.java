package com.cmall.ordercenter.model;

/**
 * @author yanzj
 *
 */
/**
 * @author yanzj
 *
 */
/**
 * @author yanzj
 *
 */
/**
 * @author yanzj
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class CardInfoModel{
	/**
	 * 取得卡号
	 * @return 卡号
	 */
	public String getCardCode() {
		return cardCode;
	}
	/**
	 * 设置卡号
	 * @param cardCode 卡号
	 */
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}
	/**
	 * 取得密码
	 * @return 密码
	 */
	public String getCardPass() {
		return cardPass;
	}
	/**
	 * 设置密码
	 * @param cardPass 密码
	 */
	public void setCardPass(String cardPass) {
		this.cardPass = cardPass;
	}
	/**
	 * 取得起始时间
	 * @return 起始时间
	 */
	public String getBeginTime() {
		return beginTime;
	}
	/**
	 * 设置起始时间
	 * @param beginTime 起始时间
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	/**
	 * 取得结束时间
	 * @return 结束时间
	 */
	public String getFinishTime() {
		return finishTime;
	}
	/**
	 * 设置结束时间
	 * @param finishTime 结束时间
	 */
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	/**
	 * 取得卡的序列号
	 * @return 卡的序列号
	 */
	public String getCardSerial() {
		return cardSerial;
	}
	/**
	 * 设置卡的序列号
	 * @param cardSerial 卡的序列号
	 */
	public void setCardSerial(String cardSerial) {
		this.cardSerial = cardSerial;
	}
	/**
	 * 取得卡的金钱
	 * @return 卡的金钱
	 */
	public double getCardMoney() {
		return cardMoney;
	}
	/**
	 * 设置卡的金钱
	 * @param cardMoney 卡的金钱
	 */
	public void setCardMoney(double cardMoney) {
		this.cardMoney = cardMoney;
	}
	/**
	 * 取得卡用过的钱
	 * @return 卡用过的钱
	 */
	public double getUsedMoney() {
		return usedMoney;
	}
	/**
	 * 设置卡用过的钱
	 * @param usedMoney 卡用过的钱
	 */
	public void setUsedMoney(double usedMoney) {
		this.usedMoney = usedMoney;
	}
	/**
	 * 取得卡初始化得钱
	 * @return 卡初始化得钱
	 */
	public double getInitalMoney() {
		return initalMoney;
	}
	/**
	 * 设置卡初始化得钱
	 * @param initalMoney 卡初始化得钱
	 */
	public void setInitalMoney(double initalMoney) {
		this.initalMoney = initalMoney;
	}
	
	/**
	 * 取得发放标志  0 未发放 1 已发放 
	 * @return 发放标志  0 未发放 1 已发放 
	 */
	public int getFlagSend() {
		return flagSend;
	}
	/**
	 * 设置发放标志  0 未发放 1 已发放 
	 * @param flagSend 发放标志  0 未发放 1 已发放 
	 */
	public void setFlagSend(int flagSend) {
		this.flagSend = flagSend;
	}
	/**
	 * 取得激活状态 0 未激活 1 已激活
	 * @return 激活状态 0 未激活 1 已激活
	 */
	public int getFlagActive() {
		return flagActive;
	}
	/**
	 * 设置激活状态 0 未激活 1 已激活
	 * @param flagActive 激活状态 0 未激活 1 已激活
	 */
	public void setFlagActive(int flagActive) {
		this.flagActive = flagActive;
	}

	

	
	
	/**
	 * 卡号
	 */
	private String cardCode="";
	
	/**
	 * 密码
	 */
	private String cardPass="";
		
	/**
	 * 起始时间
	 */
	private String beginTime = "";
	
	/**
	 * 结束时间
	 */
	private String finishTime = "";
	
	/**
	 * 卡的序列号
	 */
	private String cardSerial="";
	
	/**
	 * 卡的金钱
	 */
	private double cardMoney=0;
	
	/**
	 * 卡用过的钱
	 */
	private double usedMoney=0;
	
	/**
	 * 卡初始化得钱
	 */
	private double initalMoney=0;
	
	/**
	 * 0 未发放 1 已发放 
	 */
	private int flagSend=0;
	/**
	 * 0 未激活 1 已激活
	 */
	private int flagActive=0;
	
}
