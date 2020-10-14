package com.cmall.groupcenter.behavior.common;

/**
 * 接口请求状态
 * @author pang_jhui
 *
 */
public enum StatusEnum {
	
	/**请求成功*/
	SUCCESS("0",1),
	/**请求失败*/
	FAILURE("1",-1);
	
	
	private String code = "";
	
	private int resultCode ;
	
	StatusEnum(String code,int resultCode){
		
		this.code = code;
		
		this.resultCode = resultCode;
		
	}

	/**
	 * 获取状态编码
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 获取结果编码
	 * @return
	 */
	public int getResultCode() {
		return resultCode;
	}

}
