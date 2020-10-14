package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * @author hxd
 *
 */
public class BoutiqueResult  extends RootResult{
	List<Boutique> list = new ArrayList<Boutique>();

	public List<Boutique> getList() {
		return list;
	}

	public void setList(List<Boutique> list) {
		this.list = list;
	}
	
	
}
