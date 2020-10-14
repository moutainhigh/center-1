package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 追帖输入类
 * @author houwen
 * date 2014-09-11
 * @version 1.0
 */
public class PostsFollowAddInput extends RootInput {


	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="MI140630100001",require=1)
	private String post_code  = "";

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1)
	private String comment_content = "";
	
	@ZapcomApi(value="图片",remark="图片",demo="http://8016200024.img")
	private String comment_img  = "";

	@ZapcomApi(value="商品",remark="商品",demo="XX化妆品")
	private String product_code  = "";
	
	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getComment_img() {
		return comment_img;
	}

	public void setComment_img(String comment_img) {
		this.comment_img = comment_img;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	
}
