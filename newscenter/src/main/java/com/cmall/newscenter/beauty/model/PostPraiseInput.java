package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子点赞输入类
 * @author houwen
 * date 2014-09-10
 * @version 1.0
 */
public class PostPraiseInput extends RootInput {


	@ZapcomApi(value="帖子Id",remark="帖子Id",demo="112222",require=1)
	private String post_code = "";
	
	/*@ZapcomApi(value="操作类型",remark="操作类型",demo="1",require=1)
	private String operate_type = "" ;
*/
	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

/*	public String getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(String operate_type) {
		this.operate_type = operate_type;
	}*/
	
}
