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
 * 前台分类-新建子分类维护属性值列表
 * @author  lgx
 */
public class SellerCategoryPrePropertiesApi extends RootApi<SellerCategoryPropertiesResult,SellerCategoryProductsInput> {

	public SellerCategoryPropertiesResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {
		SellerCategoryPropertiesResult result = new SellerCategoryPropertiesResult();
		String categoryCode = inputParam.getCategoryCode();// 前台类目编号
		// String sellercode = UserFactory.INSTANCE.create().getManageCode();//店铺编号
		/*String sql = "SELECT u.uid, u.category_code, v.properties_value_code, v.properties_value, k.properties_name FROM uc_sellercategory_pre_properties_value u " + 
				"LEFT JOIN uc_properties_value v ON u.properties_value_code = v.properties_value_code " + 
				"LEFT JOIN uc_properties_key k ON u.properties_code = k.properties_code " + 
				"WHERE u.category_code = '"+categoryCode+"'";*/
		String sql = "SELECT s.uid sppv_uid, v.* FROM uc_sellercategory_pre_properties_value s " + 
				" LEFT JOIN v_uc_properties_value v ON s.properties_value_code = v.properties_value_code " + 
				" WHERE s.category_code = '"+categoryCode+"'";
		List<Map<String, Object>> propertiesList = DbUp.upTable("uc_sellercategory_pre_properties_value").dataSqlList(sql, new MDataMap());
		List<Map<String, Object>> re =  new ArrayList<Map<String, Object>>();
		if(null != propertiesList && propertiesList.size() > 0){
			re = propertiesList;
		}
		result.setListProperty(re);
		return result;
	}

}

