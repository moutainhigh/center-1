package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.ProductInfoForCC;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 商品信息   
 * @author zhaoxq
 * @version 1.0
 */
public class ApiGetProductInfoForCCResult extends RootResult{
	
	@ZapcomApi(value="商品结果list")
	private List<ProductInfoForCC> list = null;

	public List<ProductInfoForCC> getList() {
		return list;
	}

	public void setList(List<ProductInfoForCC> list) {
		this.list = list;
	}

}
