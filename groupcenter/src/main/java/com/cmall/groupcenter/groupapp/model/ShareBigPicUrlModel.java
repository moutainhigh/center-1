package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 商品分享图片
 * @author fengl
 * date 2015-11-6
 * @version 2.0
 */
public class ShareBigPicUrlModel {

	@ZapcomApi(value = "图片链接")
	private String imageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	
	
	
	
   
}
