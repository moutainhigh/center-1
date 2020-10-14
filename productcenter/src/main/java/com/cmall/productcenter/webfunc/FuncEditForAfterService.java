package com.cmall.productcenter.webfunc;


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

/**
 * 修改售后服务
 * 
 * @author yangrong
 * 
 */
public class FuncEditForAfterService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
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
					
					if(mField.getColumnName().equals("service_title")){
						String sValue = mAddMaps.get(mField.getFieldName());
						//标题不大于20字。
						if(sValue.length()>20){
							mResult.inErrorMessage(941901071);
						}
						
						String uuid = mAddMaps.get("uid");
						String service_title = mAddMaps.get(mField.getFieldName());
						
						//根据service_title校验表中是否有数据存在
						if(checkInfo(service_title,uuid)){

							mResult.setResultCode(941901077);
							mResult.setResultMessage(bInfo(941901077));
							return mResult;
							
						}
						
					}

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
		
		mInsertMap.put("creat_time", df.format(new Date()));   // new Date()为获取当前系统时间
		mInsertMap.put("update_time", df.format(new Date()));   // new Date()为获取当前系统时间

		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("creat_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
			mInsertMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
		}
		
		if (mResult.upFlagTrue()) {
			DbUp.upTable(mPage.getPageTable())
					.dataUpdate(mInsertMap, "", "uid");

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
	
	/**
	 * 校验表中是否有数据存在
	 * @param service_title  uuid
	 * @return
	 */
	private boolean checkInfo(String service_title, String uuid) {
		
		int atCount = DbUp.upTable("pc_after_service").count("service_title", service_title);
		if(atCount == 1){
			String uid = getBoutidByUid(service_title);
			//没做修改不提示添加重复
			if(uid.equals(uuid)){
				return false;
			}else{
				return true;
			}
		}
		if(atCount > 1){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据service_title查出uid
	 * @param service_title
	 * @return
	 */
	public String  getBoutidByUid(String service_title)
	{
		MDataMap AdvertiseGenreData = DbUp.upTable("pc_after_service").one("service_title", service_title);
		return AdvertiseGenreData.get("uid");
	}

}
