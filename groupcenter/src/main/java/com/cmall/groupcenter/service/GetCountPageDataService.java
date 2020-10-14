package com.cmall.groupcenter.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebView;

/**
 * 微公社商户后台-返利对账页面数据汇总
 * @author GaoYang
 *
 */
public class GetCountPageDataService  extends BaseClass{

	@SuppressWarnings("unchecked")
	public MPageData upChartData(String operateId,String searchItem,String groupByItem,Map params){
		
		MPageData pageData = new MPageData();
		
		if(StringUtils.isBlank(operateId) || searchItem.split(",").length < 1 ){
			return pageData;
		}
		
		MWebPage webPage = new MWebPage();
		webPage = WebUp.upPage(operateId);//operateId：页面编号
		String pageTable = webPage.getPageTable();
		
		String[] sItems = searchItem.split(",");
		// 查询条件
		String sWhere = " select " + searchItem + " from " + pageTable + " where 1=1 ";
		
		MDataMap mQueryMap = new MDataMap();
		MDataMap mReqMap = new MDataMap();
		
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()){
			 Map.Entry<String, String> entry = iterator.next();
			 if(StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())){
				 mReqMap.put(entry.getKey(), entry.getValue());
			 }
		}
		 
		/********** 开始处理查询输入 ********************************/
		{

			MDataMap mWhereMap = new MDataMap();
			if (StringUtils.isNotEmpty(webPage.getDataScope())) {

				MDataMap mScopeMap = new MDataMap();
				mScopeMap.inAllValues(FormatHelper.upUrlStrings(WebHelper
						.recheckReplace(webPage.getDataScope(), mReqMap)));
				mWhereMap = mScopeMap
						.upSubMap(WebConst.CONST_WEB_PAGINATION_NAME);

			}
			
			if (mWhereMap != null && mWhereMap.size() > 0){
				// 判断如果附加了SQL预条件定义
				if (mWhereMap.containsKey("sql_where")) {

					String sField = mWhereMap.get("sql_where");

					if (StringUtils.isNotEmpty(sField)) {
						sField = (StringUtils.isEmpty(sWhere) ? "" : " and ")
								+ WebHelper.recheckReplace(sField, mReqMap);
					}

					sWhere = sWhere + sField;

				}
			}
			
			// 开始加载查询条件判断
			if (mReqMap.size() > 0) {

				ArrayList<String> aWhereStrings = new ArrayList<String>();

				MWebView mView = WebUp.upQueryView(webPage.getViewCode());
				List<MWebField> listQuery = recheckFields(mView.getFields(),
						mReqMap);

				for (MWebField mField : listQuery) {

					switch (Integer.parseInt(mField.getQueryTypeAid())) {

					// 如果是范围查询
					case 104009002:
					case 104009020:
						
						if (StringUtils.isNotEmpty(mReqMap.get(mField
								.getPageFieldName()
								+ WebConst.CONST_WEB_FIELD_AFTER
								+ "between_from"))) {

							aWhereStrings.add(mField.getColumnName() + ">=:"
									+ mField.getColumnName()
									+ WebConst.CONST_WEB_FIELD_AFTER
									+ "between_from");
							mQueryMap.put(
									mField.getColumnName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_from",
									mReqMap.get(mField.getPageFieldName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_from"));

						}

						if (StringUtils
								.isNotEmpty(mReqMap.get(mField
										.getPageFieldName()
										+ WebConst.CONST_WEB_FIELD_AFTER
										+ "between_to"))) {

							aWhereStrings.add(mField.getColumnName() + "<=:"
									+ mField.getColumnName()
									+ WebConst.CONST_WEB_FIELD_AFTER
									+ "between_to");
							mQueryMap.put(
									mField.getColumnName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_to",
									mReqMap.get(mField.getPageFieldName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_to"));

						}

						break;

					// 如果是like查询
					case 104009012:

						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " like :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									"%" + mField.getPageFieldValue() + "%");
						}
						break;
					// 起始于
					case 104009019:
						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " like :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									mField.getPageFieldValue() + "%");
						}
						break;

					// 默认走等于
					default:

						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " = :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									mField.getPageFieldValue());
						}

						break;
					}

				}

				if (aWhereStrings.size() > 0) {

					sWhere = sWhere + " and " +  StringUtils.join(aWhereStrings, " and ");
				}

			}
			if(StringUtils.isNotBlank(groupByItem)){
				sWhere += " group by " + groupByItem + " ";
			}
			
		}
		
		List<Map<String, Object>> countRecordList=DbUp.upTable(pageTable).dataSqlList(sWhere,mQueryMap);
		
		// 数据
		List<List<String>> listData = new ArrayList<List<String>>();
		if(countRecordList != null && countRecordList.size() > 0){
			for(int i = 0;i < countRecordList.size();i++){
				List<String> listEach = new ArrayList<String>();
				for(String item : sItems){
					listEach.add(String.valueOf(countRecordList.get(i).get(item)));
				}
				listData.add(listEach);
			}
		}
		pageData.setPageData(listData);
		
		return pageData;
	}

	/**
	 * 商户的上期余额
	 * @param operateId
	 * @param searchItem
	 * @param orderByItem
	 * @param params
	 * @return
	 */
	public float upTraderLastMoney(String operateId,String searchItem,String orderByItem,Map params){
		
		MPageData pageData = new MPageData();
		float lastMoney=0f;
		
		if(StringUtils.isBlank(operateId) || searchItem.split(",").length < 1 || StringUtils.isBlank(orderByItem)){
			return 0f;
		}
		
		MWebPage webPage = new MWebPage();
		webPage = WebUp.upPage(operateId);//operateId：页面编号
		String pageTable = webPage.getPageTable();
		
		String[] sItems = searchItem.split(",");
		// 查询条件
		String sWhere = " select " + searchItem + " from " + pageTable + " where 1=1 ";
		
		MDataMap mQueryMap = new MDataMap();
		MDataMap mReqMap = new MDataMap();
		
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()){
			 Map.Entry<String, String> entry = iterator.next();
			 if(StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())){
				 mReqMap.put(entry.getKey(), entry.getValue());
			 }
		}
		 
		/********** 开始处理查询输入 ********************************/
		{

			MDataMap mWhereMap = new MDataMap();
			if (StringUtils.isNotEmpty(webPage.getDataScope())) {

				MDataMap mScopeMap = new MDataMap();
				mScopeMap.inAllValues(FormatHelper.upUrlStrings(WebHelper
						.recheckReplace(webPage.getDataScope(), mReqMap)));
				mWhereMap = mScopeMap
						.upSubMap(WebConst.CONST_WEB_PAGINATION_NAME);

			}
			
			if (mWhereMap != null && mWhereMap.size() > 0){
				// 判断如果附加了SQL预条件定义
				if (mWhereMap.containsKey("sql_where")) {

					String sField = mWhereMap.get("sql_where");

					if (StringUtils.isNotEmpty(sField)) {
						sField = (StringUtils.isEmpty(sWhere) ? "" : " and ")
								+ WebHelper.recheckReplace(sField, mReqMap);
					}

					sWhere = sWhere + sField;

				}
			}
			
			// 开始加载查询条件判断
			if (mReqMap.size() > 0) {

				ArrayList<String> aWhereStrings = new ArrayList<String>();

				MWebView mView = WebUp.upQueryView(webPage.getViewCode());
				List<MWebField> listQuery = recheckFields(mView.getFields(),
						mReqMap);

				for (MWebField mField : listQuery) {

					switch (Integer.parseInt(mField.getQueryTypeAid())) {

					// 如果是范围查询
					case 104009002:
					case 104009020:
						if (StringUtils.isNotEmpty(mReqMap.get(mField
								.getPageFieldName()
								+ WebConst.CONST_WEB_FIELD_AFTER
								+ "between_from"))) {

							aWhereStrings.add(mField.getColumnName() + "<:"
									+ mField.getColumnName()
									+ WebConst.CONST_WEB_FIELD_AFTER
									+ "between_from");
							mQueryMap.put(
									mField.getColumnName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_from",
									mReqMap.get(mField.getPageFieldName()
											+ WebConst.CONST_WEB_FIELD_AFTER
											+ "between_from"));

						}
//
//						if (StringUtils
//								.isNotEmpty(mReqMap.get(mField
//										.getPageFieldName()
//										+ WebConst.CONST_WEB_FIELD_AFTER
//										+ "between_to"))) {
//
//							aWhereStrings.add(mField.getColumnName() + "<=:"
//									+ mField.getColumnName()
//									+ WebConst.CONST_WEB_FIELD_AFTER
//									+ "between_to");
//							mQueryMap.put(
//									mField.getColumnName()
//											+ WebConst.CONST_WEB_FIELD_AFTER
//											+ "between_to",
//									mReqMap.get(mField.getPageFieldName()
//											+ WebConst.CONST_WEB_FIELD_AFTER
//											+ "between_to"));
//
//						}

						break;

					// 如果是like查询
					case 104009012:

						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " like :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									"%" + mField.getPageFieldValue() + "%");
						}
						break;
					// 起始于
					case 104009019:
						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " like :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									mField.getPageFieldValue() + "%");
						}
						break;

					// 默认走等于
					default:

						if (StringUtils.isNotEmpty(mField.getPageFieldValue())) {
							aWhereStrings.add(" " + mField.getColumnName()
									+ " = :" + mField.getColumnName());
							mQueryMap.put(mField.getColumnName(),
									mField.getPageFieldValue());
						}

						break;
					}

				}

				if (aWhereStrings.size() > 0) {

					sWhere = sWhere + " and " +  StringUtils.join(aWhereStrings, " and ");
				}

			}
			
			sWhere += " order by " + orderByItem + " desc,zid desc LIMIT 1000 ";
		}
		
		
		List<Map<String, Object>> countRecordList=DbUp.upTable(pageTable).dataSqlList(sWhere,mQueryMap);
		
		// 数据
		if(countRecordList != null && countRecordList.size() > 0){
			for(String item : sItems){
				lastMoney = Float.parseFloat(String.valueOf(countRecordList.get(0).get(item)));
			}
		}
		
		return lastMoney;
	}
	
	/**
	 * 重新审查字段
	 * 
	 * @param inputFields
	 * @return
	 */
	private List<MWebField> recheckFields(List<MWebField> inputFields,
			MDataMap mReqMap) {
		List<MWebField> listReturnFields = new ArrayList<MWebField>();

		for (MWebField mField : inputFields) {
			if (!mField.getSort().equals("0")) {

				MWebField mCloneField = mField.clone();

				if (mReqMap.containsKey(mCloneField.getPageFieldName())) {
					mCloneField.setPageFieldValue(mReqMap.get(mCloneField
							.getPageFieldName()));
				}

				listReturnFields.add(mCloneField);

			}
		}

		return listReturnFields;
	}
}
