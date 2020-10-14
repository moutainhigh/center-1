package com.cmall.usercenter.service.sellerinfo;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class Inputparam  extends RootInput
{
	/**
	 * 编辑人员ID
	 */
	@ZapcomApi(value="编辑人员ID")
	private String editId = "";

	public String getEditId()
	{
		return editId;
	}

	public void setEditId(String editId)
	{
		this.editId = editId;
	}
	
}
