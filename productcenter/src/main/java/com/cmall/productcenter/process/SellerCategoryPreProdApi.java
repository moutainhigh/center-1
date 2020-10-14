package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.SellerCategoryProductsInput;
import com.cmall.productcenter.model.SellerCategoryPropertiesResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 前台分类-新建子分类维护商品列表
 * @author  lgx
 */
public class SellerCategoryPreProdApi extends RootApi<SellerCategoryPropertiesResult,SellerCategoryProductsInput> {

	public SellerCategoryPropertiesResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {
		SellerCategoryPropertiesResult result = new SellerCategoryPropertiesResult();
		String categoryCode = inputParam.getCategoryCode();// 前台类目编号
		// String sellercode = UserFactory.INSTANCE.create().getManageCode();//店铺编号
		String sql = "SELECT u.*, p.product_name FROM uc_sellercategory_pre_product u LEFT JOIN productcenter.pc_productinfo p ON u.product_code = p.product_code " + 
				"WHERE u.category_code = '"+categoryCode+"'";
		List<Map<String, Object>> propertiesList = DbUp.upTable("uc_sellercategory_pre_product").dataSqlList(sql, new MDataMap());
		List<Map<String, Object>> re =  new ArrayList<Map<String, Object>>();
		if(null != propertiesList && propertiesList.size() > 0){
			re = propertiesList;
		}
		result.setListProperty(re);
		return result;
	}

}

