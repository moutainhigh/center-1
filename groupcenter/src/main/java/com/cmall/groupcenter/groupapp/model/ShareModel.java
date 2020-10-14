package com.cmall.groupcenter.groupapp.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 微公社app账户实体类
 * @author fengl
 * date 2015-11-6
 * @version 2.0
 */
public class ShareModel {

	@ZapcomApi(value = "分享链接")
	private String shareUrl;
	
	@ZapcomApi(value = "分享标题")
	private String shareTitle;
	
	@ZapcomApi(value = "分享图片",remark = "小分辨率图片")
	private String sharePicUrl;
	
	@ZapcomApi(value = "分享内容")
	private String shareContent;
	
	@ZapcomApi(value = "分享大图集合")
	private List<ShareBigPicUrlModel> shareBigPicUrlList;
	
	

	public List<ShareBigPicUrlModel> getShareBigPicUrlList() {
		return shareBigPicUrlList;
	}

	public void setShareBigPicUrlList(List<ShareBigPicUrlModel> shareBigPicUrlList) {
		this.shareBigPicUrlList = shareBigPicUrlList;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getShareTitle() {
		return shareTitle;
	}

	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	public String getSharePicUrl() {
		return sharePicUrl;
	}

	public void setSharePicUrl(String sharePicUrl) {
		this.sharePicUrl = sharePicUrl;
	}

	public String getShareContent() {
		return shareContent;
	}

	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}
	
   
}
