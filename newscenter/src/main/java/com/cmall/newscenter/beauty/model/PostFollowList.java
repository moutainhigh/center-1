package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 追帖列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostFollowList {

	
	@ZapcomApi(value="原帖Id")
	private String post_parent_code = "";
	 
	@ZapcomApi(value="追帖Id")
	private String post_code = "";
	
	@ZapcomApi(value="正文")
	private String post_content = "";
	
	@ZapcomApi(value="图片")
	private String post_img  = "";
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> picInfos = new ArrayList<PicAllInfo>();

	@ZapcomApi(value="商品")
	private ProductInfo productinfo  = new ProductInfo();
	
	@ZapcomApi(value="追帖时间")
	private String publish_time  = "";
	
	@ZapcomApi(value="点赞数")
	private String post_praise  = "";

	@ZapcomApi(value="是否点赞过",remark="是：449746870001；否：449746870002")
	private String ispraise  = "";
	
	@ZapcomApi(value="化妆包")
	private CosmeticInfo cosmetictinfo  = new CosmeticInfo();
	
	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public List<PicAllInfo> getPicInfos() {
		return picInfos;
	}

	public void setPicInfos(List<PicAllInfo> picInfos) {
		this.picInfos = picInfos;
	}

	public ProductInfo getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(ProductInfo productinfo) {
		this.productinfo = productinfo;
	}

	public String getPost_parent_code() {
		return post_parent_code;
	}

	public void setPost_parent_code(String post_parent_code) {
		this.post_parent_code = post_parent_code;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public String getPost_praise() {
		return post_praise;
	}

	public void setPost_praise(String post_praise) {
		this.post_praise = post_praise;
	}

	public String getIspraise() {
		return ispraise;
	}

	public void setIspraise(String ispraise) {
		this.ispraise = ispraise;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}

	public CosmeticInfo getCosmetictinfo() {
		return cosmetictinfo;
	}

	public void setCosmetictinfo(CosmeticInfo cosmetictinfo) {
		this.cosmetictinfo = cosmetictinfo;
	}
}
