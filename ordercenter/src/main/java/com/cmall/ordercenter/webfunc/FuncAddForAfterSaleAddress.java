package com.cmall.ordercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.FuncAdd;
import com.srnpr.zapweb.webmethod.WebMethod;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商户添加售后地址
 * @author lgx
 *
 */
public class FuncAddForAfterSaleAddress extends FuncAdd {

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
		}
		mInsertMap.put("create_time", DateUtil.getSysDateTimeString());
		
		WebMethod webMethod = new WebMethod();
		MUserInfo upUserInfo = webMethod.upUserInfo();
		String manageCode = upUserInfo.getManageCode();
		String realName = upUserInfo.getRealName();
		mInsertMap.put("small_seller_code", manageCode);
		mInsertMap.put("small_seller_name", realName);
		
		DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
		
		return mResult;
	}
}
