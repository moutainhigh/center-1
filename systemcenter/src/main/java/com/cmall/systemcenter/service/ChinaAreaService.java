package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.model.ChinaAreaInput;
import com.cmall.systemcenter.model.ChinaAreaResult;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

@SuppressWarnings("all")
public class ChinaAreaService extends RootApi<ChinaAreaResult, ChinaAreaInput> {
	/**
	 * 获取所有的省市code
	 * @return
	 */
	public List<Map<String, Object>> getProvince() {
		List<Map<String, Object>> map = new ArrayList<Map<String,Object>>();
		map = DbUp.upTable("sc_tmp").dataSqlList("SELECT code,name FROM `sc_tmp` WHERE code_lvl = 1 ORDER BY `code` ASC", new MDataMap());
		return map;
	}
	
	/**
	 * 根据省市的code获取市的code
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> getCity(String code) {
		Map<String, String> fullMap = getFullCode(code);
		List<Map<String, Object>> map = new ArrayList<Map<String,Object>>();
		if(fullMap.containsKey("lv1")) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("code", fullMap.get("lv1"));
			map = DbUp.upTable("sc_tmp").dataSqlList("SELECT code,name FROM `sc_tmp` WHERE p_code = :code ORDER BY `code` ASC", mDataMap);
		}
		return map;
	}
	
	/**
	 * 根据市的code获得所在的省的code
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> getProvice(String code) {
		Map<String, String> fullMap = getFullCode(code);
		List<Map<String, Object>> map = new ArrayList<Map<String,Object>>();
		if(fullMap.containsKey("lv1")) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("code", fullMap.get("lv1"));
			map = DbUp.upTable("sc_tmp").dataSqlList("select code,name from sc_tmp where code=:code", mDataMap);
		}
		return map;
	}
	
	/**
	 * 根据市的code获取县的信息
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> getCounty(String code) {
		Map<String, String> fullMap = getFullCode(code);
		List<Map<String, Object>> map = new ArrayList<Map<String,Object>>();
		if(fullMap.containsKey("lv2")) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("code", fullMap.get("lv2"));
			map = DbUp.upTable("sc_tmp").dataSqlList("SELECT code,name FROM `sc_tmp` WHERE p_code = :code ORDER BY `code` ASC", mDataMap);
		}
		return map;
	}
	
	/**
	 * 根据3级或者4级编码获取省和市编码
	 * @param code
	 * @return
	 */
	public Map<String, String> getFullCode(String code) {
		Map<String, String> result = new HashMap<String, String>();
		
		MDataMap m = DbUp.upTable("sc_tmp").one("code", code);
		if(m == null) {
			return result;
		}
		
		result.put("lv"+m.get("code_lvl"), code);
		code = m.get("p_code");
		m = null;
		
		// 取上一级编码  第三级
		if(StringUtils.isNotBlank(code)) {
			m = DbUp.upTable("sc_tmp").one("code", code);
			code = "";
			
			if(m != null) {
				result.put("lv"+m.get("code_lvl"), m.get("code"));
				code = m.get("p_code");
				m = null;
			}
		}
		
		// 取上一级编码  第二级
		if(StringUtils.isNotBlank(code)) {
			m = DbUp.upTable("sc_tmp").one("code", code);
			code = "";
			
			if(m != null) {
				result.put("lv"+m.get("code_lvl"), m.get("code"));
				code = m.get("p_code");
				m = null;
			}
		}
		
		// 取上一级编码 第一级
		if(StringUtils.isNotBlank(code)) {
			m = DbUp.upTable("sc_tmp").one("code", code);
			code = "";
			
			if(m != null) {
				result.put("lv"+m.get("code_lvl"), m.get("code"));
				code = m.get("p_code");
				m = null;
			}
		}
		
		return result;
	}
	
	public ChinaAreaResult Process(ChinaAreaInput inputParam, MDataMap mRequestMap) {
		ChinaAreaResult result = new ChinaAreaResult();
		if("county".equals(inputParam.getArea())) {
			result.setList(getCounty(inputParam.getCode()));
		} else if("city".equals(inputParam.getArea())) {
			result.setList(getCity(inputParam.getCode()));
		} else if("province".equals(inputParam.getArea())) {
			result.setList(getProvice(inputParam.getCode()));
		}
		return result;
	} 
}
