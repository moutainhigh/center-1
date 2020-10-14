package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class SecurityChannel {
	
	@ZapcomApi(value="渠道名称")
	private String chnanel = "";
	
	@ZapcomApi(value="渠道图片")
	private String icon = "";

	public String getChnanel() {
		return chnanel;
	}

	public void setChnanel(String chnanel) {
		this.chnanel = chnanel;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	
}
