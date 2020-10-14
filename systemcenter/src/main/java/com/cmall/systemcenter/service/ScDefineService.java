package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.model.ScDefine;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MKvdList;
import com.srnpr.zapcom.basemodel.MKvdModel;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebSource;

/**   
*    
* 项目名称：systemcenter   
* 类名称：ScDefineService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-22 下午2:07:55   
* 修改人：yanzj
* 修改时间：2013-9-22 下午2:07:55   
* 修改备注：   
* @version    
*    
*/
public class ScDefineService extends BaseClass {
	
	/**
	 * 获取数据源
	 * 
	 * @param mField
	 * @return
	 */
	public static List<MKvdModel> getDataModel(String tableName,String filedName,String textName,String parentName,String parentValue) {

		MKvdList mReturnList = new MKvdList();

		
		MDataMap mWhereMap = new MDataMap();
		String sWhereString = "";

		mWhereMap.put(parentName, parentValue);
		

		for (MDataMap mDataMap : DbUp.upTable(tableName)
				.queryAll(textName + " as fieldText, "
								+ filedName + " as fieldValue",
						"", parentName+"=:"+parentName, mWhereMap)) {
			mReturnList.inElement(mDataMap.get("fieldText"),
					mDataMap.get("fieldValue"));

		}

		return mReturnList.getChildList();
	}
	
	
	/**
	 * 获取某个定义的Listt通过父编号
	 * @param parentCode
	 * @return
	 */
	public static List<MDataMap> getDefineListByParentCode(String parentCode)
	{
		
		try {
			
			List<MDataMap> sfsListMap=
					WebTemp.upTempDataList("sc_define", "define_code,define_name", "", "", "parent_code",parentCode);
			
		
			return sfsListMap;
			
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	/**
	 * 获取某个定义的Listt通过父编号
	 * @param parentCode
	 * @return
	 */
	public static List<ScDefine> getDefineItemListByParentCode(String parentCode)
	{
		List<ScDefine> ret = new ArrayList<ScDefine>();
		
		try {
			
			List<MDataMap> sfsListMap=
					WebTemp.upTempDataList("sc_define", "define_code,define_name", "", "", "parent_code",parentCode);
			
			
			if(sfsListMap!=null){
				int size = sfsListMap.size();
				
				SerializeSupport ss = new SerializeSupport<ScDefine>();
				for(int i=0;i<size;i++)
				{
					ScDefine pic = new ScDefine();
					ss.serialize(sfsListMap.get(i), pic);
					ret.add(pic);
				}
			}
			
		} catch (Exception e) {
			
		}
		
		return ret;
	}

	
	/**
	 * 获取定义的值
	 * @param defineCode
	 * @return
	 */
	public static String getDefineNameByCode(String defineCode)
	{
		String ret = "";
		
		try {
			
			ret = WebTemp.upTempDataOne("sc_define","define_name","define_code",defineCode);
			
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
	public static MDataMap getDefineByCode(String defineCode)
	{
		MDataMap flowMain = null;
		
		try {
			
			flowMain = DbUp.upTable("sc_define").one("define_code",
					defineCode);
			
		} catch (Exception e) {
			
		}
		
		return flowMain;
	}
	
	public MDataMap defineMap(String parent_code){
		
		MDataMap dataMap = new MDataMap();
		
		List<MDataMap> list=DbUp.upTable("sc_define").queryByWhere("parent_code",parent_code);
		if(list!=null&&list.size()>0){
			
			for (MDataMap mDataMap : list) {
				dataMap.put(mDataMap.get("define_code"), mDataMap.get("define_name"));
			}
		}
		
		return dataMap;
	}
}
