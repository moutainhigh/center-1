package com.cmall.ordercenter.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.CallableStatementCreator;

import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;

/**   
*    
* 项目名称：ordercenter   
* 类名称：CreateOrderCallableStatement   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 下午6:48:54   
* 修改人：yanzj
* 修改时间：2013-9-2 下午6:48:54   
* 修改备注：   
* @version    
*    
*/
public class CreateOrderCallableStatement implements CallableStatementCreator {

	private Order order = null;
	
	

	/**
	 * @param cardnostr
	 * @param cardsplit
	 * @param isTransaction
	 * @param activeFlag
	 */
	public CreateOrderCallableStatement(Order order) {
		this.order = order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.jdbc.core.CallableStatementCreator#
	 * createCallableStatement(java.sql.Connection)
	 */
	public CallableStatement createCallableStatement(Connection conn)
			throws SQLException {
		final String callProcedureSql = "{call proc_create_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
																"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
																"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
		CallableStatement cstmt = conn.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);
	
		cstmt.setString(3, this.order.getOrderCode());
		cstmt.setString(4, this.order.getOrderSource());
		cstmt.setString(5, this.order.getOrderType());
		cstmt.setString(6, this.order.getOrderStatus());
		cstmt.setString(7, this.order.getSellerCode());
		cstmt.setString(8, this.order.getBuyerCode());
		cstmt.setString(9, this.order.getPayType());
		cstmt.setString(10, this.order.getSendType());
		cstmt.setBigDecimal(11, this.order.getProductMoney());
		cstmt.setBigDecimal(12, this.order.getTransportMoney());
		cstmt.setBigDecimal(13, this.order.getPromotionMoney());
		cstmt.setBigDecimal(14, this.order.getOrderMoney());
		cstmt.setBigDecimal(15, this.order.getPayedMoney());
		cstmt.setString(16, "");
		cstmt.setString(17, "");
		cstmt.setString(18, this.order.getAddress().getAreaCode());
		cstmt.setString(19, SqlCommon.TransactSQLInjection(this.order.getAddress().getAddress()));
		cstmt.setString(20, this.order.getAddress().getPostCode());
		cstmt.setString(21, this.order.getAddress().getMobilephone());
		cstmt.setString(22, this.order.getAddress().getTelephone());
		cstmt.setString(23, this.order.getAddress().getReceivePerson());
		cstmt.setString(24, this.order.getAddress().getEmail());
		cstmt.setString(25, this.order.getAddress().getInvoiceTitle());
		cstmt.setString(26, this.order.getAddress().getFlagInvoice());
		cstmt.setString(27, this.order.getAddress().getRemark());
		
		StringBuffer productStr = new StringBuffer();
		
		//int i=0;
		//		
		/*SELECT func_get_split_string(currentProduct,itemsplit,1) into skuCode;
		SELECT func_get_split_string(currentProduct,itemsplit,2) into skuPrice;
		SELECT func_get_split_string(currentProduct,itemsplit,3) into skuNum;
		SELECT func_get_split_string(currentProduct,itemsplit,4) into skuProductCode;
		SELECT func_get_split_string(currentProduct,itemsplit,5) into skuProductName;*/
		for(int i=0;i<order.getProductList().size();i++)
		{
			OrderDetail od =order.getProductList().get(i);
			productStr.append(od.getSkuCode()					
					+OrderConst.SecondSplitStr+od.getSkuPrice()
					+OrderConst.SecondSplitStr+od.getSkuNum()
					+OrderConst.SecondSplitStr+od.getProductCode()
					+OrderConst.SecondSplitStr+od.getSkuName().replace(OrderConst.SecondSplitStr, "").replace(OrderConst.FirstSplitStr, "")
					+OrderConst.SecondSplitStr+od.getProductPicUrl().replace(OrderConst.SecondSplitStr, "").replace(OrderConst.FirstSplitStr, ""));
			
			if(i != order.getProductList().size()-1)
				productStr.append(OrderConst.FirstSplitStr);
			
		}
		
		cstmt.setString(28, productStr.toString());
		
		StringBuffer activityStr  = new StringBuffer();
		
		if(order.getActivityList()!=null)
		{
			for(int i=0;i<order.getActivityList().size();i++)
			{
				OcOrderActivity od =order.getActivityList().get(i);
				activityStr.append(od.getProductCode()
						+OrderConst.SecondSplitStr+od.getSkuCode()
						+OrderConst.SecondSplitStr+od.getActivityCode()
						+OrderConst.SecondSplitStr+od.getActivityType()
						+OrderConst.SecondSplitStr+String.valueOf(od.getPreferentialMoney()));
				
				if(i != order.getActivityList().size()-1)
					activityStr.append(OrderConst.FirstSplitStr);
			}
		}
		
		cstmt.setString(29, activityStr.toString());
		cstmt.setBigDecimal(30, order.getFreeTransportMoney());
		cstmt.setBigDecimal(31, order.getDueMoney());
		
		
		StringBuffer payStr  = new StringBuffer();
		

		if(order.getOcOrderPayList()!=null)
		{
			for(int i=0;i<order.getOcOrderPayList().size();i++)
			{
				OcOrderPay od =order.getOcOrderPayList().get(i);
				payStr.append(od.getPaySequenceid()
						+OrderConst.SecondSplitStr+od.getPayType()
						+OrderConst.SecondSplitStr+od.getPayedMoney()
						+OrderConst.SecondSplitStr+od.getPayRemark().replace(OrderConst.SecondSplitStr, "").replace(OrderConst.FirstSplitStr, ""));
				
				if(i != order.getOcOrderPayList().size()-1)
					payStr.append(OrderConst.FirstSplitStr);
			}
		}
		
		
		
		cstmt.setString(32, payStr.toString());
		
		cstmt.setString(33, this.order.getAddress().getInvoiceType());
		cstmt.setString(34, this.order.getAddress().getInvoiceContent());
		cstmt.setString(35, this.order.getOrderChannel());
		
		cstmt.setString(36, OrderConst.FirstSplitStr);
		cstmt.setString(37, OrderConst.SecondSplitStr);
		
		return cstmt;
	}
}