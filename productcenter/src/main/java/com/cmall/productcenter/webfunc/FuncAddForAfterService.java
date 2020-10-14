package com.cmall.productcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

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
 * 添加售后服务
 * 
 * @author yangrong
 * 
 */
public class FuncAddForAfterService extends RootFunc {
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
					if(mField.getColumnName().equals("service_title")){
						
						if(sValue.length()>20){
							mResult.inErrorMessage(941901071);
						}
						
						String service_title = mAddMaps.get(mField.getFieldName());
						
						//根据service_title校验表中是否有数据存在
						if(checkInfo(service_title)){

							mResult.setResultCode(941901077);
							mResult.setResultMessage(bInfo(941901077));
							return mResult;
							
						}
					}
					
				}

			}
		}
		
		//编码自动生成
		mInsertMap.put("service_code", WebHelper.upCode("AS"));
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
			
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			
			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,
								mDataMap);
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
	 * @param service_title
	 * @return
	 */
	private boolean checkInfo(String service_title) {
		int atCount = DbUp.upTable("pc_after_service").count("service_title", service_title);
		if(atCount >= 1){
			return true;
		}
		return false;
	}
}
