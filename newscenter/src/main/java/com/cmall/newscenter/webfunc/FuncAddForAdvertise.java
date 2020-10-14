package com.cmall.newscenter.webfunc;

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
 * 发布广告管理
 * @author houwen
 *
 */
public class FuncAddForAdvertise extends RootFunc {
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
					
					if(mField.getColumnName().equals("ad_sort")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
							}
						}else{
							mInsertMap.put("ad_sort", "0");
						}
					}
				
				}

			}
		}
		
		/*//编码自动生成
		mInsertMap.put("configuration_id", WebHelper.upCode(""));*/
		
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		
		mInsertMap.put("create_time", df.format(new Date()));   // new Date()为获取当前系统时间
		

		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
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
	
	public static boolean isNumber(String str) {//判断整型
		   return str.matches("[\\d]+");
		}
}
