package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.OrderListTrialInput;
import com.cmall.newscenter.model.OrderListTrialResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.model.TrialOrder;
import com.cmall.newscenter.model.TrialProductGroup;
import com.cmall.newscenter.model.Trial_product;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 订单--已试用订单
 * 
 * @author gz date 2014-8-23
 * @version 1.0
 */
public class OrderListTrialApi extends RootApiForToken<OrderListTrialResult, OrderListTrialInput> {
	
	public OrderListTrialResult Process(OrderListTrialInput inputParam,MDataMap mRequestMap) {
		OrderListTrialResult result = new OrderListTrialResult();
		if (result.upFlagTrue()) {
			OrderService orderService = new OrderService();
			List<Order> orderList = orderService.getOrderListByBuyerAndOrderChannel(getManageCode(),getUserCode(), "", "", "449715200003", "");
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
				
				double total = 0.00;
				
				if(subList.size() !=0){
					for (Order order : subList) {
						// order中信息
						TrialOrder trialOrder = new TrialOrder();
						trialOrder.setId(order.getUid());// id
						trialOrder.setOrder_id(order.getOrderCode());// 订单号
						trialOrder.setCreate_time(order.getCreateTime());// 创建时间
						
						
						BigInteger state =BigInteger.valueOf(Long.valueOf(order.getOrderStatus()));
						
						
						
						
						// products中信息
						List<TrialProductGroup> products = new ArrayList<TrialProductGroup>();
						List<OrderDetail> detailList = order.getProductList();
						
						List<MDataMap> mDataMap = new ArrayList<MDataMap>();
						
						 mDataMap =  DbUp.upTable("nc_order_evaluation").queryByWhere("order_code",order.getOrderCode(),"manage_code",getManageCode());
						
						if(mDataMap.size()!=0&&detailList.size()!=0){
							
							if(mDataMap.size()==detailList.size()){
								
								trialOrder.setState(2);// 状态 -- 完成
							}
							
							
						}else{
						
						
						if(String.valueOf(state).equals("4497153900010003")||String.valueOf(state).equals("4497153900010002")){
							
							trialOrder.setState(0);// 状态--发货中
							
						} else if (String.valueOf(state).equals("4497153900010004")||String.valueOf(state).equals("4497153900010005")){
							
							trialOrder.setState(1);// 状态 -- 待评价
							
						}
							
						}
						
						double rial_price = 0.00;
						
						if(detailList.size() !=0){
							for (OrderDetail orderDetail : detailList) {
								TrialProductGroup product = new TrialProductGroup();
								product.setAmout(orderDetail.getSkuNum());// 数量
								FuncQueryProductInfo funcQueryProductInfo = new FuncQueryProductInfo();
								List<Trial_product> saleProduct = funcQueryProductInfo.qryOrderProInTryService(orderDetail.getSkuCode(),orderDetail.getProductCode(),getManageCode(),order.getOrderCode(),order);
								//一个商品的sku对应该一个商品信息
								Trial_product trial_product = saleProduct.get(0);
								product.setProduct(trial_product);
								products.add(product);
								
								rial_price =  order.getOcOrderPayList().get(0).getPayedMoney();
								
								
							}
							
							trialOrder.setTotal(rial_price);// 总价
							
							
							trialOrder.setProducts(products);
						}
						
						
						result.getOrders().add(trialOrder);
					}
				}
			}
		}
		return result;
	}
}
