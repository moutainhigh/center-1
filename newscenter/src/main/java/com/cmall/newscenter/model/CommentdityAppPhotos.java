package com.cmall.newscenter.model;

import com.cmall.productcenter.service.ProductService;
import com.mysql.jdbc.StringUtils;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 图片类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class CommentdityAppPhotos {

	@ZapcomApi(value = "原图宽")
	private String width = "";
	
	@ZapcomApi(value = "原图高")
	private String height = "";
	
	@ZapcomApi(value = "原图")
	private String large = "";
	
	@ZapcomApi(value = "缩略图")
	private String thumb = "";

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getLarge() {
		return large;
	}

	public void setLarge(String large) {
		this.large = large;
	}

	public String getThumb() {
		return thumb;
	}

	ProductService productService = new ProductService();
	
	public void setThumb(String thumb) {
		if(org.springframework.util.StringUtils.isEmpty(thumb)){
			this.thumb = "";
		}else if(thumb.equals("null")) {
			this.thumb = "";
			
		}else {
			this.thumb = productService.getPicInfo(800,thumb).getPicNewUrl();
		}
		
		
	}
	
}
