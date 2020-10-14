package com.cmall.systemcenter.webfunc;

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
 * app版本添加方法
 * @author zhouguohui
 *
 */
public class UpgradeAppAdd extends RootFunc {

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

				// 如果设置不为空 则进行各种校验
				if (StringUtils.isNotEmpty(mField.getFieldScope())) {

					MDataMap mFieldScope = new MDataMap().inUrlParams(mField
							.getFieldScope());

					MDataMap mScopeMap = mFieldScope
							.upSubMap(WebConst.CONST_WEB_PAGINATION_NAME);

					String sDefaultValue = "";

					if (mScopeMap.containsKey("defaultvalue")) {
						sDefaultValue = mScopeMap.get("defaultvalue");
					}

					// 判断默认值
					if (StringUtils.isNotEmpty(sDefaultValue)) {
						String sValue = "";

						if (StringUtils.contains(sDefaultValue,
								WebConst.CONST_WEB_SET_REPLACE)) {

							//重新格式化参数
							sValue = WebHelper.recheckReplace(sDefaultValue,
									mDataMap);

						} else {
							sValue = sDefaultValue;
						}

						if (StringUtils.isNotEmpty(sValue)) {
							mDataMap.put(
									WebConst.CONST_WEB_FIELD_NAME
											+ mField.getFieldName(), sValue);

							mInsertMap.put(mField.getColumnName(), sValue);
						}

					}

					// 如果有附件设置
					if (mScopeMap.containsKey("targetset")) {
						String sTargetSetString = mScopeMap.get("targetset");

						// 校验字段的唯一
						if (sTargetSetString.equals("unique")) {

							if (DbUp.upTable(mPage.getPageTable()).count(
									mField.getColumnName(),
									mInsertMap.get(mField.getColumnName())) > 0) {

								mResult.inErrorMessage(969905004,
										mField.getFieldNote());
							}

						}
						// 自动生成code
						else if (sTargetSetString.equals("code")) {

							MDataMap mSetMap = mFieldScope
									.upSubMap(WebConst.CONST_WEB_FIELD_SET);

							String sCodeName = mSetMap.get("codename");

							String sParentValue = mAddMaps.get(sCodeName);

							MDataMap mTopDataMap = DbUp.upTable(
									mPage.getPageTable()).oneWhere(
									mField.getColumnName(),
									"-"+mField.getColumnName(), "", sCodeName,
									sParentValue);

							if (mTopDataMap != null) {

								String sMaxString = mTopDataMap.get(mField
										.getColumnName());
								long lMax = Long.parseLong(StringUtils.right(sMaxString, 4)) + 1;

								
								
								mInsertMap.put(mField.getColumnName(),
										sParentValue+ StringUtils.leftPad(String.valueOf(lMax), 4, "0"));

							} else {
								String sMaxAdd = sParentValue
										+ (mSetMap.containsKey("maxadd") ? mSetMap
												.get("maxadd") : "0001");

								mInsertMap.put(mField.getColumnName(),
										String.valueOf(sMaxAdd));

							}

						}

					}

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
		
		/*if(StringUtils.isEmpty(mInsertMap.get("ifda"))){
			mInsertMap.remove("ifda");
		}else{
			if(mInsertMap.get("ifda").equals("449746250001")){
				mInsertMap.put("ifda", "1");
			}else{
				mInsertMap.put("ifda", "0");
			}
		}*/
		
		
		if(StringUtils.isEmpty(mInsertMap.get("ios_url"))){
			mInsertMap.remove("ios_url");
		}
		
		if(StringUtils.isEmpty(mInsertMap.get("url"))){
			mInsertMap.remove("url");
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

}
