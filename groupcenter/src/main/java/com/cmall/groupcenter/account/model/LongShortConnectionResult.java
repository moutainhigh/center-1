package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 长转短链接 
 * @author Administrator
 *
 */
public class LongShortConnectionResult extends RootResultWeb {

	@ZapcomApi(value="长连接")
	private String shortUrl = "";

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

}
