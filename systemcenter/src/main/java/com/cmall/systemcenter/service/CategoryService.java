package com.cmall.systemcenter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 分类管理
 * 
 * @author zhaoshuli
 * 
 */
public class CategoryService extends BaseClass {
	
	/**
	 * 获取分类名称
	 * 
	 * @param categoryCodeArr
	 * @return
	 */
	public MDataMap getCategoryName(List<String> categoryCodeArr,String sellerCode) {
		MDataMap resultMap = new MDataMap();
		if (null == categoryCodeArr || categoryCodeArr.size() < 1) {
			return resultMap;
		}
		String sFields = "category_code,category_name";
		String sWhere = " seller_code='"+sellerCode+"' and category_code in ('"
				+ StringUtils.join(categoryCodeArr, "','") + "')";
		List<MDataMap> categoryNameMapList = DbUp.upTable("uc_sellercategory")
				.queryAll(sFields, "", sWhere, null);
		for (MDataMap categoryNameMap : categoryNameMapList) {
			String categoryCode = categoryNameMap.get("category_code");
			String categoryName = categoryNameMap.get("category_name");
			resultMap.put(categoryCode, categoryName);
		}
		return resultMap;
	}
	
	public MDataMap getCategoryFullInfo(String categoryCodes) {
		MDataMap ret = new MDataMap();
		Map<String, MDataMap> categoryObjMap = new HashMap<String, MDataMap>();

		List<MDataMap> listCategory = DbUp.upTable("uc_sellercategory")
				.queryAll("", "", " seller_code= 'SI2003' ",
						new MDataMap());
		for (MDataMap mDataMap : listCategory) {
			categoryObjMap.put(mDataMap.get("category_code"), mDataMap);
		}
		
		for (String categoryCode : categoryCodes.split(",")) {
			MDataMap categoryMap = categoryObjMap.get(categoryCode);
			if (categoryMap != null && !categoryMap.isEmpty()) {// 防止变态的修改数据库
				String name = "";
				if ("3".equals(categoryMap.get("level"))) {
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap
							.get("parent_code")); // 上一级别的分类信息
					name = parentCategoryMap.get("category_name") + "->"
							+ categoryMap.get("category_name");
				} else if ("4".equals(categoryMap.get("level"))) {
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap
							.get("parent_code")); // 上一级别的分类信息
					MDataMap superParentCategoryMap = categoryObjMap
							.get(parentCategoryMap.get("parent_code")); // 第一级别的分类信息
					name = superParentCategoryMap.get("category_name") + "->"
							+ parentCategoryMap.get("category_name") + "->"
							+ categoryMap.get("category_name");
				} else {
					name = categoryMap.get("category_name");
				}
				ret.put(categoryCode, name);
			}
		}
		return ret;
	}
}
