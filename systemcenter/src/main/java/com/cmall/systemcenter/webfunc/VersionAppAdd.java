package com.cmall.systemcenter.webfunc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;


public class VersionAppAdd extends RootFunc{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String,
	 * com.srnpr.zapcom.basemodel.MDataMap)
	 */
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

		
		
		mInsertMap.put("app_id", mAddMaps.get("app_code"));
		mInsertMap.put("remind_counts", mAddMaps.get("remind_counts"));
		
		if(mAddMaps.get("page_codeZero").trim().equals("qtZero") && mAddMaps.get("column_nameZero").trim() != null){
			mInsertMap.put("minumum_versions", mAddMaps.get("column_nameZero"));
		}else{
			mInsertMap.put("minumum_versions", mAddMaps.get("page_codeZero"));
		}
		
		if(mAddMaps.get("page_codeOne").trim().equals("qtOne") && mAddMaps.get("column_nameOne").trim() != null){
			mInsertMap.put("highest_versions", mAddMaps.get("column_nameOne"));
		}else{
			mInsertMap.put("highest_versions", mAddMaps.get("page_codeOne"));
		}
		
		mInsertMap.put("versions", mAddMaps.get("page_codeTwo"));
		
		String values = mAddMaps.get("app_code");
		
		
		MDataMap map=new MDataMap();
		map.inAllValues("app_id",values);
		String sSql = "select * from sc_versions_app  where app_id =:app_id";
		Map<String,Object> mapList = new HashMap<String,Object>();
		mapList = DbUp.upTable("sc_versions_app").dataSqlOne(sSql, map);
		
		
		if(mapList == null || mapList.size()<1){
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
			
		}else{
			mInsertMap.put("zid", mapList.get("zid").toString());
			mInsertMap.put("uid", mapList.get("uid").toString());
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
			
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	
	

}
