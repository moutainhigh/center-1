package com.cmall.groupcenter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


/*
 * 分页取数据  辅助类
 */
public class DataPaging {

	/**
	 * @param sTableName
	 *            表名称
	 * @param sFields
	 *            获取字段名称
	 * @param sOrders
	 *            排序
	 * @param mWhereMap
	 *            条件
	 * @param pageOption
	 *            分页
	 * @return
	 */
	public static MPageData upPageData(String sTableName, String sFields,
			String sOrders, MDataMap mWhereMap, PageOption pageOption) {

		MPageData mPageData = new MPageData();

		// 返回数据
		mPageData.setListData(DbUp.upTable(sTableName).query(sFields, sOrders,
				"", mWhereMap, pageOption.getLimit() * pageOption.getOffset(),
				pageOption.getLimit()));

		PageResults pageResults = new PageResults();

		// 设置根据条件查询出的所有的结果的数量
		pageResults.setTotal(DbUp.upTable(sTableName).dataCount("", mWhereMap));

		// 返回的条数
		pageResults.setCount(mPageData.getListData().size());

		// 判断是否还有更多数据
		pageResults
				.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
						.getCount()) < pageResults.getTotal() ? 1 : 0);

		mPageData.setPageResults(pageResults);

		return mPageData;

	}
	
	/**
	 * @param sTableName
	 *            表名称
	 * @param sFields
	 *            获取字段名称
	 * @param sOrders
	 *            排序
	 * @param sWhere
	 *            条件
	 * @param mWhereMap
	 *            条件(无用)
	 * @param pageOption
	 *            分页
	 * @param sFieldName
	 *            条件IN的字段名
	 * @param sFieldName
	 *            条件IN的字段值
	 * @return
	 */
	public static MPageData upPageDataQueryIn(String sTableName, String sFields,
			String sOrders,String sWhere, MDataMap mWhereMap, PageOption pageOption, String sFieldName, String sFieldValue) {

		MPageData mPageData = new MPageData();

		// 返回数据
		
		mPageData.setListData(DbUp.upTable(sTableName).queryInSafe(sFields, sOrders,
				sWhere, mWhereMap, pageOption.getLimit() * pageOption.getOffset(),
				pageOption.getLimit(), sFieldName, sFieldValue));

		PageResults pageResults = new PageResults();

		// 设置根据条件查询出的所有的结果的数量
		if (StringUtils.isNotBlank(sFieldName)&&StringUtils.isNotBlank(sFieldValue)) {
			if (StringUtils.isNotEmpty(sWhere)) {
				sWhere = sWhere + " and ";
			}

			String[] sValuesStrings = StringUtils.split(sFieldValue, ",");
			List<String> lAdd = new ArrayList<String>();

			for (int i = 0, j = sValuesStrings.length; i < j; i++) {
				/*
				lAdd.add(" " + sFieldName + "=:" + sFieldName + "_"
						+ String.valueOf(i) + " ");
				mWhereMap.put(sFieldName + "_" + String.valueOf(i),
						sValuesStrings[i]);
						*/
				lAdd.add("'"+sValuesStrings[i]+"'");

			}

			sWhere = sWhere + " "+sFieldName+" in (" + StringUtils.join(lAdd, ",") + ")";

		}
		pageResults.setTotal(DbUp.upTable(sTableName).dataCount(sWhere, mWhereMap));

		// 返回的条数
		pageResults.setCount(mPageData.getListData().size());

		// 判断是否还有更多数据
		pageResults
				.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults
						.getCount()) < pageResults.getTotal() ? 1 : 0);

		mPageData.setPageResults(pageResults);

		return mPageData;

	}
	
	/**
	 * @param sTableName
	 *            表名称
	 * @param sFields
	 *            获取字段名称
	 * @param sOrders
	 *            排序
	 * @param mWhereMap
	 *            条件
	 * @param pageOption
	 *            分页
	 * @return
	 */
	public static MPageData upPageData(String sTableName, String sFields,
			String sOrders,String sWhere , MDataMap mWhereMap, PageOption pageOption) {

		MPageData mPageData = new MPageData();

		// 返回数据
		mPageData.setListData(DbUp.upTable(sTableName).query(sFields, sOrders,sWhere, mWhereMap, pageOption.getLimit() * pageOption.getOffset(),pageOption.getLimit()));
		
		int total = DbUp.upTable(sTableName).dataCount(sWhere, mWhereMap);
		
		mPageData.setPageResults(page(mPageData.getListData().size(), total, pageOption));

		return mPageData;

	}
	public static MPageData upPageData(String sTableName, String sSql, MDataMap mWhereMap, PageOption pageOption) {

		MPageData mPageData = new MPageData();
		
		List<Map<String, Object>> list=DbUp.upTable(sTableName).dataSqlList(sSql+" limit "+pageOption.getLimit() * pageOption.getOffset() +","+pageOption.getLimit(), mWhereMap);
		
		int total = Integer.valueOf(String.valueOf(DbUp.upTable(sTableName).dataSqlOne("select count(1) as dataget from ("+sSql+") ss ", mWhereMap).get("dataget")));
		
		List<MDataMap> rlist = new ArrayList<MDataMap>();
		if(list!=null&&list.size()>0){
			
			for (Map<String, Object> map : list) {
				rlist.add(new MDataMap(map));
			}
		}
		
		// 返回数据
		mPageData.setListData(rlist);

		mPageData.setPageResults(page(mPageData.getListData().size(), total, pageOption));

		return mPageData;
	}
	/**
	 * 
	 * @param size 数据的条数
	 * @param total 总记录数
	 * @param pageOption
	 * @return
	 */
	private static PageResults page(int size,int total,PageOption pageOption){
		
		PageResults pageResults = new PageResults();

		// 设置根据条件查询出的所有的结果的数量
		pageResults.setTotal(total);

		// 返回的条数
		pageResults.setCount(size);

		// 判断是否还有更多数据
		pageResults.setMore((pageOption.getLimit() * pageOption.getOffset() + pageResults.getCount()) < pageResults.getTotal() ? 1 : 0);
		
		return pageResults;
	}

}
