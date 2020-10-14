package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 新版本返利详情参数(2.1.4版)
 * @author GaoYang
 *
 */
public class RebateRecordDetailInput extends RootInput{
	
	@ZapcomApi(value = "返利记录UID",remark = "返利记录UID" ,require = 1)
	private String rebateUid = "";

	public String getRebateUid() {
		return rebateUid;
	}

	public void setRebateUid(String rebateUid) {
		this.rebateUid = rebateUid;
	}
	
}
