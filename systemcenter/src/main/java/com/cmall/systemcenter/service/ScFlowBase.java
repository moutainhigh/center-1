package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.List;
import com.cmall.systemcenter.model.ScFlowStatus;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebTemp;

public class ScFlowBase extends BaseClass {
	
	
	/**
	 * 获取定义的值
	 * @param defineCode
	 * @return
	 */
	public static String getDefineNameByCode(String defineCode)
	{
		String ret = "";
		
		try {
			
			ret = WebTemp.upTempDataOne("sc_flowstatus","define_name","define_code",defineCode);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ret;
	}
	
	
	/**
	 * 获取定义的值
	 * @param defineCode
	 * @return
	 */
	public static String getDefineNameByTypeCode(String defineCode)
	{
		String ret = "";
		
		try {
			
			ret = WebTemp.upTempDataOne("sc_flowtype","define_name","define_code",defineCode);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ret;
	}
	
	
	/**
	 * 获取定义的值
	 * @param defineCode
	 * @return
	 */
	public static String getTypeNameByCode(String defineCode)
	{
		String ret = "";
		
		try {
			
			ret = WebTemp.upTempDataOne("sc_flowtype","define_name","define_code",defineCode);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ret;
	}
	

	/**
	 * 获取某个定义的Listt通过父编号
	 * @param type 0 通用审批，1 业务审批
	 * @return
	 */
	public static List<MDataMap> getFlowTypeByType(String type)
	{
		
		try {
			
			List<MDataMap> sfsListMap=
					WebTemp.upTempDataList("sc_flowtype", "define_code,define_name", "", "", "flow_type",type);
			
		
			return sfsListMap;
			
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	
	/**
	 * 获取某个定义的Listt通过父编号
	 * @param typeCode
	 * @return
	 */
	public static List<ScFlowStatus> getFlowStatusListByTypeCode(String typeCode)
	{
		List<ScFlowStatus> ret = new ArrayList<ScFlowStatus>();
		
		try {
			
			List<MDataMap> sfsListMap=
					WebTemp.upTempDataList("sc_flowstatus", "define_code,define_name", "", " define_code in(select define_code from sc_flowstatus_type_rel where type_code=:type_code) ", "type_code",typeCode);
			
			
			if(sfsListMap!=null){
				int size = sfsListMap.size();
				
				
				for(int i=0;i<size;i++)
				{
					ScFlowStatus pic = new ScFlowStatus();
				
					pic.setDefineCode(sfsListMap.get(i).get("define_code"));
					pic.setDefineName(sfsListMap.get(i).get("define_name"));
					ret.add(pic);
				}
			}
			
		} catch (Exception e) {
			
		}
		
		return ret;
	}
	
	/**
	 * zw_define定义信息
	 * @param parentId
	 * 		父类标识
	 * @param defineName
	 * 		定义编码
	 * @return MDataMap
	 */
	public static String getZWDefineNote(String parentId, String defineName){
		
		return  WebTemp.upTempDataOne("zw_define","define_note","define_name",defineName,"parent_did",parentId);
		
	}

}
