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
 * 后台商品分类属性
 * @author  lgx
 */
public class SellerCategoryPropertiesApi extends RootApi<SellerCategoryPropertiesResult,SellerCategoryProductsInput> {

	public SellerCategoryPropertiesResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {
		SellerCategoryPropertiesResult result = new SellerCategoryPropertiesResult();
		String categoryCode = inputParam.getCategoryCode();// 私有类目编号
		// String sellercode = UserFactory.INSTANCE.create().getManageCode();//店铺编号
		String sql = "SELECT sp.sort_num, pk.* FROM uc_sellercategory_properties sp LEFT JOIN uc_properties_key pk ON sp.properties_code = pk.properties_code " + 
				"WHERE sp.category_code = '"+categoryCode+"' AND pk.is_delete = '0' ORDER BY sp.sort_num ASC";
		List<Map<String, Object>> propertiesList = DbUp.upTable("uc_sellercategory_properties").dataSqlList(sql, new MDataMap());
		List<Map<String, Object>> re =  new ArrayList<Map<String, Object>>();
		if(null != propertiesList && propertiesList.size() > 0){
			re = propertiesList;
		}
		result.setListProperty(re);
		return result;
	}

}

