package com.cmall.groupcenter.behavior.request;

import com.cmall.groupcenter.behavior.face.IBfdRequest;

/**
 * 百分点请求信息
 * @author pang_jhui
 *
 */
public class BfdRecResultRequest implements IBfdRequest {
	
	/*推荐栏请求id*/
	private String req = "";
	
	/*用户唯一标识*/
	private String uid = "";
	
	/*返回结果的格式*/
	private String fmt = "";
	
	/*百分点对于用户标识*/
	private String gid = "";
	
	
	/*推荐栏列表，顺序与req字段保持一致*/
	private String bidlst = "";
	
	/*session key*/
	private String ssk = "";
	/*当前软件的唯一标示,wangqx 2016年3月31日被强制要求添加*/
	private String appkey = "";
	
	/*用户当前浏览商品Id*/
	private String iid = "";
	
	/*分类 同级用,分割 父级用|分割*/
	private String cat = "";

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFmt() {
		return fmt;
	}

	public void setFmt(String fmt) {
		this.fmt = fmt;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getBidlst() {
		return bidlst;
	}

	public void setBidlst(String bidlst) {
		this.bidlst = bidlst;
	}

	public String getSsk() {
		return ssk;
	}

	public void setSsk(String ssk) {
		this.ssk = ssk;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getIid() {
		return iid;
	}

	public void setIid(String iid) {
		this.iid = iid;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}
	

}
