package com.cmall.newscenter.group.model;


import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 帖子列表类输出
 * @version 1.0
 */
public class PostsList {
	
	@ZapcomApi(value="帖子ID")
	private String pId = "";
	
	@ZapcomApi(value="标题")
	private String pTitle = "" ;
	
	@ZapcomApi(value="导语")
	private String pIntroduction;
	
	@ZapcomApi(value="发布时间")
	private String publishTime = "";
	
	@ZapcomApi(value="最后更新时间")
	private String lastUpdateTime = "";
	
	@ZapcomApi(value="帖子封面图",remark="图片的url")
	private String listImgUrl  = "";
	
	@ZapcomApi(value="微信帖子封面大图",remark="图片的url")
	private String wxListBigImgUrl  = "";
	
	@ZapcomApi(value="微信帖子封面小图",remark="图片的url")
	private String wxListSmallImgUrl  = "";
	
	@ZapcomApi(value="帖子封面小图",remark="图片的url")
	private String smallListImgUrl  = "";
	
	@ZapcomApi(value="分享数",remark="实际分享数+分享增加数")
	private String shareNum  = "";
	
	@ZapcomApi(value="返利金额")
	private String rebateAmount = "";
	
	@ZapcomApi(value="商品数量 ")
	private String productNum = "";
	
	@ZapcomApi(value="帖子时间标签",remark="0代表当天即新鲜，1为昨天，2是当年的 long ago,3为long long ago")
	private String timeLable = "0";
	
	
	@ZapcomApi(value = "分享数据")
	private ShareModel shareModel;

	public ShareModel getShareModel() {
		return shareModel;
	}

	public void setShareModel(ShareModel shareModel) {
		this.shareModel = shareModel;
	}
	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getpTitle() {
		return pTitle;
	}

	public void setpTitle(String pTitle) {
		this.pTitle = pTitle;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getListImgUrl() {
		return listImgUrl;
	}

	public void setListImgUrl(String listImgUrl) {
		this.listImgUrl = listImgUrl;
	}

	public String getShareNum() {
		return shareNum;
	}

	public void setShareNum(String shareNum) {
		this.shareNum = shareNum;
	}

	public String getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	public String getProductNum() {
		return productNum;
	}

	public void setProductNum(String productNum) {
		this.productNum = productNum;
	}

	public String getTimeLable() {
		return timeLable;
	}

	public void setTimeLable(String timeLable) {
		this.timeLable = timeLable;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getSmallListImgUrl() {
		return smallListImgUrl;
	}

	public void setSmallListImgUrl(String smallListImgUrl) {
		this.smallListImgUrl = smallListImgUrl;
	}

	public String getpIntroduction() {
		return pIntroduction;
	}

	public void setpIntroduction(String pIntroduction) {
		this.pIntroduction = pIntroduction;
	}

	public String getWxListBigImgUrl() {
		return wxListBigImgUrl;
	}

	public void setWxListBigImgUrl(String wxListBigImgUrl) {
		this.wxListBigImgUrl = wxListBigImgUrl;
	}

	public String getWxListSmallImgUrl() {
		return wxListSmallImgUrl;
	}

	public void setWxListSmallImgUrl(String wxListSmallImgUrl) {
		this.wxListSmallImgUrl = wxListSmallImgUrl;
	}


}
