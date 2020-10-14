package com.cmall.productcenter.model.api;


import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiOperateInput extends RootInput {
	
	/**
	 * 商品基本信息
	 */
	private PcProductinfo ppi = new PcProductinfo();
	
	/**
	 * 操作 商品
	 * @param product
	 * @param type
	 * 1 创建
	 * 2 更新sku库存
	 * 3更新sku价格
	 * 4下架商品
	 * 5下架sku
	 * 6更新商品图片
	 * 7更新市场价格。
	 * 8修改sku名字
	 * @return
	 */
	private int type = 0;

	public PcProductinfo getPpi() {
		return ppi;
	}

	public void setPpi(PcProductinfo ppi) {
		this.ppi = ppi;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
