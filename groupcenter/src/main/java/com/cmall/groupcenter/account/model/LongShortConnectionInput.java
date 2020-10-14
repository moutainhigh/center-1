package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 长转短链接
 * @author Administrator
 *
 */
public class LongShortConnectionInput extends RootInput {

	@ZapcomApi(value="短连接",remark="www.weigongshe.com",require=1)
	private String longUrl = "";

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	
}
