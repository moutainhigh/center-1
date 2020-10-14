package com.cmall.ordercenter.model;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ResponseObject 
 * 类描述：     前后数据交互类
 * 创建人：     gaoy  
 * 创建时间：2013年9月11日上午11:13:16 
 * 修改人：     gaoy
 * 修改时间：2013年9月11日上午11:13:16
 * 修改备注：  
 * @version
 *
 */
public class ResponseObject {

	//返回结果-成功
	public static final int RESULT_SUCESS = 1;
	//返回结果-失败
	public static final int RESULT_FAIL = 0;
	
	//返回结果标志
	private int result;
	//错误内容
	private String message;
	//返回数据
	private Object data;
	
	private String items ="";
	

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public static int getResultSucess() {
		return RESULT_SUCESS;
	}

	public static int getResultFail() {
		return RESULT_FAIL;
	}

	/**
	 * 取得返回值，正确为1  否则为错误编码
	 * @return
	 */
	public int getResult() {
		return result;
	}
	
	/**
	 * 设置返回值，正确为1  否则为错误编码
	 * @param result
	 */
	public void setResult(int result) {
		this.result = result;
	}
	
	/**
	 * 取得错误内容
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 设置错误内容 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 取得返回数据
	 * @return
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * 设置返回数据
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
}
