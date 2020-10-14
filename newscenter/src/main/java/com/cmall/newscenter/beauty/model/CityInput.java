package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-获取城市输入类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class CityInput extends RootInput {

	@ZapcomApi(value="省id",demo="110000",require=1)
	private String id = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
