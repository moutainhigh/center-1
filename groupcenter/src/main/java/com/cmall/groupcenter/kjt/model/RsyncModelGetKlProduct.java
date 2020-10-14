package com.cmall.groupcenter.kjt.model;

import java.io.Serializable;

public class RsyncModelGetKlProduct implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer recCode=0;
	
	private String recMsg="";
	
	public Integer getRecCode() {
		return recCode;
	}

	public void setRecCode(Integer recCode) {
		this.recCode = recCode;
	}

	public String getRecMsg() {
		return recMsg;
	}

	public void setRecMsg(String recMsg) {
		this.recMsg = recMsg;
	}

	public KlProModel getGoodsInfo() {
		return goodsInfo;
	}

	public void setGoodsInfo(KlProModel goodsInfo) {
		this.goodsInfo = goodsInfo;
	}

	private KlProModel goodsInfo = new KlProModel();
	
	
}
