package com.cmall.newscenter.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.OrderForLastInput;
import com.cmall.newscenter.beauty.model.OrderForLastResult;
import com.cmall.newscenter.model.SaleProductGroup;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiOrderForLast extends RootApiForToken<OrderForLastResult, OrderForLastInput> {

	public OrderForLastResult Process(OrderForLastInput inputParam,
			MDataMap mRequestMap) {
		OrderForLastResult result = new OrderForLastResult();
		if (result.upFlagTrue()) {
			String sql = "select max(create_time) as create_time,order_code,order_money from oc_orderinfo where buyer_code='"+getUserCode()+"' and seller_code = '"+getManageCode()+"' and order_status = '4497153900010005'";
			 Map<String,Object> map = DbUp.upTable("oc_orderinfo").dataSqlOne(sql, new MDataMap());
			 if(map.get("order_code") !=null){
				 result.setId((String)map.get("order_code"));// id
				 result.setOrder_id((String)map.get("order_code"));// 订单号
				 result.setTotal(BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("order_money")))));// 总价
				 result.setCreate_time((String)map.get("create_time"));// 创建时间
				 List<MDataMap> mDataMap = new ArrayList<MDataMap>();
			  	 mDataMap =  DbUp.upTable("oc_orderdetail").queryByWhere("order_code",(String)map.get("order_code"));
				 for (MDataMap detailMap : mDataMap) {
					 SaleProductGroup product = new SaleProductGroup();
						product.setAmout(Integer.parseInt(detailMap.get("sku_num")));// 数量
						FuncQueryProductInfo funcQueryProductInfo = new FuncQueryProductInfo();
						List<Sale_Product> saleProduct = funcQueryProductInfo.qryOrderProInSaleService(detailMap.get("sku_code"),detailMap.get("product_code"),getUserCode(),getManageCode(),detailMap.get("order_code"));
						//一个商品的sku对应该一个商品信息
						Sale_Product sale_Product = saleProduct.get(0);
						product.setProduct(sale_Product);
						result.getProducts().add(product);
				 }
			 }
		}
		return result;
	}

}
