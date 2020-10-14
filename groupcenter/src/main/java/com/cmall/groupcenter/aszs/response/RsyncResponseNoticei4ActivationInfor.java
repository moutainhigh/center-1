package com.cmall.groupcenter.aszs.response;

import com.cmall.groupcenter.groupface.IRsyncResponse;


/** 
* @ClassName: RsyncResponseNoticei4ActivationInfor 
* @Description: 爱思助手通知激活返回信息
* @author 张海生
* @date 2016-3-3 下午3:48:37 
*  
*/
public class RsyncResponseNoticei4ActivationInfor implements IRsyncResponse {
	
	/**
	 * 是否成功（true/false）
	 */
	private String success = "";
	
	/**
	 * 是否成功
	 */
	private String message = "";

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

