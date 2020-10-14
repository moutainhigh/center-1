package com.cmall.systemcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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


public class SolrWeightAdd extends RootFunc{
	
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
        
		/**
		 * 创建日期和用户名
		 */
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		mInsertMap.put("update_time", df.format(new Date()));   // new Date()为获取当前系统时间
		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("user_name", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
		}
		
		mInsertMap.put("seller_code", mAddMaps.get("seller_code"));		
		mInsertMap.put("is_index", mAddMaps.get("is_index").trim().toString());
		mInsertMap.put("is_store", mAddMaps.get("is_store").trim().toString());
		
		/**
		 * 需要判断当前字符串的类型 filed 字段类型，如果为String类型，默认is_store为0
		 */
		if(null!= mAddMaps.get("filed")||!"".equals(mAddMaps.get("filed"))){
			if(mAddMaps.get("filed").trim().toString().equals("449747050005") || mAddMaps.get("filed").trim().toString().equals("449747050006") ||mAddMaps.get("filed").trim().toString().equals("449747050009")){
				mInsertMap.put("bf", mAddMaps.get("bf").trim().toString());
			}else{
				mInsertMap.put("bf", "0");
			}
		}else{
			mInsertMap.put("bf", "0");
		}
		
		mInsertMap.put("qf", mAddMaps.get("qf").trim().toString());
		mInsertMap.put("filed", mAddMaps.get("filed").trim().toString());
		
		/**
		 * 判断当前字段是否存在，如果存在就走修改，不存在就走添加
		 */
		String sqlFiled="select zid,uid from pc_solr_weight where filed='"+mAddMaps.get("filed").trim()+"' and seller_code='"+mAddMaps.get("seller_code")+"'";
		Map<String,Object> mapFiled = DbUp.upTable("pc_solr_weight").dataSqlOne(sqlFiled, new MDataMap());
		
		
		if(mapFiled == null || mapFiled.size()<1){
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
			mInsertMap.put("zid", mapFiled.get("zid").toString());
			mInsertMap.put("uid", mapFiled.get("uid").toString());
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
