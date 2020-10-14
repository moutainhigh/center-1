package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 追帖妆品输入类
 * @author houwen
 * date 2015-01-21
 * @version 1.0
 */
public class PostsCosmeticFollowAddInput extends RootInput {


	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="MI140630100001",require=1)
	private String post_code  = "";

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了")
	private String post_content = "";

	@ZapcomApi(value="妆品id",remark="妆品id",demo="8019406881",require=1)
	private List<String> cosmetic_code  = new ArrayList<String>();

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

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

}
