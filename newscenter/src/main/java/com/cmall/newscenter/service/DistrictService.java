package com.cmall.newscenter.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.City;
import com.cmall.newscenter.beauty.model.CityResult;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MObjMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 地区service
 * @author chenxk
 *
 */
public class DistrictService {

	//缓存所有城市，key为当前日期
	private static MObjMap<String,Object> allCityCache = new MObjMap<String,Object>();
	
	private static Map<String,String> defaultCityMap = new HashMap<String,String>();
	static{
		defaultCityMap.put("110000", "北京市");
		defaultCityMap.put("120000", "天津市");
		defaultCityMap.put("310000", "上海市");
		defaultCityMap.put("500000", "重庆市");
	}
	
	public CityResult getAllCitys(){
		CityResult result = null;
		if(null != allCityCache.get(DateHelper.upDate(new Date(), "yyyy-MM-dd"))){
			result = (CityResult) allCityCache.get(DateHelper.upDate(new Date(), "yyyy-MM-dd"));
		}else{
			result = initCityCache();
			if(result.getCity().size() > 0){
				allCityCache.clear();
				allCityCache.put(DateHelper.upDate(new Date(), "yyyy-MM-dd"), result);
			}
		}
		return result;
	}
	private CityResult initCityCache(){
		
		CityResult result = new CityResult();
		List<Map<String, Object>> provincesList = DbUp.upTable("sc_gov_district").dataSqlList("select code,name from sc_gov_district where substring(code,3,4)='0000'", new MDataMap());
		
		for(Map<?, ?> provinceMap : provincesList){
			if(null != provinceMap.get("code") && null == defaultCityMap.get(provinceMap.get("code"))){
				String sql = "select code,name from sc_gov_district where substring(code,1,2)='"+provinceMap.get("code").toString().substring(0, 2)+"' and substring(code,3,4) !='0000' and substring(code,5,2)='00'";
				List<Map<String, Object>> citysList = DbUp.upTable("sc_gov_district").dataSqlList(sql, new MDataMap());
				
				for(Map<?, ?> cityMap : citysList){
					City city = new City();
					
					city.setId(cityMap.get("code").toString());
					city.setName(cityMap.get("name").toString());
					
					result.getCity().add(city);
				}
			}
		}
		if(result.getCity().size() > 0){
			
			for(String key : defaultCityMap.keySet()){
				City city = new City();
				
				city.setId(key);
				city.setName(defaultCityMap.get(key).toString());
				
				result.getCity().add(city);
			}
			Collections.sort(result.getCity());
		}
		return result;
	}
}
