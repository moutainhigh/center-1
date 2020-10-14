package com.cmall.ordercenter.service;



import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class OrderShoppingService extends BaseClass {
	/**
	 *根据订单号删除本订单对应的购物车中的商品 并  更新状态
	 */
	public boolean deleteSkuToShopCart(String orderCode){
		boolean flag = true;
		try {
			//区分大小订单
			if("DD".equals(orderCode.substring(0, 2))){
//				MDataMap updateMap = new MDataMap();
//				updateMap.put("order_code", orderCode);
//				updateMap.put("order_status", "4497153900010002");
//				updateMap.put("delete_flag", "0");
//				DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "order_code");
				MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				//更新订单状态
				this.updateOrderStatus(dm);
				OrderService service = new OrderService();
				Order order = service.getOrder(orderCode);
				List<OrderDetail> productList = order.getProductList();
				String buyerCode = order.getBuyerCode();
				if(productList!=null&&!productList.isEmpty()){
					for (int i = 0; i < productList.size(); i++) {
						OrderDetail de = productList.get(i);
						String skuCode = de.getSkuCode();
						DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
					}
				}
			}else if("OS".equals(orderCode.substring(0, 2))){
//				MDataMap updateMap = new MDataMap();
//				updateMap.put("big_order_code", orderCode);
//				updateMap.put("order_status", "4497153900010002");
//				updateMap.put("delete_flag", "0");
//				DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "big_order_code");
				List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code",orderCode));
				for(MDataMap map : list){
					//更新订单状态
					this.updateOrderStatus(map);
					OrderService service = new OrderService();
					Order order = service.getOrder(map.get("order_code"));
					List<OrderDetail> productList = order.getProductList();
					String buyerCode = order.getBuyerCode();
					if(productList!=null&&!productList.isEmpty()){
						for (int i = 0; i < productList.size(); i++) {
							OrderDetail de = productList.get(i);
							String skuCode = de.getSkuCode();
							DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	public void updateOrderStatus (MDataMap map){
		FlowBussinessService fs = new FlowBussinessService();
		String flowBussinessUid = map.get("uid");
		String fromStatus = map.get("order_status");
		String toStatus = "4497153900010002";//下单成功-未发货
		String flowType = "449715390008";
		String userCode = StringUtils.isEmpty(map.get("buyer_code"))?"system":map.get("buyer_code");
		String remark = "auto by system";
		MDataMap md = new MDataMap();
		md.put("order_code", map.get("order_code"));
		fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
				toStatus, userCode, remark, md);
	}
	/**
	 *根据订单号删除本订单对应的购物车中的商品 
	 */
	public boolean deleteSkuToShopCartNotstatus(String orderCode){
		boolean flag = true;
		try {
			//区分大小订单
			if("DD".equals(orderCode.substring(0, 2))){
				OrderService service = new OrderService();
				Order order = service.getOrder(orderCode);
				List<OrderDetail> productList = order.getProductList();
				String buyerCode = order.getBuyerCode();
				if(productList!=null&&!productList.isEmpty()){
					for (int i = 0; i < productList.size(); i++) {
						OrderDetail de = productList.get(i);
						String skuCode = de.getSkuCode();
						DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
					}
				}
			}else if("OS".equals(orderCode.substring(0, 2))){
				List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code",orderCode));
				for(MDataMap map : list){
					OrderService service = new OrderService();
					Order order = service.getOrder(map.get("order_code"));
					List<OrderDetail> productList = order.getProductList();
					String buyerCode = order.getBuyerCode();
					if(productList!=null&&!productList.isEmpty()){
						for (int i = 0; i < productList.size(); i++) {
							OrderDetail de = productList.get(i);
							String skuCode = de.getSkuCode();
							DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
}
