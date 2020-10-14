package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 项目名称：usercenter 
 * 类名称：     ApiMemberCreditsManageResult 
 * 类描述：     会员积分管理接口输入参数
 * 创建人：     GaoYang  
 * 创建时间：2013年11月9日下午2:08:27 
 * 修改人：     GaoYang
 * 修改时间：2013年11月9日下午2:08:27
 * 修改备注：  
 * @version
 * 
 */
public class ApiMemberCreditsManageInput extends RootInput{
	
	/**
	 * 会员ID
	 */
	@ZapcomApi(value="会员ID")
	private String userId = "";
	
	/**
	 * 积分状态（积分状态，0为无操作，1为增加积分，2为消减积分，9为清零）
	 */
	@ZapcomApi(value="积分状态",remark="0为无操作，1为增加积分，2为消减积分，9为清零")
	private int creditsStatus = 0;
	
	/**
	 * 积分内容
	 */
	@ZapcomApi(value="积分内容")
	private String creditsContent = "";
	
	/**
	 * 积分值（增加或消减的积分值）
	 */
	@ZapcomApi(value="积分值",remark="增加或消减的积分值")
	private int creditsValue = 0;
	
	/**
	 * 积分备注
	 */
	@ZapcomApi(value="积分备注")
	private String creditsMemo = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCreditsStatus() {
		return creditsStatus;
	}

	public void setCreditsStatus(int creditsStatus) {
		this.creditsStatus = creditsStatus;
	}

	public String getCreditsContent() {
		return creditsContent;
	}

	public void setCreditsContent(String creditsContent) {
		this.creditsContent = creditsContent;
	}

	public int getCreditsValue() {
		return creditsValue;
	}

	public void setCreditsValue(int creditsValue) {
		this.creditsValue = creditsValue;
	}

	public String getCreditsMemo() {
		return creditsMemo;
	}

	public void setCreditsMemo(String creditsMemo) {
		this.creditsMemo = creditsMemo;
	}
	
}
