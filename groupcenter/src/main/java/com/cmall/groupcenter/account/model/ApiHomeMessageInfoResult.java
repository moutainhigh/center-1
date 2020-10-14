package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 用户订单轨迹
 * @author wz
 *
 */
public class ApiHomeMessageInfoResult{
	@ZapcomApi(value="消息编号UID")
	private String uid="";
	@ZapcomApi(value="消息标题")
	private String title="";
	@ZapcomApi(value="售后单号")
	private String afterSaleCode="";
	@ZapcomApi(value="消息图片")
	private String picture="";
	@ZapcomApi(value="消息内容")
	private String content="";
	@ZapcomApi(value="消息通知类型：4497471600410001纯文本/4497471600410002是URL链接 /4497471600410003是商品/4497471600410004跳转售后/4497471600410005跳转物流")
	private String type="";
	@ZapcomApi(value="消息链接类型的链接地址")
	private String url="";
	@ZapcomApi(value="消息商品类型的商品编号")
	private String product_code="";
	@ZapcomApi(value="消息开始时间")
	private String start_time="";
	@ZapcomApi(value="消息结束时间")
	private String end_time="";
	@ZapcomApi(value="消息物流的订单编号")
	private String order_code="";
	@ZapcomApi(value="消息物流订单的商品主图")
	private String prod_main_pic="";
	@ZapcomApi(value="消息布局类型:4497471600440001纯文本/4497471600440002有图上下格式/4497471600440003有图左右格式/4497471600440004意见反馈专用")
	private String layout="";
	
	@ZapcomApi(value="意见反馈内容")
	private String suggestion_feedback="";
	@ZapcomApi(value="客服回复内容")
	private String repply_content="";
	@ZapcomApi(value="提交意见反馈的时间")
	private String feedbackTime="";
	
	public String getSuggestion_feedback() {
		return suggestion_feedback;
	}

	public void setSuggestion_feedback(String suggestion_feedback) {
		this.suggestion_feedback = suggestion_feedback;
	}

	public String getRepply_content() {
		return repply_content;
	}

	public void setRepply_content(String repply_content) {
		this.repply_content = repply_content;
	}

	public String getFeedbackTime() {
		return feedbackTime;
	}

	public void setFeedbackTime(String feedbackTime) {
		this.feedbackTime = feedbackTime;
	}

	public String getProd_main_pic() {
		return prod_main_pic;
	}

	public void setProd_main_pic(String prod_main_pic) {
		this.prod_main_pic = prod_main_pic;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getUid() {
		return uid;
	}
	
	public String getAfterSaleCode() {
		return afterSaleCode;
	}

	public void setAfterSaleCode(String afterSaleCode) {
		this.afterSaleCode = afterSaleCode;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

}
