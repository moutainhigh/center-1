package com.cmall.usercenter.webfunc;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AreaTemplateAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if(provinceCity(mAddMaps)==0) {
			mResult.setResultCode(959701038);
			mResult.setResultMessage("请添加城市后再提交!");
			return mResult;
		}
		String manageCode = UserFactory.INSTANCE.create().getManageCode();
		String is_default = mAddMaps.get("is_default");
		if (StringUtils.isNotBlank(mAddMaps.get("is_member")) && "449746250001".equals(is_default)) {//是否商户提交
			MDataMap mWhereMap = new MDataMap();
			/*mWhereMap.put("is_default", "449746250001");
			mWhereMap.put("merchants_code", manageCode);*/
			int dataCount = DbUp.upTable("sc_area_template").dataCount("is_default = '449746250001' and is_delete = '0' and merchants_code ='"+manageCode+"'", mWhereMap);
			if(dataCount > 0) {
				mResult.setResultCode(959701038);
				mResult.setResultMessage("已经存在默认模板，请修改后再提交!");
				return mResult;
			}

		}else if("449746250001".equals(is_default)) {
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("is_default", "449746250001");
			mWhereMap.put("is_delete", "0");
			int dataCount = DbUp.upTable("sc_area_template").dataCount("is_default =:is_default", mWhereMap);
			if(dataCount > 0) {
				mResult.setResultCode(959701038);
				mResult.setResultMessage("已经存在默认模板，请修改后再提交!");
				return mResult;
			}
		}
		String create_time = DateUtil.getNowTime();// 系统当前时间
		String create_user = UserFactory.INSTANCE.create().getLoginName();/* 获取当前登录人 */
		
		MDataMap tempMap = new MDataMap();
		//wangmeng by 5.6.9
		if (StringUtils.isNotBlank(mAddMaps.get("is_member"))) {
			
			tempMap.put("merchants_code", manageCode);
		}
		
		String templateCode = WebHelper.upCode("AR");
		tempMap.put("template_code", templateCode);
		tempMap.put("is_default", is_default);
		tempMap.put("template_name", mAddMaps.get("template_name"));
		tempMap.put("template_type", mAddMaps.get("template_type"));
		tempMap.put("create_time", create_time);
		tempMap.put("create_user", create_user);
		tempMap.put("update_time", create_time);
		tempMap.put("update_user", create_user);
		try {
			DbUp.upTable("sc_area_template").dataInsert(tempMap);//插入区域模板
			Iterator<String> teKey = mAddMaps.keySet().iterator();
			MDataMap infMap = new MDataMap();
			while(teKey.hasNext()){//插入区域模板对应的城市
				String key = teKey.next();
				String cityTmpCode = mAddMaps.get(key);
				String provinceCity[] = cityTmpCode.split("_");
				
				if(key.contains("city_code")){
					infMap.put("template_code", templateCode);
					infMap.put("province_code", provinceCity[0]);
					infMap.put("city_code", provinceCity[1]);
					DbUp.upTable("sc_area_template_info").dataInsert(infMap);//插入模板对应的区域信息
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mResult;
	}
	
	
	public int provinceCity(MDataMap mAddMaps) {
		int res = 0;
		Iterator<String> teKey = mAddMaps.keySet().iterator();
		while(teKey.hasNext()){//插入区域模板对应的城市
			String key = teKey.next();

			
			if(key.contains("city_code")){
				res++;
			}
		}
		return res;
	}
	

}
