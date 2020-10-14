package com.cmall.productcenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class BoutiqueInput  extends RootInput{
	/**
	 * 精品汇Code
	 */
	@ZapcomApi(value="精品汇Code")
	private List<String> boutique_code = new ArrayList<String>();

	public List<String> getBoutique_code() {
		return boutique_code;
	}

	public void setBoutique_code(List<String> boutique_code) {
		this.boutique_code = boutique_code;
	}

	
	
}
