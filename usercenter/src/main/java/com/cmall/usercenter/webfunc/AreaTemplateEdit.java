package com.cmall.usercenter.webfunc;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.xmassystem.load.LoadTemplateAreaCode;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
* @ClassName: AreaTemplateEdit 
* @Description: 更新模板区域
* @author 张海生
* @date 2015-12-18 下午6:06:39 
*  
*/
public class AreaTemplateEdit extends RootFunc {

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
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("uid", mAddMaps.get("uid"));
		Map<String, Object> dataSqlOne = DbUp.upTable("sc_area_template").dataSqlOne("select * from sc_area_template where uid =:uid", mWhereMap);
		if (StringUtils.isNotBlank(mAddMaps.get("is_member")) && "449746250001".equals(is_default)) {//是否商户提交 并且为默认
			int dataCount = DbUp.upTable("sc_area_template").dataCount("is_default = '449746250001' and is_delete = '0' and merchants_code ='"+manageCode+"' and uid != '"+mAddMaps.get("uid")+"'", new MDataMap());
			if(dataCount > 0) {
				mResult.setResultCode(959701038);
				mResult.setResultMessage("已经存在默认模板，请修改后再提交!");
				return mResult;
			}

		}else if("449746250002".equals(dataSqlOne.get("is_default").toString())) {
			if("449746250001".equals(is_default)) {
				mWhereMap.put("is_default", "449746250001");
				mWhereMap.put("is_delete", "0");
				int dataCount = DbUp.upTable("sc_area_template").dataCount("is_default =:is_default", mWhereMap);
				if(dataCount > 0) {
					mResult.setResultCode(959701038);
					mResult.setResultMessage("已经存在默认模板，请修改后再提交!");
					return mResult;
				}
			}
		}
		String create_time = DateUtil.getNowTime();// 系统当前时间
		String create_user = UserFactory.INSTANCE.create().getLoginName();/* 获取当前登录人 */
		MDataMap tempMap = new MDataMap();
		tempMap.put("template_name", mAddMaps.get("template_name"));
		tempMap.put("template_type", mAddMaps.get("template_type"));
		tempMap.put("is_default", is_default);
		tempMap.put("update_time", create_time);
		tempMap.put("update_user", create_user);
		tempMap.put("uid", mAddMaps.get("uid"));
		try {
			DbUp.upTable("sc_area_template").dataUpdate(tempMap, "template_name,template_type,update_time,update_user,is_default", "uid");//插入区域模板
			DbUp.upTable("sc_area_template_info").dataDelete("", new MDataMap("template_code",mAddMaps.get("template_code")), "template_code");//先删除再插入
			Iterator<String> teKey = mAddMaps.keySet().iterator();
			MDataMap infMap = new MDataMap();
			while(teKey.hasNext()){
				String key = teKey.next();
				String cityTmpCode = mAddMaps.get(key);
				String provinceCity[] = cityTmpCode.split("_");
				
				if(key.contains("city_code")){
					infMap.put("template_code", mAddMaps.get("template_code"));
					infMap.put("province_code", provinceCity[0]);
					infMap.put("city_code", provinceCity[1]);
					DbUp.upTable("sc_area_template_info").dataInsert(infMap);//插入模板对应的区域信息
				}
			}
		} catch (Exception e) {
			
		}
		new LoadTemplateAreaCode().deleteInfoByCode(mAddMaps.get("template_code"));//清除缓存数据
		return mResult;
	}
	
	
	public int provinceCity(MDataMap mAddMaps) {
		int res = 0;
		Iterator<String> teKey = mAddMaps.keySet().iterator();
		MDataMap infMap = new MDataMap();
		while(teKey.hasNext()){//插入区域模板对应的城市
			String key = teKey.next();
			String cityTmpCode = mAddMaps.get(key);
			String provinceCity[] = cityTmpCode.split("_");
			
			if(key.contains("city_code")){
				res++;
			}
		}
		return res;
	}

}
