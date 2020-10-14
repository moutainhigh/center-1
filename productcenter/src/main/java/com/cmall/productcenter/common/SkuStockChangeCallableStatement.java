package com.cmall.productcenter.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.CallableStatementCreator;


import com.cmall.productcenter.model.ProductSkuInfo;

/**
 * 
 * 项目名称：productcenter 类名称：SkuStockChangeCallableStatement 类描述： 创建人：yanzj
 * 创建时间：2013-9-3 上午11:35:52 修改人：yanzj 修改时间：2013-9-3 上午11:35:52 修改备注：
 * 
 * @version
 * 
 */
public class SkuStockChangeCallableStatement implements
		CallableStatementCreator {

	List<ProductSkuInfo> list = new ArrayList<ProductSkuInfo>();

	private int flag =0;
	
	public SkuStockChangeCallableStatement(List<ProductSkuInfo> list,int flag) {
		this.list = list;
		this.flag = flag;
	}

	public CallableStatement createCallableStatement(Connection con)
			throws SQLException {
		final String callProcedureSql = "{call proc_sku_stock(?, ?, ?, ?, ?, ?)}";
		CallableStatement cstmt = con.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		
		/*OUT outFlag varchar(50),
		OUT error VARCHAR(5000),
		IN detailStr VARCHAR(500000),
		IN productsplit VARCHAR(10),
		IN itemsplit VARCHAR(10),
		IN flag INT*/
		
		StringBuffer productStr = new StringBuffer();

		for (int i = 0; i < list.size(); i++) {
			ProductSkuInfo od = list.get(i);
			productStr.append(od.getSkuCode() + SkuCommon.SecondSplitStr + od.getStockNum());

			if (i != list.size() - 1)
				productStr.append(SkuCommon.FirstSplitStr);
		}

		cstmt.setString(3, productStr.toString());
		cstmt.setString(4, SkuCommon.FirstSplitStr);
		cstmt.setString(5, SkuCommon.SecondSplitStr);
		cstmt.setInt(6, this.flag);

		return cstmt;
	}

}
