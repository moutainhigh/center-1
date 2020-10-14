/**
 * Project Name:ordercenter
 * File Name:ReturnMoneyApiResult.java
 * Package Name:com.cmall.ordercenter.service
 * Date:2013-9-16下午9:02:26
 * Copyright (c) 2013, hexudonghot@.163.com All Rights Reserved.
 *
 */
package com.cmall.ordercenter.service.goods;
import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.ReturnGoods;
import com.srnpr.zapcom.topapi.RootResult;
/**
 * ClassName: ReturnMoneyApiResult <br/>
 * date: 2013-9-16 下午9:24:31 <br/>
 * @author hexd
 * @version 
 * @since JDK 1.6
 */
public class ReturnGoodsApiResult extends RootResult
{
	private List<ReturnGoods> goods = new ArrayList<ReturnGoods>();

	public List<ReturnGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<ReturnGoods> goods) {
		this.goods = goods;
	}
}
