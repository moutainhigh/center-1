package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 加入购物车 输入类
 * @author yangrong
 * date 2014-9-20
 * @version 1.0
 */
public class AddSkuToShopCarInput extends RootInput {

	@ZapcomApi(value = "购物车添加信息")
	private List<GoodsInfoForAdd>  info = new ArrayList<GoodsInfoForAdd>();

	public List<GoodsInfoForAdd> getInfo() {
		return info;
	}

	public void setInfo(List<GoodsInfoForAdd> info) {
		this.info = info;
	}
	
	
}
