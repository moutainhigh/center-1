package com.cmall.groupcenter.func;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class OrderInformationService extends BaseClass {

	public List<OrderInformation> rsyncOrder(String shopId)
			throws ParseException {

		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();

		MDataMap dm = new MDataMap();

		Map<String, Object> map = new HashMap<String, Object>();

		List<OrderInformation> orderInfoList = new ArrayList<OrderInformation>();

		Order order = new Order();

		OrderService orderService = new OrderService();

		dm.put("order_status", "4497153900010002");

		dm.put("sellerCode", "SI2007");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		buyerList = DbUp
				.upTable("oc_orderinfo")
				.dataSqlList(
						"select order_code from ordercenter.oc_orderinfo where order_status=:order_status and"
								+ " seller_code=:sellerCode and order_code ='DD150205100455'  and  order_code not in "
								+ " (select request_ordercode from logcenter.lc_betabeauty_log where flag_success = '0') order by order_code ",
						dm);
		if (buyerList != null && !buyerList.isEmpty()) {

			for (int j = 0; j < buyerList.size(); j++) {

				map = buyerList.get(j);

				order = orderService.getOrder(String.valueOf(map
						.get("order_code"))); // 获取与订单相关内容的信息

				OrderInformation orderInfo = new OrderInformation();

				if (order != null) {

					List<OrderProcuct> product_info = new ArrayList<OrderProcuct>();

					/* 订单号 */
					orderInfo.setOut_tid(order.getOrderCode());
					/* 订单类型 */
					orderInfo.setOrder_type("正常订单");
					/* 订单店铺 */
					orderInfo.setShop_id(shopId);
					/* 仓库编号 */
					orderInfo.setStorage_id(1);
					/* 买家编号 */
					orderInfo.setBuyer_id(order.getBuyerCode());
					/* 支付方式 */
					orderInfo.setPay_method(order.getPayType());
					/* 配送方式 */
					orderInfo.setShip_method(order.getSendType());
					/* 运费 */
					orderInfo.setActual_freight_get(order.getTransportMoney()
							.doubleValue());
					/* 订单金额 */
					orderInfo.setOrder_totalMoney(order.getFreeTransportMoney()
							.doubleValue());
					/* 订单日期 */
					orderInfo.setOrder_date(order.getCreateTime().substring(0,
							10));
					/* 备注 */
					orderInfo.setBuyer_msg(order.getAddress().getRemark());
					/* 处理状态 */
					orderInfo.setProcess_status("未确认");
					/* 付款状态 */
					orderInfo.setPay_status("已付款");
					/* 发货状态 */
					orderInfo.setDeliver_status("未发货");
					/* 是否货到付款 */
					orderInfo.setIs_COD(0);
					/* 收货人姓名 */
					orderInfo.setConsignee(order.getAddress()
							.getReceivePerson());
					/* 联系人电话 */
					orderInfo
							.setMobilPhone(order.getAddress().getMobilephone());
					/* 省 */
					orderInfo.setPrivince("");
					/* 市 */
					orderInfo.setCity("");
					/* 区 */
					orderInfo.setArea("");
					/* 物流 */
					orderInfo.setWuLiu("申通");
					/*快递公司名称*/
					orderInfo.setExpress("申通");
					/* 收货地址 */
					orderInfo.setAddress(order.getAddress().getAddress());
					/* 收货人邮政编码 */
					orderInfo.setPostcode(order.getAddress().getPostCode());
					/* 发票类型 */
					orderInfo.setInvoice_type(order.getAddress()
							.getInvoiceType());
					/* 发票抬头 */
					orderInfo.setInvoice_title(order.getAddress()
							.getInvoiceTitle());
					/* 发票内容 */
					orderInfo.setInvoice_msg(order.getAddress()
							.getInvoiceContent());

					List<OrderDetail> productList = order.getProductList();
					if (productList != null && !productList.isEmpty()) {
						for (int i = 0; i < productList.size(); i++) {

							OrderProcuct product_item = new OrderProcuct();

							product_item.setOut_tid(order.getOrderCode());

//							product_item.setBarCode(order.getProductList()
//									.get(i).getSkuCode());// 货号sell_productcode

							
							Map<String, Object> skuMap = DbUp
									.upTable("pc_skuinfo")
									.dataSqlOne(
											"select sell_productcode from pc_skuinfo where sku_code=:sku_code ",
											new MDataMap("sku_code", order.getProductList().get(i).getSkuCode()));
							
							String sell_productcode = "";
							
							if(skuMap!=null){
							
								sell_productcode = skuMap.get("sell_productcode").toString();
								
							}
							
							product_item.setBarCode(sell_productcode);//货号sell_productcode
							
							
							/* 数量 */
							product_item.setOrderGoods_Num(order
									.getProductList().get(i).getSkuNum());
							/* 产品价格 */
							product_item.setOut_price(order.getProductList()
									.get(i).getSkuPrice().doubleValue());
							/* 产品名称 */
							product_item.setProduct_title(order
									.getProductList().get(i).getSkuName());
							/* 产品编号 */
							product_item.setOut_productId(order
									.getProductList().get(i).getSkuCode());

							product_item.setStandard("*");

							product_info.add(product_item);

						}
						orderInfo.setProduct_info(product_info);
					}

				}
				orderInfoList.add(orderInfo);
			}

		}

		return orderInfoList;
	}

}
