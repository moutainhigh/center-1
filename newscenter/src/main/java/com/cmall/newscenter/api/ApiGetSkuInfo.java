package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.cmall.newscenter.model.GetSkuInfoInput;
import com.cmall.newscenter.model.GetSkuInfoResult;
import com.cmall.newscenter.model.Productinfo;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 商品详情
 * 
 * @author syz
 */
public class ApiGetSkuInfo extends
		RootApiForMember<GetSkuInfoResult, GetSkuInfoInput> {

	public GetSkuInfoResult Process(GetSkuInfoInput api,
			MDataMap mRequestMap) {
		
		GetSkuInfoResult result = new GetSkuInfoResult();
		
		String sellerCode = bConfig("newscenter.app_code"); // 获取appCode
		
		ProductService productService = new ProductService();
		
		List<PcProductinfo> productSkuInfoList = productService.getSellProducts(sellerCode, "",api.getProductCode());
		
		//关联商品其它信息
		FuncQueryProductInfo queryProduct = new FuncQueryProductInfo();
		
		String userCode = "";
		if(getFlagLogin()){
			userCode = getOauthInfo().getUserCode();
		
		}
		
		Productinfo sale_product = new Productinfo();
		
		sale_product = queryProduct.queryProductIn(productSkuInfoList, userCode,sellerCode).get(0);
		
		result.setProducts(sale_product);
		
		return result;
	}
}
