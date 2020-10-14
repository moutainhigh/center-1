package com.cmall.newscenter.young.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class VideoChannelInput extends RootInput {
	
	@ZapcomApi(value="图片宽度")
	private  Integer  picWidth = 0 ;

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	

}
