package com.cmall.systemcenter.service;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fusesource.hawtbuf.BufferInputStream;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.TempGovDistrict;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 大区管理
 * 
 * @author zhaoshuli
 * 
 */
public class BigAreaService extends BaseClass {

	/**
	 * 区域表
	 */
	private static String BIG_AREA_TABLE="big_area";
	
	/**
	 * 运营支持
	 */
	private static String OPERATIONSUPPORT="operationsupport";
	
	/**
	 * 保存区域
	 * 
	 * @param mdataMap
	 * @return
	 */
	public MWebResult saveBigArea(MDataMap mdataMap) {

		MWebResult ret = new MWebResult();
		ret.setResultCode(1);
		ret.setResultMessage("OK");
		DbUp.upTable(BIG_AREA_TABLE).dataInsert(mdataMap);

		return ret;
	}
	
	
	/**
	 * 保存运营支持和区域的关系
	 * @param mdataMap
	 * @return
	 */
	public MWebResult saveOperateSupport(MDataMap mdataMap) {

		MWebResult ret = new MWebResult();
		ret.setResultCode(1);
		ret.setResultMessage("OK");
		String[] osNames = mdataMap.get("os_name").split(";");
		String[] osCodes = mdataMap.get("os_code").split(";");
		for(int i=0;i<osNames.length;i++){
			mdataMap.put("os_name", osNames[i]);
			mdataMap.put("os_code", osCodes[i]);	
			DbUp.upTable(OPERATIONSUPPORT).dataInsert(mdataMap);
			//删除uid，有的话会重复
			mdataMap.remove("uid");
		}
		
		
//		DbUp.upTable(OPERATIONSUPPORT).dataInsert(mdataMap);
		return ret;
	}
	
	
	/**
	 * 产生大区
	 * @param mdataMap
	 * @return
	 */
	public MWebResult generateBigArea(MDataMap mdataMap) {
		MWebResult ret = new MWebResult();
		ret.setResultCode(1);
		ret.setResultMessage("OK");
		
		 Map<String,List<TempGovDistrict>>  cityMap = getCity();
		
		TempGovDistrict gd = new TempGovDistrict();
		gd.setId("global");
		gd.setText("全国");
		List<TempGovDistrict> list = new ArrayList<TempGovDistrict>();
		List<Map<String, Object>> listObjs = DbUp.upTable(
				BIG_AREA_TABLE).dataSqlList(" select distinct area_code id,area_name text from score.big_area;  ", null);
		
		for(Map<String,Object> m:listObjs){   //大区列表
			TempGovDistrict gd1  = new TempGovDistrict();
			gd1.setId((String)m.get("id"));
			gd1.setText((String)m.get("text"));
			
			
			MDataMap sendMap =  new MDataMap();
			sendMap.put("id", (String)m.get("id"));
			List<Map<String, Object>> listObjs2 = DbUp.upTable(        //省份列表
					BIG_AREA_TABLE).dataSqlList(" SELECT code id, name  text,area_name,area_code FROM score.big_area a , systemcenter.sc_gov_district b where a.province_code like concat('%',b.code,'%') and a.area_code=:id order by area_code ",sendMap);
			
			TempGovDistrict gd2 ;
			for(Map<String,Object> m2:listObjs2){
				gd2  = new TempGovDistrict();
				gd2.setId((String)m2.get("id"));
				gd2.setText((String)m2.get("text"));
				
				
				//children为空，状态设置为""
				List<TempGovDistrict> cityTemp = cityMap.get((String)m2.get("id"));
				
				for(TempGovDistrict city:cityTemp){
						city.setState("");
				}
				
				gd2.setChildren(cityTemp);
				
				gd1.getChildren().add(gd2);
				
			}
			gd.getChildren().add(gd1);
			
		}
		
		JsonHelper<TempGovDistrict> json = new JsonHelper<TempGovDistrict>();
		String result = "callback(["+json.ObjToString(gd)+"])";
		
//		System.out.println(json.ObjToString(gd));
		
		String filepath = TopConst.CONST_TOP_DIR_SERVLET
				+ "/resources/cmanage/json/bigArea.json";
		try {
			OutputStream fos = new FileOutputStream(filepath);
//			FileWriter jsonFile = new FileWriter(filepath);
//			jsonFile.write(result);
			
			byte[] tempbytes = new byte[1024];
			int byteread = 0;
			String a = new String(result.getBytes("utf-8"));
			InputStream in = new BufferInputStream(result.getBytes("utf-8"));
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
			while ((byteread = in.read(tempbytes)) != -1) {
				fos.write(tempbytes, 0, byteread);
			}
			fos.close();
			// for (int b = 0; b < result.length(); b++) {
			// char c = result.charAt(b);
			// jsonFile.write(c);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return ret;
		
	}
	
	
	/**
	 * 返回城市
	 * @return
	 */
	private Map<String,List<TempGovDistrict>> getCity(){
		//市
		List<TempGovDistrict> list2 = new ArrayList<TempGovDistrict>();
		TempGovDistrict gd  = new TempGovDistrict();
		list2 = getChildrendTempGovDistrict(list2, "");
		
		Map<String,List<TempGovDistrict>> map  = new HashMap<String, List<TempGovDistrict>>();
		
		for(TempGovDistrict td:list2){
				map.put(td.getId(), td.getChildren());
			
		}
		
		return map;
		
		
	}
	
	
	
	/**
	 * @param list
	 * @param whereStr
	 * @return
	 */
	private List<TempGovDistrict> getChildrendTempGovDistrict(List<TempGovDistrict> list,
			String whereStr) {
		String queryStr = "";
		if (StringUtils.isNotBlank(whereStr)) {

			if (whereStr.length() == 2) {
				queryStr = " code like '" + whereStr + "%00' and code <> '"
						+ whereStr + "0000'       ";
			} else {
				queryStr = " code like '" + whereStr + "999%' and code <> '"
						+ whereStr + "00'       ";
			}
		} else {
			queryStr = " code like '%0000'  ";
		}

		List<Map<String, Object>> listObjs = DbUp.upTable(
				"sc_gov_district").dataQuery(" name,code ", "", queryStr,
				null, 0, 0);
		TempGovDistrict gd;
		for (Map<String, Object> map : listObjs) {
			gd = new TempGovDistrict();
			gd.setId((String) map.get("code"));
			gd.setText((String) map.get("name"));
			
			
			if (whereStr.length() < 4) {
				
				gd.setChildren(getChildrendTempGovDistrict(gd.getChildren(), gd
						.getId().substring(0, whereStr.length() + 2)));
			}
			list.add(gd);
		}

		return list;
	}
	
	/**
	 * 生成 区域 json
	 */
	public void genalAreaJson(){
		JsonHelper<TempGovDistrict> json = new JsonHelper<TempGovDistrict>();
		TempGovDistrict gd = new TempGovDistrict();
		gd.setId("global");
		gd.setText("全国");
		List<TempGovDistrict> list = new ArrayList<TempGovDistrict>();

		gd.setChildren(getChildrendTempGovDistrict(list, ""));

		String result = "callback(["+json.ObjToString(gd)+"])";

		String filepath = TopConst.CONST_TOP_DIR_SERVLET
				+ "/resources/cmanage/json/address.json";
		try {
			OutputStream fos = new FileOutputStream(filepath);
			FileWriter jsonFile = new FileWriter(filepath);
			byte[] tempbytes = new byte[1024];
			int byteread = 0;

			InputStream in = new BufferInputStream(result.getBytes());
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
			while ((byteread = in.read(tempbytes)) != -1) {
				fos.write(tempbytes, 0, byteread);
			}
			fos.close();
			// for (int b = 0; b < result.length(); b++) {
			// char c = result.charAt(b);
			// jsonFile.write(c);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
