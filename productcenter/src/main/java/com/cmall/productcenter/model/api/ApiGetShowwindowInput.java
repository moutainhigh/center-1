package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ApiGetShowwindowInput 
 * 类描述：     获取卖家橱窗产品的输入参数
 * 创建人：     gaoy
 * 创建时间：2013年10月30日上午11:35:45
 * 修改人：     gaoy
 * 修改时间：2013年10月30日上午11:35:45
 * 修改备注： 
 *
 */
public class ApiGetShowwindowInput extends RootInput{

	/**
	 * 卖家编号
	 */
	@ZapcomApi(value="卖家编号")
	private String selleCode="";

	public String getSelleCode() {
		return selleCode;
	}

	public void setSelleCode(String selleCode) {
		this.selleCode = selleCode;
	}

}
