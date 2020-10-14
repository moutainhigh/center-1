package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取通知消息 参数
 * @author zhouguohui
 *
 */
public class ApiHomeMessageInfoInput extends RootInput{
	
	@ZapcomApi(value="消息类别",require=1,remark="客户端消息列表中的classifyCode", demo="4497471600420002")
	private String classifyCode="4497471600420001";
	@ZapcomApi(value="当前页面",require=1,remark="当前页  默认为第一页")
	private int pageNo=0;
	@ZapcomApi(value="每页多少条",require=1,remark="每页显示多少条数据    默认显示10条")
	private int pageSize=10;

	
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
		
}
