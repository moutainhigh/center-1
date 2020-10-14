package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 活动- 回复评价输入类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class ActivityReplyCommentsInput extends RootInput{
	
	@ZapcomApi(value="信息编码",remark="信息编码",demo="1234567890",require=1)
	private String info_code = "";
	
	@ZapcomApi(value="评论id",remark="评论id",demo="123456",require=1)
	private String comment = "";
	
	@ZapcomApi(value="评论内容",remark="评论内容",demo="adfasdfasdf",require=1,verify={"minlength=6","maxlength=2000"})
	private String text = "";
	

	public String getInfo_code() {
		return info_code;
	}

	public void setInfo_code(String info_code) {
		this.info_code = info_code;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
