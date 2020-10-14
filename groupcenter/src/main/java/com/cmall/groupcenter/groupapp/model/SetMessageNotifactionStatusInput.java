package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class SetMessageNotifactionStatusInput extends RootInput{
	
	@ZapcomApi(value="操作类型",require=1,remark = "0:开启免打扰,1:关闭免打扰")
	private String operationType="";


	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
}
