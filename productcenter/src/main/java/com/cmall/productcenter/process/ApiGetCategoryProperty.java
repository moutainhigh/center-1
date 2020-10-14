package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.MApiCategoryInput;
import com.cmall.productcenter.model.MApiCategoryResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetCategoryProperty extends
		RootApi<MApiCategoryResult, MApiCategoryInput> {

	public MApiCategoryResult Process(MApiCategoryInput inputParam, MDataMap mRequestMap) {
		MApiCategoryResult mResult = new MApiCategoryResult();

		String sCategoryCodeString =inputParam.getCategoryCode();

		if (StringUtils.isNotEmpty(sCategoryCodeString)) {

			List<String> listProp = new ArrayList<String>();

			for (MDataMap mCatMap : DbUp.upTable("pc_categoryproperty_rel")
					.queryByWhere("category_code", sCategoryCodeString)) {

				listProp.add(mCatMap.get("property_code"));

			}

			if (listProp.size() > 0) {

				String sClist = StringUtils.join(listProp, ",");
				mResult.setListProperty(DbUp
						.upTable("pc_propertyinfo")
						.queryAll(
								"property_code,property_name,parent_code,flag_main,flag_color,show_type_did",
								"property_code",
								"property_code in (" + sClist
										+ ") or parent_code in(" + sClist + ")",
								null));

			}

		}

		return mResult;
	}
}
