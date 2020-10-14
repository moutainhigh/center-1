package com.cmall.groupcenter.func;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加微公社服务电话
 * @author lijx
 *
 */
public class AddServiceTel extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (checkIsExistTelType(mAddMaps.get("tel_type").toString())) {
			// 返回提示信息"存在此电话类型，请重新填写！"
			mResult.setResultMessage("存在此电话类型，请重新填写！");
			
			return mResult;
		}
		
		String createUser = UserFactory.INSTANCE.create().getLoginName();

		String createTime = FormatHelper.upDateTime();
		
		String telCode = WebHelper.upCode("TEL");
		
		mAddMaps.put("create_time", createTime);

		mAddMaps.put("create_user", createUser);

		mAddMaps.put("tel_code", telCode);

		DbUp.upTable("gc_service_tel").dataInsert(mAddMaps);

		return mResult;
	}
	
	/**
	 * 检验是否有相对应的电话类型
	 * @param telType
	 * @param uid
	 * @return 如果存在返回true,否则返回false
	 */
	private boolean checkIsExistTelType(String telType){
		
		// 判断数据库中是否存在相同记录
		int count = DbUp.upTable("gc_service_tel").count("tel_type",
				telType);
		if(count > 0){
			return true;
		}
		return false;
		
	}

}
