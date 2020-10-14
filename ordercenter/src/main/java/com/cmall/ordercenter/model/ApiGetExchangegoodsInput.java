package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 查询换货信息输入参数
 * 项目名称：ordercenter 
 * 类名称：     ApiGetExchangegoodsInput 
 * 类描述：     换货信息对象
 * 创建人：     gaoy  
 * 创建时间：2013年9月16日下午2:10:21 
 * 修改人：     gaoy
 * 修改时间：2013年9月16日下午2:10:21
 * 修改备注：  
 * @version
 * 
 */
public class ApiGetExchangegoodsInput extends RootInput{
	
	/**
	 * 买家编号
	 */
	@ZapcomApi(value="买家编号")
	private String buyerCode = "";

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}
}
