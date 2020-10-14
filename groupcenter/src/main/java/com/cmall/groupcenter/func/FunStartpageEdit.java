package com.cmall.groupcenter.func;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改app启动页
 * 
 * @author lijx
 *
 */

public class FunStartpageEdit extends RootFunc{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getFieldName());

					mInsertMap.put(mField.getColumnName(), sValue);
				}
			}
			if (bFlagComponent) {
				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {
						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,mDataMap);
					}
				}
			}
		}

		if (mResult.upFlagTrue()) {
			
			//先判断登录是否有效
			if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
				mResult.inErrorMessage(941901073);
				return mResult;
			}
			String picId = WebHelper.upCode("PXH");
			String start_time = mAddMaps.get("start_time");
			String end_time = mAddMaps.get("end_time");
			String uid = mAddMaps.get("uid");
			String modifier = UserFactory.INSTANCE.create().getLoginName();
			String modifier_time = FormatHelper.upDateTime();
			//开始时间必须小于结束时间
			if (start_time.compareTo(end_time) >= 0) {
				mResult.inErrorMessage(916401201);
				return mResult;
			}
			
			//判断开始结束时间是否修改过
			MDataMap flagTimeUpdate = DbUp.upTable("nc_startpage").one("uid",uid,
													"start_time",start_time,"end_time",end_time);
			if(null == flagTimeUpdate || flagTimeUpdate.isEmpty()){
				/*所属app*/
				String app_code = "SI2011";
				String sWhere = " ((start_time BETWEEN '"+start_time+"' and '"+end_time+"') "+
						" or (end_time BETWEEN '"+start_time+"' and '"+end_time+"') "+
						" or ('"+start_time+"' BETWEEN start_time and end_time) "+
						" or ('"+end_time+"' BETWEEN start_time and end_time)) and app_code='"+app_code+"' and uid != '"+uid+"'";
				MDataMap startPage = DbUp.upTable("nc_startpage").oneWhere("uid", "", sWhere);
				if (startPage != null && !startPage.isEmpty()) {
					mResult.inErrorMessage(916401202);
					return mResult;
				}
			}
			
			mInsertMap.put("pic_id", picId);
			mInsertMap.put("modifier_time",modifier_time);
			mInsertMap.put("modifier",modifier);
			//插入数据
			DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
}
