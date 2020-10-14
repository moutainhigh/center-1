package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.model.ExchangegoodsForCC;
import com.cmall.ordercenter.model.ExchangegoodsStatusLogForCC;
import com.cmall.ordercenter.model.ExpressForCC;
import com.cmall.ordercenter.model.LcOrderStatusForCC;
import com.cmall.ordercenter.model.OcOrderActivityForCC;
import com.cmall.ordercenter.model.OcOrderPayForCC;
import com.cmall.ordercenter.model.OcOrderShipmentsForCC;
import com.cmall.ordercenter.model.OrderAddressForCC;
import com.cmall.ordercenter.model.OrderDetailForCC;
import com.cmall.ordercenter.model.OrderInfoForCC;
import com.cmall.ordercenter.model.OrderRemarkForCC;
import com.cmall.ordercenter.model.PcProductinfoExt;
import com.cmall.ordercenter.model.ProductInfoForCC;
import com.cmall.ordercenter.model.ProductSkuInfoForCC;
import com.cmall.ordercenter.model.PropertyValueInfo;
import com.cmall.ordercenter.model.ReturnGoodsForCC;
import com.cmall.ordercenter.model.ReturnGoodsLogForCC;
import com.cmall.ordercenter.model.ReturnMoneyForCC;
import com.cmall.ordercenter.model.ReturnMoneyLogForCC;
import com.cmall.ordercenter.model.PropertyInfoForProtuct;
import com.cmall.ordercenter.model.PicInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：ordercenter 类名称：OrderInfoServiceForCC
 *  类描述：根据查询条件为客服系统返回结果订单信息 
 *  创建人：zhaoxq 
 *  创建时间：2015-10-16
 * 
 * @version
 * 
 */
