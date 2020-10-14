package com.cmall.systemcenter.webfunc;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SolrWeightEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		// 定义组件判断标记
		boolean bFlagComponent = false;

		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getColumnName());

					mInsertMap.put(mField.getColumnName(), sValue);
				} else if (mField.getFieldTypeAid().equals("104005103")) {
					//特殊判断修改时如果没有传值 则自动赋空
					mInsertMap.put(mField.getColumnName(), "");
				}

			}
		}

		//创建时间为当年系统时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
				
				mInsertMap.put("create_time", df.format(new Date()));   // new Date()为获取当前系统时间
				

				if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
					mResult.inErrorMessage(941901073);
				}else{
					mInsertMap.put("founder", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
				}
				
				mInsertMap.put("seller_code", mAddMaps.get("seller_code"));		
				
				if("449746250001".equals(mAddMaps.get("is_index").trim())){
					mInsertMap.put("is_index", "1");
				}else{
					mInsertMap.put("is_index", "0");
				}
				
				
				if("449746250001".equals(mAddMaps.get("is_store").trim())){
					//需要判断当前字符串的类型 filed 字段类型，如果为String类型，默认is_store为0
					
					mInsertMap.put("is_store", "1");
				}else{
					mInsertMap.put("is_store", "0");
				}
				
				mInsertMap.put("bf", mAddMaps.get("bf").trim().toString());
				mInsertMap.put("qf", mAddMaps.get("qf").trim().toString());
				mInsertMap.put("filed", mAddMaps.get("filed").trim().toString());		
				
		if (mResult.upFlagTrue()) {
			DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");

			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inEdit(
								mField, mDataMap);

					}
				}

			}

		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}

}
