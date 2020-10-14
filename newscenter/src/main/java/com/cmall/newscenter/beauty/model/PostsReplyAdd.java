package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 回复帖子列表类输入
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostsReplyAdd {
	
	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="MI140630100001",require=1)
	private String post_code  = "";

	/*@ZapcomApi(value="用户ID",remark="用户ID",demo="MI140630100001",require=1)
	private String publisher_code  = "";*/

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1)
	private String post_content = "";
	
	@ZapcomApi(value="图片",remark="图片",demo="http://8016200024.img",require=1)
	private String post_img  = "";

	@ZapcomApi(value="商品",remark="商品",demo="XX化妆品",require=1)
	private String product_code  = "";

	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}
	

}
