/**
 * Project Name:ordercenter
 * File Name:ReturnMoneyApiInput.java
 * Package Name:com.cmall.ordercenter.service
 * Date:2013-9-16下午9:02:53
 * Copyright (c) 2013, hexudonghot@.163.com All Rights Reserved.
 *
 */

package com.cmall.ordercenter.service.goods;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


/**
 * ClassName: ReturnMoneyApiInput <br/>
 * date: 2013-9-16 下午9:25:22 <br/>
 * @author hexd
 * @version 
 * @since JDK 1.6
 */
public class ReturnGoodsApiInput extends RootInput
{
	/**
	 * 买家编号
	 */
	@ZapcomApi(value="买家编号")
	private String buyer_code = "";

	public String getBuyer_code() {
		return buyer_code;
	}

	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}

	

	
	
}
