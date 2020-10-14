package com.cmall.groupcenter.homehas.model;

import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestControlGiftVoucher implements IRsyncRequest {

	/**
	 * 操作类型
	 * U：使用 R：还原
	 */
	private String do_type;
	
	/**
	 * 礼金券信息集合
	 */
	private List<RsyncModelGiftVoucher> ljqList;

	public String getDo_type() {
		return do_type;
	}

	public void setDo_type(String do_type) {
		this.do_type = do_type;
	}

	public List<RsyncModelGiftVoucher> getLjqList() {
		return ljqList;
	}

	public void setLjqList(List<RsyncModelGiftVoucher> ljqList) {
		this.ljqList = ljqList;
	}
	
	
}
