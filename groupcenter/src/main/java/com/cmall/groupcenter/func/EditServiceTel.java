package com.cmall.groupcenter.func;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改微公社服务电话
 * @author lijx
 *
 */

public class EditServiceTel extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String telType = mAddMaps.get("tel_type");
		
		String uid = mAddMaps.get("uid");
		
		if (checkIsExistTelType(telType,uid)) {
			// 返回提示信息"存在此电话类型，请重新填写！"
			mResult.setResultMessage("存在此电话类型，请重新填写！");
			
			return mResult;
		}
		
		String updateUser = UserFactory.INSTANCE.create().getLoginName();
		
		String updateTime = FormatHelper.upDateTime();
		
		mAddMaps.put("update_time",updateTime);
		
		mAddMaps.put("update_user",updateUser);
		
		DbUp.upTable("gc_service_tel").dataUpdate(mAddMaps, "update_time,update_user,tel_number", "uid");
		
		return mResult;
		
	}
	
	/**
	 * 检验是否有相对应的电话类型
	 * @param chaName 频道名称
	 * @param uid
	 * @return 如果存在：true 否则：false
	 */
	private boolean checkIsExistTelType(String telType, String uid) {
		// TODO Auto-generated method stub
		// 判断数据库中是否存在相同记录
		String sWhere = "tel_type = '" + telType + "'" + " and uid != '" + uid +"'";
		int count = DbUp.upTable("gc_service_tel").dataCount(sWhere, new MDataMap());
		if (count > 0) {
			return true;
		}
		return false;
	}

}
