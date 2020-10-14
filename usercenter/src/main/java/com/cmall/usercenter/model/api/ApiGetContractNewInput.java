package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.baseface.IBaseInput;

/**
 * 输入参数
 * 
 * @author srnpr
 * 
 */
public class ApiGetContractNewInput implements IBaseInput {

	/**
	 * 图片路径
	 */
	@ZapcomApi(value = "供应商编号", remark = "", require = 0)
	private String sellerCode;

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

}
