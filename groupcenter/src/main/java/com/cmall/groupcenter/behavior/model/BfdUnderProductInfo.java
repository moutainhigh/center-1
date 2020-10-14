package com.cmall.groupcenter.behavior.model;

/**
 * 百分点下架商品信息
 * @author pang_jhui
 *
 */
public class BfdUnderProductInfo {
	
	/*商品编号*/
	private String iid = "";
	
	
	

	public BfdUnderProductInfo(String iid) {
		super();
		this.iid = iid;
	}

	/**
	 * 获取商品编号
	 * @return
	 */
	public String getIid() {
		return iid;
	}

	/**
	 * 设置商品编号
	 * @param iid
	 */
	public void setIid(String iid) {
		this.iid = iid;
	}

}
