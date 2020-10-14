package com.cmall.productcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改商品规格属性
 * @author 李国杰
 *
 */
public class FuncEditForPropertyinfoCf extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {
			
			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				if (mAddMaps.containsKey(mField.getFieldName())&& StringUtils.isNotEmpty(mField.getColumnName())) {
					String sValue = mAddMaps.get(mField.getFieldName());
					mInsertMap.put(mField.getColumnName(), sValue);
				}
			}
			
			String parent_code=mDataMap.get("zw_f_parent_code");
			String property_name=mDataMap.get("zw_f_property_name");
			String property_name_old=mDataMap.get("zw_f_property_name_old");
			
			if (checkName(parent_code,property_name,property_name_old)) {
				//返回提示信息
				mResult.setResultCode(941901082);
				mResult.setResultMessage(bInfo(941901082));
				return mResult;
			}else{
				DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
				mResult.setResultMessage(bInfo(969909001));
			}
		}

		return mResult;
	}
	
	/**
	 * 校验是否与数据库里有重复
	 * @param appCode, placeName, columnCode
	 * @return 
	 */
	private boolean checkName(String parent_code,String property_name,String property_name_old) {
		
		if(property_name.equals(property_name_old)){
			return false;
		}
		
		MDataMap mDataParam = new MDataMap();
		mDataParam.put("parent_code", parent_code);
		mDataParam.put("property_name", property_name);
		
		//判断数据库中是否存在相同记录
		
		 List<MDataMap> list =DbUp.upTable("pc_propertyinfo").query("uid", "", "", mDataParam, 0, 1);		
		 if (list.size() > 0) {
				return true;
			}
			return false;
	}
}
