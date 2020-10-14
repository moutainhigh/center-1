package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.cmall.productcenter.model.MApiCategoryForSkuInput;
import com.cmall.productcenter.model.MApiCategoryResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetCategoryPropertyForSku extends
		RootApi<MApiCategoryResult, MApiCategoryForSkuInput> {

	public MApiCategoryResult Process(MApiCategoryForSkuInput inputParam,
			MDataMap mRequestMap) {
		MApiCategoryResult mResult = new MApiCategoryResult();

		String sCategoryCodeString = inputParam.getCategoryCode();

		if (StringUtils.isNotEmpty(sCategoryCodeString)) {
			List<String> listProp = new ArrayList<String>();
//			MDataMap map = new MDataMap();
//			map.put("product_code", inputParam.getProductCode());
//			List<Map<String, Object>> list = DbUp
//					.upTable("pc_productproperty")
//					.dataQuery(
//							"DISTINCT property_keycode ",
//							"",
//							"property_type in ('449736200001','449736200002') and product_code = :product_code",
//							map, 0, 0);
//			for (int i = 0; i < list.size(); i++) {
//				if (list.get(i).get("property_keycode") != null
//						&& !"".equals(list.get(i).get("property_keycode")
//								.toString())) {
//					listProp.add(list.get(i).get("property_keycode").toString());
//				}
//			}
			if (listProp.size() == 0) {// 此商品下无sku的属性时
				for (MDataMap mCatMap : DbUp.upTable("pc_categoryproperty_rel")
						.queryByWhere("category_code", sCategoryCodeString)) {
					listProp.add(mCatMap.get("property_code"));
				}
			}
			if (listProp.size() > 0) {
				String sClist = "'" + StringUtils.join(listProp, "','") + "'";
				mResult.setListProperty(DbUp
						.upTable("pc_propertyinfo")
						.queryAll(
								"property_code,property_name,parent_code,flag_main,flag_color,show_type_did",
								"property_code",
								"property_code in (" + sClist
										+ ") or parent_code in(" + sClist + ")",
								null));
/*
				// 兼容历史数据 只存在pc_productproperty 表 不存在pc_propertyinfo中
				if (mResult.getListProperty() == null
						|| mResult.getListProperty().size() == 0) {

					MDataMap mQueryMap = new MDataMap();
					mQueryMap.inAllValues("product_code",
							inputParam.getProductCode());

					List<MDataMap> listMaps = DbUp
							.upTable("pc_productproperty").queryIn(
									"distinct property_code,property_value",
									"", "", mQueryMap, 0, 0,
									"property_keycode", "");
					
					for(MDataMap mPropMap:listMaps)
					{
						
						
						
					}
					
					
					
					
					
				}*/

			}
		}
		return mResult;
	}
}