public class OrderInfoServiceForCC extends BaseClass {
	/**
	 * 客服系统订单查询
	 * @param orderCode 订单编号
	 * @param outOrderCode 外部订单编号
	 * @param bigOrderCode 支付订单编号
	 * @param orderStatus 订单状态
	 * @param orderStatusExt 订单辅助状态
	 * @param createTimeStart 创建时间
	 * @param createTimeEnd 创建时间
	 * @param registerMobile 注册手机号
	 * @param receivePerson 收货人
	 * @param mobilePhone 收货人手机号
	 * @param address 收货地址
	 * @param productName 商品名称
	 * @param waybill 物流单号
	 * @return List<OrderInfoForCC>
	 */
	public List<OrderInfoForCC> getOrderListForCC(String orderCode,String outOrderCode,
			String bigOrderCode,String orderStatus,String orderStatusExt,
			String createTimeStart,String createTimeEnd,String registerMobile,
			String receivePerson,String mobilePhone,String address,
			String productName,String waybill) {
		MDataMap mapParam = new MDataMap();
		StringBuffer whereSql = new StringBuffer();

		String fromSql = " ordercenter.oc_orderinfo,membercenter.mc_login_info";
		//注册手机号
		whereSql.append(" and mc_login_info.member_code = oc_orderinfo.buyer_code");
		boolean isaccurateSel = false;
		//收货表查询条件
		if(!StringUtils.isEmpty(receivePerson) || !StringUtils.isEmpty(mobilePhone)
				|| !StringUtils.isEmpty(address)){
			fromSql = fromSql + " ,ordercenter.oc_orderadress";
			whereSql.append(" and oc_orderadress.order_code = oc_orderinfo.order_code");
		}
		
		//物流信息查询条件
		if(!StringUtils.isEmpty(waybill)){
			fromSql = fromSql + " ,ordercenter.oc_order_shipments";
			whereSql.append(" and oc_order_shipments.order_code = oc_orderinfo.order_code");
		}

		//订单编号
		if(!StringUtils.isEmpty(orderCode)){
			whereSql.append(" and oc_orderinfo.order_code=:order_code");
			mapParam.put("order_code", orderCode);
			isaccurateSel = true;
		}
		//外部订单编号
		if(!StringUtils.isEmpty(outOrderCode)){
			whereSql.append(" and oc_orderinfo.out_order_code=:out_order_code");
			mapParam.put("out_order_code", outOrderCode);
			isaccurateSel = true;
		}
		//支付订单编号
		if(!StringUtils.isEmpty(bigOrderCode)){
			whereSql.append(" and oc_orderinfo.big_order_code=:big_order_code");
			mapParam.put("big_order_code", bigOrderCode);
			isaccurateSel = true;
		}
		//订单状态
		if(!StringUtils.isEmpty(orderStatus)){
			whereSql.append(" and oc_orderinfo.order_status=:order_status");
			mapParam.put("order_status", orderStatus);
			isaccurateSel = true;
		}
		//订单辅助状态
		if(!StringUtils.isEmpty(orderStatusExt)){
			whereSql.append(" and oc_orderinfo.order_status_ext=:order_status_ext");
			mapParam.put("order_status_ext", orderStatusExt);
			isaccurateSel = true;
		}
		//创建时间开始
		if(!StringUtils.isEmpty(createTimeStart)){
			String startTime = createTimeStart+" 00:00:00";
			whereSql.append(" and oc_orderinfo.create_time >= :create_time_start");
			mapParam.put("create_time_start", startTime);
		}
		//创建时间结束
		if(!StringUtils.isEmpty(createTimeEnd)){
			String endTime = createTimeEnd+" 23:59:59";
			whereSql.append(" and oc_orderinfo.create_time <= :create_time_end");
			mapParam.put("create_time_end", endTime);
		}
		//注册手机号
		if( !StringUtils.isEmpty(registerMobile)){
			whereSql.append(" and mc_login_info.login_name=:registerMobile");
			mapParam.put("registerMobile", registerMobile);
			isaccurateSel = true;
		}
		
		//收货人
		if(!StringUtils.isEmpty(receivePerson)){
			whereSql.append(" and oc_orderadress.receive_person=:receive_person");
			mapParam.put("receive_person", receivePerson);
			isaccurateSel = true;
		}
		//收货人手机号
		if(!StringUtils.isEmpty(mobilePhone)){
			whereSql.append(" and (oc_orderadress.mobilephone=:mobilephone");
			whereSql.append(" or oc_orderadress.telephone=:telephone)");
			mapParam.put("mobilephone", mobilePhone);
			mapParam.put("telephone", mobilePhone);
			isaccurateSel = true;
		}
		//收货地址
		if(!StringUtils.isEmpty(address)){
			whereSql.append(" and locate(:address, oc_orderadress.address)>0 ");
			mapParam.put("address", address);
		}
		//商品名称
		if(!StringUtils.isEmpty(productName)){
			whereSql.append(" and locate(:product_name,oc_orderinfo.product_name)>0");
			mapParam.put("product_name", productName);
		}
		
		//物流信息查询条件
		if(!StringUtils.isEmpty(waybill)){
			whereSql.append(" and oc_order_shipments.waybill=:waybill");
			mapParam.put("waybill", waybill);
			isaccurateSel = true;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("select oc_orderinfo.order_code, oc_orderinfo.order_source,");
		sql.append("oc_orderinfo.order_type, oc_orderinfo.order_status,");
		sql.append("oc_orderinfo.seller_code, oc_orderinfo.buyer_code,");
		sql.append("oc_orderinfo.pay_type, oc_orderinfo.send_type,");
		sql.append("(oc_orderinfo.transport_money-free_transport_money) as transport_money,");
		sql.append("oc_orderinfo.order_money,oc_orderinfo.order_channel,");
		sql.append("oc_orderinfo.pay_type, oc_orderinfo.send_type,");
		sql.append("oc_orderinfo.due_money, oc_orderinfo.payed_money,");
		sql.append("oc_orderinfo.create_time, oc_orderinfo.update_time,");
		sql.append("oc_orderinfo.out_order_code,oc_orderinfo.order_status_ext,");
		sql.append("oc_orderinfo.seller_code,mc_login_info.login_name,");
		sql.append("oc_orderinfo.small_seller_code,oc_orderinfo.big_order_code");
		sql.append(" from ").append(fromSql);
		sql.append(" where 1=1 ").append(whereSql.toString());
		sql.append(" order by oc_orderinfo.create_time desc");
		if(!isaccurateSel){
			sql.append(" limit 1,10");
		}
		List<Map<String,Object>> orderListMap = DbUp
				.upTable("oc_orderinfo").dataSqlList(
						sql.toString(), mapParam);
		List<OrderInfoForCC> orderList = new ArrayList<OrderInfoForCC>();
		for (Map<String, Object> map : orderListMap) {
			OrderInfoForCC orderInfo = new OrderInfoForCC();
			orderInfo.setCreateTime(map.get("create_time") == null?
					"":map.get("create_time").toString());
			orderInfo.setOrderCode(map.get("order_code").toString());
			orderInfo.setOrderSource(map.get("order_source") == null?
					"":map.get("order_source").toString());
			orderInfo.setOrderType(map.get("order_type") == null?
					"":map.get("order_type").toString());
			orderInfo.setOrderStatus(map.get("order_status") == null?
					"":map.get("order_status").toString());
			orderInfo.setOrderChannel(map.get("seller_code") == null?
					"":map.get("seller_code").toString());
			orderInfo.setRegisterMobile(map.get("login_name") == null?
					"":map.get("login_name").toString());
			orderInfo.setPayType(map.get("pay_type") == null?
					"":map.get("pay_type").toString());
			orderInfo.setSendType(map.get("send_type") == null?
					"":map.get("send_type").toString());
			orderInfo.setTransportMoney(new BigDecimal(map.get("transport_money").toString()));
			orderInfo.setOrderMoney(new BigDecimal(map.get("order_money").toString()));
			orderInfo.setDueMoney(new BigDecimal(map.get("due_money").toString()));
			orderInfo.setPayedMoney(new BigDecimal(map.get("payed_money").toString()));
			orderInfo.setCreateTime(map.get("create_time") == null?
					"":map.get("create_time").toString());
			orderInfo.setUpdateTime(map.get("update_time") == null?
					"":map.get("update_time").toString());
			orderInfo.setOutOrderCode(map.get("out_order_code") == null?
					"":map.get("out_order_code").toString());
			orderInfo.setOrderStatusExt(map.get("order_status_ext") == null?
					"":map.get("order_status_ext").toString());
			orderInfo.setBigOrderCode(map.get("big_order_code") == null?
					"":map.get("big_order_code").toString());

			//设置发货商
			String smallSellerCode = "";
			if(map.get("small_seller_code") == null){
				smallSellerCode = "";
			}else{
				smallSellerCode = map.get("small_seller_code").toString();
			}
			if("".equals(smallSellerCode)|| "SI2003".equals(smallSellerCode)){
				orderInfo.setShipper("LD系统");
			}else if("SF03KJT".equals(smallSellerCode)){
				orderInfo.setShipper("跨境通");
			}else{
				orderInfo.setShipper("商户");
			}
			//设置商户名称
			if("".equals(smallSellerCode)){
				
			}else if("SF03KJT".equals(smallSellerCode)){
				orderInfo.setSellerName("跨境通");
			}else{
				//获取商户名称
				String sellerName=(String)DbUp.upTable("uc_sellerinfo").dataGet(
						"seller_name", "small_seller_code=:small_seller_code", 
						new MDataMap("small_seller_code",smallSellerCode));
				orderInfo.setSellerName(sellerName);
			}
			
			//设置商品详情
			setOrderDtailsForCC(orderInfo);
			//设置订单活动信息
			setOrderactivitysForCC(orderInfo);
			//设置订单配送信息
			setOrderAddressForCC(orderInfo);
			//设置订单支付信息
			setOrderaPaysForCC(orderInfo);
			//设置发货信息
			setOrderShipmentsForCC(orderInfo);
			//设置运单流水信息
			setOrderExpresssForCC(orderInfo);
			//设置日志流水信息
			setLcOrderStatusForCC(orderInfo);
			//设置订单备注信息
			setOrderRemarksForCC(orderInfo);
			//退货及日志流水信息
			setOrderReturnGoodsForCC(orderInfo); 
			//换货及日志流水信息
			setOrderExchangeGoodsForCC(orderInfo);
			//退款及日志流水信息
			setOrderReturnMoneyForCC(orderInfo);

			orderList.add(orderInfo);
	   }
	   return (List<OrderInfoForCC>) orderList;
	}
	
	/**
	 * 设置订单详情信息
	 * @param orderInfo OrderInfoForCC
	 * @return void
	 */
	private void setOrderDtailsForCC(OrderInfoForCC orderInfo){
		//取订单商品详细信息
		List<MDataMap> OrderDetails = DbUp.upTable("oc_orderdetail").queryAll(
				"order_code,sku_code,sku_name,product_code,sku_price,sku_num,save_amt","",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<OrderDetailForCC> orderDetailList = new ArrayList<OrderDetailForCC>();
		BigDecimal orderSaveAmt = new BigDecimal(0.00);
		if (OrderDetails != null) {
			for( MDataMap orderDetail:OrderDetails){
				orderSaveAmt = orderSaveAmt.add(new BigDecimal(orderDetail.get("save_amt")));
				SerializeSupport<OrderDetailForCC> ss = new SerializeSupport<OrderDetailForCC>();
				OrderDetailForCC od = new OrderDetailForCC();
				ss.serialize(orderDetail, od);
				MDataMap product = DbUp.upTable("pc_productinfo").one("product_code",od.getProductCode());
				if(product != null){
					od.setProductName(product.get("product_name"));
					if("LD系统".equals(orderInfo.getShipper())){
						if("Y".equals(product.get("validate_flag"))){
							orderInfo.setShipper("LD厂商");
						}else{
							orderInfo.setShipper("仓库");
						}
					}
				}
				MDataMap sku = DbUp.upTable("pc_skuinfo").one("sku_code",od.getSkuCode());
				if(sku != null){
					od.setSkuKeyValue(sku.get("sku_keyvalue"));
				}
				orderDetailList.add(od);
			}
			orderInfo.setOrderSaveAmt(orderSaveAmt);
			orderInfo.setProductList(orderDetailList);
		}
	}
	/**
	 * 设置订单活动信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderactivitysForCC(OrderInfoForCC orderInfo){
		//取订单活动
		List<MDataMap> activitys = DbUp.upTable("oc_order_activity").queryAll(
				"order_code,product_code,sku_code,preferential_money,activity_code,activity_type","",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<OcOrderActivityForCC> orderActivityList = new ArrayList<OcOrderActivityForCC>();;
		if (activitys != null) {
			for( MDataMap ocOrderActivity:activitys){
				SerializeSupport<OcOrderActivityForCC> ss = new SerializeSupport<OcOrderActivityForCC>();
				OcOrderActivityForCC oa = new OcOrderActivityForCC();
				ss.serialize(ocOrderActivity, oa);
				orderActivityList.add(oa);
			}
			orderInfo.setActivityList(orderActivityList);
		}
	}
	
	/**
	 * 设置订单配送信息
	 * @param orderInfo OrderInfoForCC
	 * @return 
	 */
	private void setOrderAddressForCC(OrderInfoForCC orderInfo){
		//取配送信息
		MDataMap orderAddress = DbUp.upTable("oc_orderadress").one("order_code",orderInfo.getOrderCode());
		if (orderAddress != null) {
				SerializeSupport<OrderAddressForCC> ss = new SerializeSupport<OrderAddressForCC>();
				OrderAddressForCC address = new OrderAddressForCC();
				ss.serialize(orderAddress,address);
				orderInfo.setAddress(address);
		}
	}
	
	/**
	 * 设置订单支付信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderaPaysForCC(OrderInfoForCC orderInfo){
		//取支付信息
		List<MDataMap> pays = DbUp.upTable("oc_order_pay").queryAll(
				"order_code,pay_sequenceid,payed_money,create_time,pay_type,pay_remark","create_time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		BigDecimal cgroupPayAmt = new BigDecimal(0.00);
		List<OcOrderPayForCC> orderPayList = new ArrayList<OcOrderPayForCC>();;
		if (pays != null) {
			for( MDataMap ocOrderPay:pays){
				SerializeSupport<OcOrderPayForCC> ss = new SerializeSupport<OcOrderPayForCC>();
				OcOrderPayForCC op = new OcOrderPayForCC();
				ss.serialize(ocOrderPay, op);
				//微公社支付类型
				if("449746280009".equals(op.getPayType())){
					cgroupPayAmt = cgroupPayAmt.add(op.getPayedMoney());
				}
				orderPayList.add(op);
			}
			//设置订单中微公社支付部分金额
			orderInfo.setCgroupPayAmt(cgroupPayAmt);
			orderInfo.setOcOrderPayList(orderPayList);
		}
	}
	
	/**
	 * 设置订单发货信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderShipmentsForCC(OrderInfoForCC orderInfo){
		//取发货信息
		MDataMap ordershipment = DbUp.upTable("oc_order_shipments").one("order_code",orderInfo.getOrderCode());
		if (ordershipment != null) {
				SerializeSupport<OcOrderShipmentsForCC> ss = new SerializeSupport<OcOrderShipmentsForCC>();
				OcOrderShipmentsForCC shipment = new OcOrderShipmentsForCC();
				ss.serialize(ordershipment,shipment);
				orderInfo.setOcorderShipments(shipment);
		}
	}
	
	/**
	 * 设置运单流水信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderExpresssForCC(OrderInfoForCC orderInfo){
		//取订单流水信息
		List<MDataMap> expresss = DbUp.upTable("oc_express_detail").queryAll(
				"order_code,waybill,context,time","time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<ExpressForCC> expressList = new ArrayList<ExpressForCC>();;
		if (expresss != null) {
			for(MDataMap orderExpress:expresss){
				SerializeSupport<ExpressForCC> ss = new SerializeSupport<ExpressForCC>();
				ExpressForCC express = new ExpressForCC();
				ss.serialize(orderExpress, express);
				expressList.add(express);
			}
			orderInfo.setExpressList(expressList);
		}
	}

	/**
	 * 设置日志流水信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setLcOrderStatusForCC(OrderInfoForCC orderInfo){
		//取订单日志流水信息
		List<MDataMap> logs = DbUp.upTable("lc_orderstatus").queryAll(
				"code,info,create_time,create_user,old_status,now_status","create_time desc",
				"code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<LcOrderStatusForCC> logList = new ArrayList<LcOrderStatusForCC>();;
		if (logs != null) {
			for(MDataMap log:logs){
				SerializeSupport<LcOrderStatusForCC> ss = new SerializeSupport<LcOrderStatusForCC>();
				LcOrderStatusForCC orderLog = new LcOrderStatusForCC();
				ss.serialize(log, orderLog);
				logList.add(orderLog);
			}
			orderInfo.setLcOrderStatusList(logList);
		}
	}
	
	/**
	 * 设置订单备注信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderRemarksForCC(OrderInfoForCC orderInfo){
		//取订单备注信息
		List<MDataMap> remarks = DbUp.upTable("oc_order_remark").queryAll(
				"order_code,remark,create_time,create_user_name","create_time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<OrderRemarkForCC> remarkList = new ArrayList<OrderRemarkForCC>();;
		if (remarks != null) {
			for(MDataMap remark:remarks){
				SerializeSupport<OrderRemarkForCC> ss = new SerializeSupport<OrderRemarkForCC>();
				OrderRemarkForCC orderRemark = new OrderRemarkForCC();
				ss.serialize(remark, orderRemark);
				remarkList.add(orderRemark);
			}
			orderInfo.setOrderRemarkList(remarkList);
		}
	}
	
	/**
	 * 设置订单退货信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderReturnGoodsForCC(OrderInfoForCC orderInfo){
		//取订单退货信息
		List<MDataMap> returnGoods = DbUp.upTable("oc_return_goods").queryAll(
				"return_code,order_code,return_reason,description,status,"
				+ "transport_people,seller_code,create_time,contacts,mobile,address","create_time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<ReturnGoodsForCC> returnGoodsList = new ArrayList<ReturnGoodsForCC>();
		List<ReturnGoodsLogForCC> returnGoodsLogList = new ArrayList<ReturnGoodsLogForCC>();
		if (returnGoods != null) {
			for(MDataMap returngood:returnGoods){
				SerializeSupport<ReturnGoodsForCC> ss = new SerializeSupport<ReturnGoodsForCC>();
				ReturnGoodsForCC orderReturnGoods = new ReturnGoodsForCC();
				ss.serialize(returngood, orderReturnGoods);
				//设置注册手机号
				orderReturnGoods.setRegisterMobile(orderInfo.getRegisterMobile());
				orderReturnGoods.setSellerName(orderInfo.getSellerName());
				returnGoodsList.add(orderReturnGoods);
				//设置退货日志流水信息
				List<MDataMap> returnGoodsLogs = DbUp.upTable("lc_return_goods_status").queryAll(
						"return_no,info,create_user,status,create_time","create_time desc",
						"return_no=:return_no", new MDataMap("return_no",orderReturnGoods.getReturnCode()));
				if (returnGoodsLogs != null) {
					for(MDataMap returngoodLog:returnGoodsLogs){
						SerializeSupport<ReturnGoodsLogForCC> ssLog = new SerializeSupport<ReturnGoodsLogForCC>();
						ReturnGoodsLogForCC orderReturnGoodsLog = new ReturnGoodsLogForCC();
						ssLog.serialize(returngoodLog, orderReturnGoodsLog);
						String createUserName = getCreatorName(orderReturnGoodsLog.getCreateUser());
						if(StringUtils.isNotEmpty(createUserName)){
							orderReturnGoodsLog.setCreateUser(createUserName);
						}
						returnGoodsLogList.add(orderReturnGoodsLog);
					}
				}
			}
			orderInfo.setReturngoodsList(returnGoodsList);
			orderInfo.setReturngoodsLogList(returnGoodsLogList);
		}
	}

	/**
	 * 设置换货信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderExchangeGoodsForCC(OrderInfoForCC orderInfo){
		//取订单换货信息
		List<MDataMap> exchangeGoods = DbUp.upTable("oc_exchange_goods").queryAll(
				"exchange_no,order_code,exchange_reason,description,status,"
				+ "transport_people,seller_code,create_time,contacts,mobile,address","create_time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		List<ExchangegoodsForCC> exchangeGoodsLogList = new ArrayList<ExchangegoodsForCC>();
		List<ExchangegoodsStatusLogForCC> changeGoodsLogList = new ArrayList<ExchangegoodsStatusLogForCC>();
		if (exchangeGoods != null) {
			for(MDataMap exchangeGood:exchangeGoods){
				SerializeSupport<ExchangegoodsForCC> ss = new SerializeSupport<ExchangegoodsForCC>();
				ExchangegoodsForCC orderExchangeGood = new ExchangegoodsForCC();
				ss.serialize(exchangeGood, orderExchangeGood);
				orderExchangeGood.setRegisterMobile(orderInfo.getRegisterMobile());
				orderExchangeGood.setSellerName(orderInfo.getSellerName());
				exchangeGoodsLogList.add(orderExchangeGood);
				//取订单换货信息
				List<MDataMap> changeGoodsLogs = DbUp.upTable("lc_exchangegoods").queryAll(
						"exchange_no,info,create_time,create_user,old_status,now_status","create_time desc",
						"exchange_no=:exchange_no", new MDataMap("exchange_no",orderExchangeGood.getExchangeNo()));
				//设置换货日志流水
				if (changeGoodsLogs != null) {
					for(MDataMap changeGoodsLog:changeGoodsLogs){
						SerializeSupport<ExchangegoodsStatusLogForCC> ssLog = new SerializeSupport<ExchangegoodsStatusLogForCC>();
						ExchangegoodsStatusLogForCC orderChangeGoodsLog = new ExchangegoodsStatusLogForCC();
						ssLog.serialize(changeGoodsLog, orderChangeGoodsLog);
						String createUserName = getCreatorName(orderChangeGoodsLog.getCreateUser());
						if(StringUtils.isNotEmpty(createUserName)){
							orderChangeGoodsLog.setCreateUser(createUserName);
						}
						changeGoodsLogList.add(orderChangeGoodsLog);
					}
				}
			}
			orderInfo.setChangegoodsList(exchangeGoodsLogList);
			orderInfo.setChangegoodsLogList(changeGoodsLogList);
		}
	}
	
	/**
	 * 设置退款信息
	 * @param orderInfo OrderInfoForCC
	 * @return
	 */
	private void setOrderReturnMoneyForCC(OrderInfoForCC orderInfo){
		//取订单退款信息
		List<MDataMap> returnMoneys = DbUp.upTable("oc_return_money").queryAll(
				" return_money_code,order_code,seller_code,contacts,mobile,status,return_money,create_time","create_time desc",
				"order_code=:order_code", new MDataMap("order_code",orderInfo.getOrderCode()));
		if (returnMoneys != null) {
			List<ReturnMoneyForCC> returnMoneyList = new ArrayList<ReturnMoneyForCC>();
			List<ReturnMoneyLogForCC> returnMoneyLogList = new ArrayList<ReturnMoneyLogForCC>();
			for(MDataMap returnMoney:returnMoneys){
				SerializeSupport<ReturnMoneyForCC> ss = new SerializeSupport<ReturnMoneyForCC>();
				ReturnMoneyForCC orderReturnMoney = new ReturnMoneyForCC();
				ss.serialize(returnMoney, orderReturnMoney);
				orderReturnMoney.setRegisterMobile(orderInfo.getRegisterMobile());
				orderReturnMoney.setSellerName(orderInfo.getSellerName());
				returnMoneyList.add(orderReturnMoney);
				//取订单退款日志流水
				List<MDataMap> returnMoneyLogs = DbUp.upTable("lc_return_money_status").queryAll(
						"return_money_no,info,create_time,create_user,status","create_time desc",
						"return_money_no=:return_money_no", 
						new MDataMap("return_money_no",orderReturnMoney.getReturnMoneyCode()));
				if (returnMoneyLogs != null) {
					for(MDataMap returnMoneyLog:returnMoneyLogs){
						SerializeSupport<ReturnMoneyLogForCC> ssLog = new SerializeSupport<ReturnMoneyLogForCC>();
						ReturnMoneyLogForCC orderReturnMoneyLog = new ReturnMoneyLogForCC();
						ssLog.serialize(returnMoneyLog, orderReturnMoneyLog);
						String createUserName = getCreatorName(orderReturnMoneyLog.getCreateUser());
						if(StringUtils.isNotEmpty(createUserName)){
							orderReturnMoneyLog.setCreateUser(createUserName);
						}
						returnMoneyLogList.add(orderReturnMoneyLog);
					}
				}
			}
			orderInfo.setReturnMoneyList(returnMoneyList);
			orderInfo.setReturnMoneyLogList(returnMoneyLogList);
		}
	}
	
	/**
	 * 客服系统获取商品详情 
	 * @param productCode 商品编码
	 * @param productName 商品名称
	 * @param skuCode	商品SKU编码
	 * @param productStatus	商品状态
	 * @param sellerCode 商户编号
	 * @param sellerName 商户名称
	 * @param validateFlag 商品类型
	 * @param productCategory 商品分类
	 * @param brandCode 商品品牌
	 * @param labels 商品规格
	 * @param keyword 商品关键词
	 * @param minSellPrice 商品价格区间最小
	 * @param maxSellPrice 商品价格区间最大
	 * @return List<ProductInfoForCC>
	 */
	public List<ProductInfoForCC> getProductInfoForCC(String productCode,String productName,
			String skuCode,String productStatus,String sellerCode,String sellerName,
			String validateFlag,String productCategory,	String brandCode,String labels,
			String keyword,BigDecimal minSellPrice,BigDecimal maxSellPrice){
		StringBuffer whereSql = new StringBuffer();

		String fromSql = " productcenter.pc_productinfo";
		//商户名称
		if(!StringUtils.isEmpty(sellerName)){
			fromSql = fromSql + " ,usercenter.uc_sellerinfo";
			whereSql.append(" and pc_productinfo.seller_code=uc_sellerinfo.seller_code");
		}
		//关键词
		if(!StringUtils.isEmpty(keyword)){
			fromSql = fromSql + " ,productcenter.pc_productdescription";
			whereSql.append(" and pc_productinfo.product_code=pc_productdescription.product_code");
		}
		boolean isaccurateSel = false;
		//商品SKU编码
		if(!StringUtils.isEmpty(skuCode)){
			fromSql = fromSql + " ,productcenter.pc_skuinfo";
			whereSql.append(" and pc_productinfo.product_code=pc_skuinfo.product_code");
		}
		
		MDataMap mapParam = new MDataMap();
		//商品编码
		if(!StringUtils.isEmpty(productCode)){
			whereSql.append(" and pc_productinfo.product_code=:product_code");
			mapParam.put("product_code", productCode);
			isaccurateSel = true;
		}
		//商品名称
		if(!StringUtils.isEmpty(productName)){
			whereSql.append(" and locate(:product_name,pc_productinfo.product_name)>0");
			mapParam.put("product_name", productName);
		}
		//商品SKU编码
		if(!StringUtils.isEmpty(skuCode)){
			whereSql.append(" and pc_skuinfo.sku_code=:sku_code");
			mapParam.put("sku_code", skuCode);
			isaccurateSel = true;
		}
		//商品状态
		if(!StringUtils.isEmpty(productStatus)){
			whereSql.append(" and pc_productinfo.product_status=:product_status");
			mapParam.put("product_status", productStatus);
		}
		//商户编号
		if(!StringUtils.isEmpty(sellerCode)){
			whereSql.append(" and pc_productinfo.seller_code=:seller_code");
			mapParam.put("seller_code", sellerCode);
		}
		//商户名称
		if(!StringUtils.isEmpty(sellerName)){
			whereSql.append(" and locate(:seller_name,uc_sellerinfo.seller_name)>0");
			mapParam.put("seller_name", sellerName);
		}
		//商品类型
		if(!StringUtils.isEmpty(validateFlag)){
			whereSql.append(" and pc_productinfo.validate_flag=:validate_flag");
			mapParam.put("validate_flag", validateFlag);
		}
		//商品分类
		if(!StringUtils.isEmpty(productCategory)){
			fromSql = fromSql + " ,usercenter.uc_sellercategory_product_relation";
			whereSql.append(" and pc_productinfo.product_code=uc_sellercategory_product_relation.product_code");
			whereSql.append(" and pc_productinfo.seller_code=uc_sellercategory_product_relation.seller_code");
			whereSql.append(" and locate(:category_code,uc_sellercategory_product_relation.category_code)>0");
			mapParam.put("category_code", productCategory);
		}
		//商品品牌
		if(!StringUtils.isEmpty(brandCode)){
			whereSql.append(" and pc_productinfo.brand_code=:brand_code");
			mapParam.put("brand_code", brandCode);
		}
		//商品关键词
		if(!StringUtils.isEmpty(labels)){
			whereSql.append(" and pc_productinfo.labels like '%")
			.append(labels).append("%'");
		}
		//商品标签
		if(!StringUtils.isEmpty(keyword)){
			whereSql.append(" and pc_productdescription.keyword like'%")
			.append(keyword).append("%'");
		}
		//商品价格区间最小
		if(!StringUtils.isEmpty(minSellPrice.toString()) 
				&& minSellPrice.compareTo(new BigDecimal(0.00))>0){
			whereSql.append(" and pc_productinfo.min_sell_price>=:min_sell_price");
			mapParam.put("min_sell_price", minSellPrice.toString());
		}
		//商品价格区间最大
		if(!StringUtils.isEmpty(maxSellPrice.toString())
				&& maxSellPrice.compareTo(new BigDecimal(0.00))>0){
			whereSql.append(" and pc_productinfo.min_sell_price<=:max_sell_price");
			mapParam.put("max_sell_price", maxSellPrice.toString());
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select pc_productinfo.product_code,pc_productinfo.product_name,");
		sql.append("pc_productinfo.min_sell_price,pc_productinfo.seller_code,");
		sql.append("pc_productinfo.product_status,pc_productinfo.product_shortname,");
		sql.append("pc_productinfo.market_price,pc_productinfo.brand_code,");
		sql.append("pc_productinfo.product_weight,pc_productinfo.product_volume,");
		sql.append("pc_productinfo.transport_template,pc_productinfo.mainpic_url,");
		sql.append("pc_productinfo.labels,pc_productinfo.validate_flag,");
		sql.append("pc_productinfo.sell_productcode,pc_productinfo.video_url,");
		sql.append("pc_productinfo.create_time,pc_productinfo.update_time,");
		sql.append("pc_productinfo.flag_payway,pc_productinfo.small_seller_code");
		sql.append(" from ").append(fromSql);
		sql.append(" where 1=1 ").append(whereSql.toString());
		sql.append(" order by pc_productinfo.update_time desc");
		if(!isaccurateSel){
			sql.append(" limit 1,100");
		}
		List<Map<String,Object>> productListMap = DbUp
				.upTable("pc_productinfo").dataSqlList(
						sql.toString(), mapParam);
		List<ProductInfoForCC> productList = new ArrayList<ProductInfoForCC>();
		for (Map<String, Object> map : productListMap) {
			ProductInfoForCC productInfo = new ProductInfoForCC();
			productInfo.setProductCode(map.get("product_code").toString());
			productInfo.setProductName(map.get("product_name") == null?
					"":map.get("product_name").toString());
			productInfo.setSellPrice(new BigDecimal(map.get("min_sell_price").toString()));
			productInfo.setValidateFlag(map.get("validate_flag") == null?
					"":map.get("validate_flag").toString());
			productInfo.setProductStatus(map.get("product_status") == null?
					"":map.get("product_status").toString());
			productInfo.setProductShortname(map.get("product_shortname")== null?
					"":map.get("product_shortname").toString());
			productInfo.setMarketPrice(new BigDecimal(map.get("market_price").toString()));
			if(map.get("brand_code")!= null){
				MDataMap brandMapParam = new MDataMap();
				brandMapParam = DbUp.upTable("pc_brandinfo").one("brand_code",
						map.get("brand_code").toString());
				if (brandMapParam != null) {
					productInfo.setBrandCode(brandMapParam.get("brand_name"));
				}
			}
			productInfo.setProductWeight(map.get("product_weight") == null?
					"":map.get("product_weight").toString());
			productInfo.setProductVolume(new BigDecimal(map.get("product_volume").toString()));
			//设置运费模板
			String trans = "";
			if(map.get("transport_template") != null){
				trans = map.get("transport_template").toString();
				productInfo.setTransportTemplate(trans);
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(trans);
				if(!isNum.matches()){
					MDataMap mWhereMap = new MDataMap();
					mWhereMap.put("uid", trans);
					Map<String, Object> transmap = DbUp.upTable("uc_freight_tpl")
						.dataSqlOne("select tpl_name from uc_freight_tpl where uid=:uid", mWhereMap);
					String tplName = "";
					if(transmap!=null&&!transmap.isEmpty()){
						if(transmap.get("tpl_name") != null){
							tplName = transmap.get("tpl_name").toString();
							productInfo.setTransportTemplate(tplName);
						}else{
							productInfo.setTransportTemplate("");
						}
					}	
				}
			}
			productInfo.setLabels(map.get("labels") == null?
					"":map.get("labels").toString());
			productInfo.setSellProductcode(map.get("sell_productcode") == null?
					"":map.get("sell_productcode").toString());
			productInfo.setVideoUrl(map.get("video_url") == null?
					"":map.get("video_url").toString());
		
			productInfo.setCreateTime(map.get("create_time") == null?
					"":map.get("create_time").toString());
			productInfo.setUpdateTime(map.get("update_time") == null?
					"":map.get("update_time").toString());
			if("SI2003".equals(map.get("small_seller_code")) 
					&& "N".equals(map.get("validate_flag"))){
				productInfo.setFlagPayway("1");
			}else{
				productInfo.setFlagPayway("0");
			}
			
			MDataMap productExtMap  = DbUp.upTable("pc_productinfo_ext").one("product_code",productInfo.getProductCode());
			if(productExtMap!=null && !productExtMap.isEmpty()){
				SerializeSupport<PcProductinfoExt> ss = new SerializeSupport<PcProductinfoExt>();
				PcProductinfoExt productExt = new PcProductinfoExt();
				ss.serialize(productExtMap, productExt);
				if (!StringUtils.isEmpty(productExt.getPrchType())) {
					//	10-商品中心一地入库
					//	20-网站一地入库
					//		00-非一地入库
					String prchTypeCode = productExt.getPrchType();
					String oaSiteNo = productExt.getOaSiteNo();
					if ("10".equals(prchTypeCode)) {
						productInfo.setPrchType("商品中心一地入库");
						productInfo.setOaSiteNo(oaSiteNo);
					}else if ("20".equals(prchTypeCode)) {
						productInfo.setPrchType("网站一地入库");
						productInfo.setOaSiteNo(oaSiteNo);
					}else if ("00".equals(prchTypeCode)) {
						productInfo.setPrchType("非一地入库");
//						productInfo.setOaSiteNo("C01,C02,C04,C10");
						productInfo.setOaSiteNo(oaSiteNo);
					}
				}
				productInfo.setDlrid(productExt.getDlrId());
				productInfo.setDlrNm(productExt.getDlrNm());
				productInfo.setPurchaseType(productExt.getPurchaseType());
				productInfo.setSettlementType(productExt.getSettlementType());
				productInfo.setFictitiousSales(productExt.getFictitiousSales());
			}
			
			//主图
			if(map.get("mainpic_url") != null && !"".equals(map.get("mainpic_url").toString())){
				String mainPicUrl = map.get("mainpic_url").toString();
				PicInfo mainPic = new PicInfo();
				mainPic.setPicOldUrl(mainPicUrl);
				mainPic.setPicNewUrl(mainPicUrl);
				productInfo.setMainpicUrl(mainPic);
			}
			MDataMap descriptionMap = DbUp.upTable("pc_productdescription").one("product_code",productInfo.getProductCode());
			if(descriptionMap!= null){
				if(descriptionMap.get("description_info") != null){
					//商品描述
					productInfo.setDescriptInfo(descriptionMap.get("description_info").toString());
				}
				if(descriptionMap.get("keyword") != null){
					//商品标签
					productInfo.setKeyword(descriptionMap.get("keyword").toString());
				}
				if(!StringUtils.isEmpty(descriptionMap.get("description_pic"))){
					String[] descriptionPicArr = descriptionMap.get("description_pic").toString().split("\\|");
					for (String descriptionPic:descriptionPicArr) {	
						//描述图片
						PicInfo dspic = new PicInfo();
						dspic.setPicOldUrl(descriptionPic);
						dspic.setPicNewUrl(descriptionPic);
						productInfo.getDiscriptPicList().add(dspic);
					}
				}
			}
			
			//商品图片列表list
			List<MDataMap> picUrlsMap  = DbUp.upTable("pc_productpic").queryByWhere("product_code",productInfo.getProductCode());
			for (MDataMap picUrl : picUrlsMap) {
				if(!StringUtils.isEmpty(picUrl.get("pic_url"))){
					String picUrlStr = picUrl.get("pic_url").toString();
					PicInfo pic = new PicInfo();
					pic.setPicOldUrl(picUrlStr);
					pic.setPicNewUrl(picUrlStr);
					productInfo.getPcPicList().add(pic);
				}
			}
			
			//查询商品分类信息
			List<MDataMap> categoryListMap = DbUp.upTable("uc_sellercategory_product_relation").queryByWhere(
					"product_code",productInfo.getProductCode(),"seller_code",map.get("seller_code").toString());
			List<String> categoryList = new ArrayList<String>();
			if (categoryListMap != null) {
				for( MDataMap category:categoryListMap){
					String pc = category.get("category_code");
					categoryList.add(pc);
				}
				productInfo.setProductCategoryList(categoryList);
			}
			//查询出商品下的sku
			List<MDataMap> skuListMap = DbUp.upTable("pc_skuinfo").queryByWhere(
					"product_code",productInfo.getProductCode(),"seller_code",map.get("seller_code").toString());
			List<ProductSkuInfoForCC> productSkuList = new ArrayList<ProductSkuInfoForCC>();;
			if (productSkuList != null) {
				for( MDataMap sku:skuListMap){
					SerializeSupport<ProductSkuInfoForCC> ss = new SerializeSupport<ProductSkuInfoForCC>();
					ProductSkuInfoForCC skuInfo = new ProductSkuInfoForCC();
					ss.serialize(sku, skuInfo);
					productSkuList.add(skuInfo);
				}
				productInfo.setSkuList(productSkuList);
			}
			//设置商品规格list
			setProperty(productInfo,map.get("seller_code").toString());
			productList.add(productInfo);
		}
		
		return productList;
	}
	
	/**
	 * 设置商品规格list
	 * @param productInfo
	 * @param sellerCode
	 */
	private void setProperty(ProductInfoForCC productInfo,String sellerCode){
		// 商品规格List
		List<MDataMap> sInfoMap = DbUp.upTable("pc_skuinfo").queryByWhere(
				"product_code", productInfo.getProductCode(), "seller_code",
				sellerCode,"sale_yn","Y","flag_enable","1");
		MDataMap propertyMap = new MDataMap();

		for (MDataMap mDataMap : sInfoMap) {
			String proCodeStr = mDataMap.get("sku_key"); // 属性code
			String proValueStr = mDataMap.get("sku_keyvalue"); // 属性value

			if (null == proCodeStr || null == proValueStr
					|| "".equals(proCodeStr) || "".equals(proValueStr)) {
				continue;
			}
			// 获得不重复的key-value
			String[] propertiesCodeArr = proCodeStr.split("&");
			String[] propertiesValue = proValueStr.split("&");
			if(propertiesCodeArr != null && propertiesValue != null &&
					propertiesCodeArr.length == propertiesValue.length){
				for (int i = 0; i < propertiesCodeArr.length; i++) {
					propertyMap.put(propertiesCodeArr[i], propertiesValue[i]);
				}
			}
		}
		List<PropertyInfoForProtuct> propertyList = new ArrayList<PropertyInfoForProtuct>(); // keyObjList
		MDataMap proKeyMap = new MDataMap(); // keyMap
		String[] propertyMapKey = propertyMap.convertKeysToStrings();
		for (int i = 0; i < propertyMapKey.length; i++) { // 获得不重复的规格key
			String[] codesStr = propertyMapKey[i].split("=");
			String[] valueStr = propertyMap.get(propertyMapKey[i]).split("=");
			proKeyMap.put(codesStr[0], valueStr[0]); // key
		}
		for (String keyCode : proKeyMap.convertKeysToStrings()) {
			List<PropertyValueInfo> propertyValueList = new ArrayList<PropertyValueInfo>(); // valueObjList
			PropertyInfoForProtuct proCodeObj = new PropertyInfoForProtuct(); // keyObj
			for (int i = 0; i < propertyMapKey.length; i++) {
				PropertyValueInfo proValueObj = new PropertyValueInfo(); // valueObj
				String keyCodes = propertyMapKey[i];
				String[] codesStr = keyCodes.split("=");
				String[] valueStr = propertyMap.get(keyCodes).split("=");

				if (codesStr[0].equals(keyCode)) {
					proValueObj.setPropertyValueCode(codesStr[1]); // value
					proValueObj.setPropertyValueName(valueStr[1]);
					propertyValueList.add(proValueObj);
				}
			}
			// 商品规格属性值按照key进行字典排序
			if (null != propertyValueList && propertyValueList.size() > 1) {
				  Collections.sort(propertyValueList, new Comparator<PropertyValueInfo>() {
			            public int compare(PropertyValueInfo arg0, PropertyValueInfo arg1) {
			                return arg0.getPropertyValueName().compareTo(arg1.getPropertyValueName());
			            }
			        });
			}
			//商品详情接口商品规格排序按ASCII码升序排列
			//衣服尺码特别处理，按以下方式排序：XXS、XS、S、M、L、XL、XXL、XXXL、XXXXL、XXXXXL
			String[] specialProperties = new String[]{"XXS","XS","S","M"};						//需要进行特殊排序的衣服尺码
			String[] normalProperties = new String[]{"L","XL","XXL","XXXL","XXXXL","XXXXXL"};	//不需要进行特殊排序的衣服尺码
			boolean flagExistSpecial = false;		//标志是否含有需要特殊排序的衣服尺码
			boolean flagExistNormal = false;		//标志是否含有不需要特殊排序的衣服尺码
			List<PropertyValueInfo> specialPropertyInfoList = new ArrayList<PropertyValueInfo>();		//结果集中包含的需要进行特殊排序的衣服尺码数组
			List<Integer> specialIndexArr = new ArrayList<Integer>();									//结果集中包含的需要进行特殊排序的衣服尺码下标数组
			for (int j = 0; j < propertyValueList.size(); j++) {
				for (String specialProperty : specialProperties) {
					if (propertyValueList.get(j).getPropertyValueName().equals(specialProperty)) {
						flagExistSpecial = true;
						specialPropertyInfoList.add(propertyValueList.get(j));
						specialIndexArr.add(j);
					}
				}
			}
			if (flagExistSpecial) {
				int normalStrIndex = 0;													//含有的第一个不需要特殊排序的衣服尺码下标
				for (int j = 0; j < propertyValueList.size(); j++) {
					for (String normalProperty : normalProperties) {
						if (propertyValueList.get(j).getPropertyValueName().equals(normalProperty)) {
							flagExistNormal = true;
							normalStrIndex = j;
							break;
						}
					}
					if (flagExistNormal) break;
				}
				
				//开始进行排序,有个规律，需要进行特殊排序的衣服尺码都位于不需要进行特殊排序的衣服尺码前面，插入顺序为{M、S、XS、XXS}，插入的下标为normalStrIndex
				for (int i = specialProperties.length-1; i >= 0; i--) {
					for (int j = 0;j < specialPropertyInfoList.size();j++ ) {
						if (specialPropertyInfoList.get(j).getPropertyValueName().equals(specialProperties[i])) {
							propertyValueList.add(normalStrIndex, specialPropertyInfoList.get(j));
							propertyValueList.remove(specialIndexArr.get(j)+1);
						}
					}
				}
			}
			
			proCodeObj.setPropertyKeyCode(keyCode);
			proCodeObj.setPropertyKeyName(proKeyMap.get(keyCode));
			proCodeObj.setPropertyValueList(propertyValueList);
			propertyList.add(proCodeObj);
		}
		// 商品规格属性按照key进行字典排序
		if (null != propertyList && propertyList.size() > 1) {	
			  Collections.sort(propertyList, new Comparator<PropertyInfoForProtuct>() {
		            public int compare(PropertyInfoForProtuct arg0, PropertyInfoForProtuct arg1) {
		                return arg0.getPropertyKeyCode().compareTo(arg1.getPropertyKeyCode());
		            }
		        });
		}
		productInfo.setPropertyList(propertyList); // 商品规格值
	}
	/**
	 * 查看登录名
	 * @param member_code
	 * @return
	 */
	private String getCreatorName(String user_code) {
		String creatorName = "";
		if(StringUtils.isBlank(user_code)){
			return creatorName;
		}
		MDataMap dataMap = DbUp.upTable("za_userinfo").one("user_code",user_code);
		if(dataMap!=null){
			creatorName=dataMap.get("real_name");
		}
		return creatorName;
	}
}
