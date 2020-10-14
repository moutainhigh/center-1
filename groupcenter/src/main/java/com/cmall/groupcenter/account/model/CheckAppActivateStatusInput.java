package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 检查应用是否激活 参数bean
 * 
 * @author wangzx
 * 
 */
public class CheckAppActivateStatusInput extends RootInput {

	@ZapcomApi(value = "应用编号", remark = "应用编号 (惠家友SI2003,微公社 SI2011)", demo = "SI2003", require = 1)
	private String managecode = "";

	public String getManagecode() {
		return managecode;
	}

	public void setManagecode(String managecode) {
		this.managecode = managecode;
	}

}
