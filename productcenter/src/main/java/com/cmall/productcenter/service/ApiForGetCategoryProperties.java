package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.productcenter.model.ApiForGetCategoryPropertiesInput;
import com.cmall.productcenter.model.ApiForGetCategoryPropertiesResult;
import com.cmall.productcenter.model.CategoryProperties;
import com.cmall.productcenter.model.PropertiesValue;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据分类查询分类属性信息
 * @author lgx
 *
 */
public class ApiForGetCategoryProperties extends RootApi<ApiForGetCategoryPropertiesResult, ApiForGetCategoryPropertiesInput> {

	@Override
	public ApiForGetCategoryPropertiesResult Process(ApiForGetCategoryPropertiesInput inputParam,
			MDataMap mRequestMap) {
		ApiForGetCategoryPropertiesResult result = new ApiForGetCategoryPropertiesResult();
		
		List<CategoryProperties> cpList = new ArrayList<CategoryProperties>();
		
		String categoryCode = inputParam.getCategoryCode();
		if(StringUtils.isNotEmpty(categoryCode)) {
			String thirdCode = categoryCode;
			String sql1 = "SELECT s.* FROM uc_sellercategory s WHERE s.seller_code = 'SI2003' AND s.category_code = " + 
					"(SELECT u.parent_code FROM uc_sellercategory u WHERE u.category_code = '"+thirdCode+"' AND u.seller_code = 'SI2003')"; 
			Map<String, Object> parentcategory = DbUp.upTable("uc_sellercategory").dataSqlOne(sql1, new MDataMap());
			if(null != parentcategory) {
				String secondCode = MapUtils.getString(parentcategory, "category_code");
				String firstCode = MapUtils.getString(parentcategory, "parent_code");
				
				// 一级分类
				cpList = getCategoryProperties(cpList, firstCode);
				// 二级分类
				cpList = getCategoryProperties(cpList, secondCode);
				// 三级分类
				cpList = getCategoryProperties(cpList, thirdCode);
			}
		}
		
		result.setCpList(cpList);
		
		return result;
	}

	public List<CategoryProperties> getCategoryProperties(List<CategoryProperties> list, String categoryCode){
		// 查询分类属性
		String sql2 = "SELECT k.* FROM uc_sellercategory_properties p LEFT JOIN uc_properties_key k ON p.properties_code = k.properties_code " + 
				"WHERE p.category_code = '"+categoryCode+"' AND k.is_delete = '0' ORDER BY sort_num ASC ";
		List<Map<String, Object>> firstList = DbUp.upTable("uc_sellercategory_properties").dataSqlList(sql2 , new MDataMap());
		if(firstList != null && firstList.size() > 0) {
			for (Map<String, Object> map : firstList) {
				CategoryProperties cp = new CategoryProperties();
				String properties_code = MapUtils.getString(map, "properties_code");
				// 验证list中是否已经包含该属性(去重)
				boolean includeFlag = false;
				for (CategoryProperties categoryProperties : list) {
					if(properties_code.equals(categoryProperties.getProperties_code())){
						includeFlag = true;
						break;
					}
				}
				if(includeFlag) {
					continue;
				}
				
				String properties_value_type = MapUtils.getString(map, "properties_value_type");
				cp.setIs_must(MapUtils.getString(map, "is_must"));
				cp.setProperties_code(properties_code);
				cp.setProperties_name(MapUtils.getString(map, "properties_name"));
				cp.setProperties_value_type(properties_value_type);
				cp.setProperties_value_code("");
				cp.setProperties_value("");
				// 如果是固定值,查询属性值列表
				if("449748500001".equals(properties_value_type)) {
					List<PropertiesValue> pvList = new ArrayList<PropertiesValue>();
					List<Map<String, Object>> pvMapList = DbUp.upTable("uc_properties_value").dataSqlList("SELECT * FROM uc_properties_value WHERE properties_code = '"+properties_code+"' AND is_delete = '0' ORDER BY sort_num ASC", new MDataMap());
					if(pvMapList != null && pvMapList.size() > 0) {
						for (Map<String, Object> map2 : pvMapList) {
							PropertiesValue propertiesValue = new PropertiesValue();
							propertiesValue.setProperties_value_code(MapUtils.getString(map2, "properties_value_code"));
							propertiesValue.setProperties_value(MapUtils.getString(map2, "properties_value"));
							pvList.add(propertiesValue);
						}
					}
					
					cp.setList(pvList);
				}
				
				list.add(cp);
			}
		}
		
		return list;
	}
	
}
