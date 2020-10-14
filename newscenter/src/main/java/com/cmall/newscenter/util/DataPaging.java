package com.cmall.newscenter.util;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.PageResults;
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
	//overload upPageData support query by wheresql
	public static MPageData upPageData(String sTableName, String sFields,String sWhere,
			String sOrders, MDataMap mWhereMap, PageOption pageOption) {

		MPageData mPageData = new MPageData();

		// 返回数据
		mPageData.setListData(DbUp.upTable(sTableName).query(sFields, sOrders,
				sWhere, mWhereMap, pageOption.getLimit() * pageOption.getOffset(),
				pageOption.getLimit()));

		PageResults pageResults = new PageResults();

		// 设置根据条件查询出的所有的结果的数量
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
}
