package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 申请试用输出类
 * @author houwen
 * date 2014-10-14
 * @version 1.0
 */
public class TryOutApplyResult extends RootResultWeb {

	
	@ZapcomApi(value="申请状态",remark="申请状态 ：已申请：449746890002",demo="449746890002",require=1)
	private String status = "";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
