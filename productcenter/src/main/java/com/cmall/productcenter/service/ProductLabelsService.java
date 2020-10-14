package com.cmall.productcenter.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.productcenter.model.LabelProductDTO;
import com.cmall.productcenter.model.ProductBaseInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


/** 
* @ClassName: ProductLabelsService 
* @Description: 
* @author houwen
* @date 2016-5-11 下午2:25:18 
*  
*/
public class ProductLabelsService extends BaseClass {

	/**
	 * 获取标签管理商品列表
	 * 
	 * @return
	 */
	public String getLabelProducts(String keyword) { //标签 code
		LabelProductDTO result = new LabelProductDTO();
		if (StringUtils.isEmpty(keyword)) {
			return "";
		}
	
		List<ProductBaseInfo> productInfoList = new ArrayList<ProductBaseInfo>();
		String productCodes = "";
		MDataMap mwhereMap = new MDataMap();
		mwhereMap.put("keyword", keyword);
		
		List<Map<String, Object>> map = DbUp.upTable("pc_productdescription").dataQuery("product_code", "", "INSTR(keyword,:keyword)",mwhereMap,0,0);
		if (map == null || map.isEmpty() || map.size()==0) {
			return "";
		}
		for (Map<String, Object> map2 : map) {
			productCodes = productCodes + map2.get("product_code") + ",";
		}
		
		productCodes = productCodes.substring(0, productCodes.length()-1);
		result.setProductCodes(productCodes);
		// 商品基本信息
		if (StringUtils.isNotEmpty(productCodes)) {
			String sWhere = "product_code in ('"
					+ productCodes.replace(",", "','") + "')";
			String sFields = "product_code,product_name";
			List<MDataMap> productMap = DbUp.upTable("pc_productinfo")
					.queryAll(sFields, "", sWhere, null);
			for (MDataMap mDataMap : productMap) {
				ProductBaseInfo productInfo = new ProductBaseInfo();
				productInfo.setProductCode(mDataMap.get("product_code"));
				productInfo.setProductName(mDataMap.get("product_name"));
				productInfoList.add(productInfo);
			}
		}
	
		result.setProductInfoList(productInfoList);

		return JSON.toJSONString(result);
	}

}
