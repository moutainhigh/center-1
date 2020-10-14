package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AddProductShareNumInput extends RootInput{
	
	@ZapcomApi(value = "分享类型",require=1,demo="qq:4497472000100001、qq空间：4497472000100002、微信：4497472000100003、微信朋友圈：4497472000100004、微公社：4497472000100005、新浪微博： 4497472000100006 、短信：44974720001000027",verify="in=4497472000100001,4497472000100002,4497472000100003,4497472000100004,4497472000100005,4497472000100006,44974720001000027",remark="qq:4497472000100001、qq空间：4497472000100002、微信：4497472000100003、微信朋友圈：4497472000100004、微公社：4497472000100005、新浪微博： 4497472000100006 、短信：44974720001000027")
	private String shareType= "";
	
	@ZapcomApi(value = "商品编号",require=1)
	private String prodctCode="";

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public String getProdctCode() {
		return prodctCode;
	}

	public void setProdctCode(String prodctCode) {
		this.prodctCode = prodctCode;
	}
	
 
	
	
	
    

	
	
	
}
