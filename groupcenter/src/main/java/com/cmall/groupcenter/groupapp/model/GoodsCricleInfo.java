package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GoodsCricleInfo  {

	@ZapcomApi(value = "分享信息")
	private ShareModel shareModel;
	
	@ZapcomApi(value = "商品信息")
	private GoodsInfo goodsInfo;
	
	@ZapcomApi(value = "倒计时剩余时间",demo="差值（毫秒）")
	private long offsetTime;
	
	

	@ZapcomApi(value = "分享人数")
	private int shareCount;

	public int getShareCount() {
		return shareCount;
	}

	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}

	public ShareModel getShareModel() {
		return shareModel;
	}

	public void setShareModel(ShareModel shareModel) {
		this.shareModel = shareModel;
	}

	public GoodsInfo getGoodsInfo() {
		return goodsInfo;
	}

	public void setGoodsInfo(GoodsInfo goodsInfo) {
		this.goodsInfo = goodsInfo;
	}

	public long getOffsetTime() {
		return offsetTime;
	}

	public void setOffsetTime(long offsetTime) {
		this.offsetTime = offsetTime;
	}
	
	
	
	
	

}
