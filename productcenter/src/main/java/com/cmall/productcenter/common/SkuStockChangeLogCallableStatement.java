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
* 项目名称：productcenter   
* 类名称：SkuStockChangeLogCallableStatement   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-3 下午2:21:52   
* 修改人：yanzj
* 修改时间：2013-9-3 下午2:21:52   
* 修改备注：   
* @version    
*    
*/
public class SkuStockChangeLogCallableStatement implements
		CallableStatementCreator {

	List<ProductSkuInfo> list = new ArrayList<ProductSkuInfo>();

	private String orderCode = "";
	private String changeType = "";
	private String createUser = "";

	public SkuStockChangeLogCallableStatement(List<ProductSkuInfo> list,
			String orderCode,String changeType,String createUser) {
		this.list = list;
		this.orderCode=orderCode;
		this.createUser=createUser;
		this.changeType=changeType;
	}

	public CallableStatement createCallableStatement(Connection con)
			throws SQLException {
		final String callProcedureSql = "{call proc_sku_stockchange_log_add(?, ?, ?, ?, ?, ?, ?, ?)}";
		CallableStatement cstmt = con.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);

		StringBuffer productStr = new StringBuffer();

		for (int i = 0; i < list.size(); i++) {
			ProductSkuInfo od = list.get(i);
			productStr.append(od.getSkuCode() + SkuCommon.SecondSplitStr);
			if(changeType.equals(SkuCommon.SkuStockChangeTypeOrderCommit))
				productStr.append("-"+od.getStockNum());
			else if(changeType.equals(SkuCommon.SkuStockChangeTypeOrderRollBack) || changeType.equals(SkuCommon.SkuStockChangeTypeCreateProduct))
				productStr.append(od.getStockNum());
			else
				productStr.append("-"+od.getStockNum());
			
			if (i != list.size() - 1)
				productStr.append(SkuCommon.FirstSplitStr);
		}

		cstmt.setString(3, productStr.toString());
		cstmt.setString(4, SkuCommon.FirstSplitStr);
		cstmt.setString(5, SkuCommon.SecondSplitStr);
		cstmt.setString(6, this.orderCode);
		cstmt.setString(7, this.changeType);
		cstmt.setString(8, this.createUser);

		return cstmt;
	}

}
