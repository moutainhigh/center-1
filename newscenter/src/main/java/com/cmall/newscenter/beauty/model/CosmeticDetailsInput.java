package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽—妆品详情输入类
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class CosmeticDetailsInput extends RootInput {

	@ZapcomApi(value = "妆品编码",require=1)
	private String cosmetic_code = "";

	public String getCosmetic_code() {
		return cosmetic_code;
	}

	public void setCosmetic_code(String cosmetic_code) {
		this.cosmetic_code = cosmetic_code;
	}
	
	
	
}
