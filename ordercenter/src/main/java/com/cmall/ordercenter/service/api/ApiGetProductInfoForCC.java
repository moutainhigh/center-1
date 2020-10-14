package com.cmall.ordercenter.service.api;

import java.math.BigDecimal;
import java.util.List;

import com.cmall.ordercenter.model.ProductInfoForCC;
import com.cmall.ordercenter.model.api.ApiGetProductInfoForCCInput;
import com.cmall.ordercenter.model.api.ApiGetProductInfoForCCResult;
import com.cmall.ordercenter.service.OrderInfoServiceForCC;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;


/**
 * 客服系统商品信息查询 
 * 
 * @author zhaoxq
 */
public class ApiGetProductInfoForCC extends
		RootApiForMember<ApiGetProductInfoForCCResult, ApiGetProductInfoForCCInput> {

	public ApiGetProductInfoForCCResult Process(ApiGetProductInfoForCCInput api,
			MDataMap mRequestMap) {
		ApiGetProductInfoForCCResult result = new ApiGetProductInfoForCCResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
			return result;
		}
		//商品编码
		String productCode=api.getProductCode();
		//商品名称
		String productName=api.getProductName();
		//商品SKU编码
		String skuCode=api.getSkuCode();
		//商品状态
		String productStatus=api.getProductStatus();
		//商户编号
		String sellerCode=api.getSellerCode();
		//商户名称
		String sellerName = api.getSellerName();
		//商品类型
		String validateFlag = api.getValidateFlag();
		//商品分类
		String productCategory = api.getProductCategory();
		//商品品牌
		String brandCode = api.getBrandCode();
		//商品关键词
		String labels = api.getLabels();
		//商品标签
		String keyword = api.getKeyword();
		//商品价格区间最小
		BigDecimal minSellPrice = api.getMinSellPrice();
		//商品价格区间最大
		BigDecimal maxSellPrice = api.getMaxSellPrice();
		
		OrderInfoServiceForCC os = new OrderInfoServiceForCC();			
		try {			
			List<ProductInfoForCC> list = os.getProductInfoForCC(productCode,productName,
					skuCode,productStatus,sellerCode,sellerName,validateFlag,
					productCategory,brandCode,labels,keyword,minSellPrice,maxSellPrice);
			result.setList(list);
			result.setResultCode(1);
			
		} catch (Exception e) {
			result.setResultCode(939301033);
			result.setResultMessage(bInfo(939301033));
			e.printStackTrace();
		}
		return result;
	}
}
