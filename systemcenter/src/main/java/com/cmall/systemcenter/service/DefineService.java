package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 处理根据sc_define 数据
 * @author fq
 *
 */
public class DefineService {

	
	
	/**
	 * 根据父级编号查询以下定义参数
	 * @param parentCode 
	 * @return
	 */
	public List<MDataMap> getDefineInfoByParentCode(String parentCode) {
		
		List<MDataMap> queryAll = new ArrayList<MDataMap>();
		if(StringUtils.isNotBlank(parentCode)) {
			
			MDataMap param = new MDataMap();
			param.put("parentCode", parentCode);
			queryAll = DbUp.upTable("sc_define").queryAll("*", "", "parent_code=:parentCode", param);
			
		}
		
		return queryAll;
	}
	
	/**
	 * 根据父级code获取子类  name和code的map
	 * @param parentDefineCode
	 * @return Map<String, String> key:定义名称 ；value：定义code；
	 */
	public Map<String, String> getDefineCodeRELName(String parentDefineCode) {
		
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isNotBlank(parentDefineCode)) {
			List<MDataMap> defineInfoByParentCode = this.getDefineInfoByParentCode(parentDefineCode);
			for (MDataMap mDataMap : defineInfoByParentCode) {
				map.put(mDataMap.get("define_name"), mDataMap.get("define_code"));
			}
		}
		
		return map;
		
	}
	
	public List<String> getDefineCodeByParentCode(String parentDefineCode) {
		List<String> codeArr = new ArrayList<String>();
		List<MDataMap> defineInfoByParentCode = this.getDefineInfoByParentCode(parentDefineCode);
		for (MDataMap mDataMap : defineInfoByParentCode) {
			codeArr.add(mDataMap.get("define_code"));
		}
		return codeArr;
	}
	
}
