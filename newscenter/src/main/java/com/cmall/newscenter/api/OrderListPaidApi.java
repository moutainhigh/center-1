package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.OrderListPaidInput;
import com.cmall.newscenter.model.OrderListPaidResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.model.SaleOrder;
import com.cmall.newscenter.model.SaleProductGroup;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 订单-已付款订单
 * 
 * @author gz date 2014-8-23
 * @version 1.0
 */
public class OrderListPaidApi extends RootApiForToken<OrderListPaidResult, OrderListPaidInput> {

	public OrderListPaidResult Process(OrderListPaidInput inputParam,MDataMap mRequestMap) {

		OrderListPaidResult result = new OrderListPaidResult();
		if (result.upFlagTrue()) {
			OrderService orderService = new OrderService();
			List<Order> orderList = orderService.getOrderListByBuyerAndOrderChannel(getManageCode(),getUserCode(), "", "", "449715200005", "");
			if(orderList.size() != 0){
				// 商品总数
				int totalNum = orderList.size();
				int offset = inputParam.getPaging().getOffset();// 起始页
				int limit = inputParam.getPaging().getLimit();// 每页条数
				int startNum = limit * offset;// 开始条数
				int endNum = startNum + limit;// 结束条数
				int more = 1;// 有更多数据
				if (endNum > totalNum) {
					endNum = totalNum;
					more = 0;
				}
				//如果起始条件大于总数则返回0条数据
				if(startNum>totalNum){
					startNum = 0;
					endNum = 0;
					more = 0;
				}
				// 分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum - startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				List<Order> subList = orderList.subList(startNum, endNum);
				if(subList.size() !=0){
					for (Order order : subList) {
						// order中信息
						SaleOrder saleOrder = new SaleOrder();
						saleOrder.setId(order.getOrderCode());// id
						saleOrder.setOrder_id(order.getOrderCode());// 订单号
						saleOrder.setTotal(Double.parseDouble(String.valueOf(order.getOrderMoney())));// 总价
						
						saleOrder.setCreate_time(order.getCreateTime());// 创建时间
						// products中信息
						List<OrderDetail> detailList = order.getProductList();
						
						List<SaleProductGroup> products = new ArrayList<SaleProductGroup>();
						
						if(detailList.size() !=0){
							for (OrderDetail orderDetail : detailList) {
								SaleProductGroup product = new SaleProductGroup();
								product.setAmout(orderDetail.getSkuNum());// 数量
								FuncQueryProductInfo funcQueryProductInfo = new FuncQueryProductInfo();
								List<Sale_Product> saleProduct = funcQueryProductInfo.qryOrderProInSaleService(orderDetail.getSkuCode(),orderDetail.getProductCode(),getUserCode(),getManageCode(),order.getOrderCode());
								//一个商品的sku对应该一个商品信息
								Sale_Product sale_Product = saleProduct.get(0);
								product.setProduct(sale_Product);
								
								products.add(product);
								
							}
							
							BigInteger state =BigInteger.valueOf(Long.valueOf(order.getOrderStatus()));
							
							List<MDataMap> mDataMap = new ArrayList<MDataMap>();
							
							 mDataMap =  DbUp.upTable("nc_order_evaluation").queryByWhere("order_code",order.getOrderCode(),"manage_code",getManageCode());
							
							if(mDataMap.size()!=0&&detailList.size()!=0){
								
							 if(mDataMap.size()==detailList.size()){
								 
								 saleOrder.setState(2);// 状态 -- 完成
							 }
								
							}else{
							
							
							if(String.valueOf(state).equals("4497153900010003")||String.valueOf(state).equals("4497153900010002")){
								
								saleOrder.setState(0);// 状态--发货中
								
							} else if (String.valueOf(state).equals("4497153900010004")||String.valueOf(state).equals("4497153900010005")){
								
								saleOrder.setState(1);// 状态 -- 待评价
								
							}
								
								
							}

							
							saleOrder.setProducts(products);
						}
						result.getOrders().add(saleOrder);
					}
				}
			}
		}
		return result;
	}
}
