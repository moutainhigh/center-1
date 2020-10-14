package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 发布妆品帖子输入类
 * @author houwen
 * date 2015-01-21
 * @version 1.0
 */
public class PostsCometicAddInput extends RootInput {


	@ZapcomApi(value="选择标签",remark="选择标签",demo="化妆品",require=1)
	private String post_label = "";
	
	@ZapcomApi(value="标题",remark="标题",demo="XX化妆品太好用了",require=1)
	private String post_title = "" ;
	
	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了")
	private String post_content = "";
	
	@ZapcomApi(value="妆品id",remark="妆品id",demo="8019406881",require=1)
	private List<String> cosmetic_code  = new ArrayList<String>();

	public String getPost_label() {
		return post_label;
	}

	public void setPost_label(String post_label) {
		this.post_label = post_label;
	}

	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public List<String> getCosmetic_code() {
		return cosmetic_code;
	}

	public void setCosmetic_code(List<String> cosmetic_code) {
		this.cosmetic_code = cosmetic_code;
	}
	
}
