package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.kjt.OrderSoCreate;
import com.cmall.groupcenter.kjt.model.SOAuthenticationInfo;
import com.cmall.groupcenter.kjt.model.SOItemInfo;
import com.cmall.groupcenter.kjt.model.SOPayInfo;
import com.cmall.groupcenter.kjt.model.SOShippingInfo;
import com.cmall.groupcenter.kjt.request.RsyncRequestOrderSoCreate;
import com.cmall.groupcenter.model.AuthenticationInfo;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 跨境通订单
 * @author jlin
 *
 */
public class OrderForKJT extends BaseClass {

	/**
	 * 同步跨境通订单
	 * @param order
	 */
	public boolean rsyncOrder(String order_code) {
		
		boolean process_succ=true;
		
		List<MDataMap> list = DbUp.upTable("oc_order_kjt_list").queryAll("order_code_seq,order_code_out", "", "order_code=:order_code", new MDataMap("order_code",order_code));
		if(list.size()>0){
			
			for (MDataMap mDataMap : list) {
				String order_code_seq = mDataMap.get("order_code_seq");
				String order_code_out = mDataMap.get("order_code_out");
				if(StringUtils.isBlank(order_code_out)){
					MDataMap data = DbUp.upTable("oc_order_kjt_list_data").one("order_code_seq",order_code_seq);
					if(data!=null){
						Order order = JSON.parseObject(data.get("request_clazz"),Order.class);
						if(!rsyncOrder(order)){
							process_succ=false;
						}
					}
				}
			}
			
		}else{
			
			List<Order> orderList = groupOrder(order_code);
			
			for (int i = 0; i < orderList.size(); i++) {
				Order order = orderList.get(i);
				
//				OcOrderPay ocOrderPay = order.getOcOrderPayList().get(0);
//				String paySequenceid=ocOrderPay.getPaySequenceid();
////				ocOrderPay.setPaySequenceid((i+1)+paySequenceid.substring(0, paySequenceid.length()-String.valueOf(i+1).length()));
//				ocOrderPay.setPaySequenceid(WebHelper.upCode("88")+paySequenceid.substring(14));
				
				String now=DateUtil.getSysDateTimeString();
				DbUp.upTable("oc_order_kjt_list").dataInsert(new MDataMap("order_code_seq", order.getOrderCode(),"order_code", order_code,"create_time", now,"update_time",now));
				DbUp.upTable("oc_order_kjt_list_data").dataInsert(new MDataMap("order_code_seq", order.getOrderCode(),"request_clazz", JSON.toJSONString(order)));
				
				List<OrderDetail> details=order.getProductList();
				for (OrderDetail orderDetail : details) {
					DbUp.upTable("oc_order_kjt_detail").dataInsert(new MDataMap("order_code", order_code,"order_code_seq", order.getOrderCode(),"product_code",orderDetail.getProductCode(),"sku_code",orderDetail.getSkuCode(),"sku_name",orderDetail.getSkuName(),"sku_price",String.valueOf(orderDetail.getSkuPrice()),"sku_num",String.valueOf(orderDetail.getSkuNum()),"product_code_out",orderDetail.getProductCodeOut()));
				}
			}
			
			for (Order order : orderList) {
				if(!rsyncOrder(order)){
					process_succ=false;
				}
			}
		}
		
		return process_succ;
	}
	
	/**
	 * 拆单组单
	 * @param order_code
	 */
	public List<Order> groupOrder(String order_code){
		
		List<Order> orderList=new ArrayList<Order>();
		//获取订单信息
		OrderService orderService = new OrderService();
		Order order=orderService.getOrder(order_code);
		List<OrderDetail> detailList = order.getProductList();
		
		//此处更新商品的价格为 成本价
		for (OrderDetail orderDetail : detailList) {
			BigDecimal costPrice=(BigDecimal)DbUp.upTable("pc_productinfo").dataGet("cost_price", "product_code=:product_code", new MDataMap("product_code",orderDetail.getProductCode()));
			orderDetail.setCostPrice(costPrice==null?BigDecimal.ZERO:costPrice);
		}
		
		
		//拆单1 按商品拆单
		Map<String, List<OrderDetail>> listOrderMap1=new HashMap<String, List<OrderDetail>>();
		for (OrderDetail orderDetail : detailList) {
			
			MDataMap pext=DbUp.upTable("pc_productinfo_ext").oneWhere("dlr_id,product_trade_type,product_store_type,kjt_seller_code", "", "product_code=:product_code", "product_code",orderDetail.getProductCode());
			String theKey=pext.get("product_trade_type")+"_"+pext.get("dlr_id")+"_"+pext.get("product_store_type")+"_"+pext.get("kjt_seller_code")+"_"+getWarehouseID(orderDetail.getStoreCode());
			
			List<OrderDetail> olist=listOrderMap1.get(theKey);
			if(olist==null){
				olist = new ArrayList<OrderDetail>();
				listOrderMap1.put(theKey, olist);
			}
			olist.add(orderDetail);
		}
		
		int order_code_seq=0;//订单序列
		//拆单2 按金额组单   循环太多 代码以后优化
		for (Map.Entry<String, List<OrderDetail>> map : listOrderMap1.entrySet()) {
			List<OrderDetail> list=map.getValue();
			List<OrderDetail> groupDetailBe=new ArrayList<OrderDetail>();
			for (OrderDetail orderDetail : list) {
				for (int i = 0; i < orderDetail.getSkuNum(); i++) {
					OrderDetail orderDetailc=SerializationUtils.clone(orderDetail);
					orderDetailc.setSkuNum(1);
					groupDetailBe.add(orderDetailc);
				}
			}
			
//			List<Map<String,OrderDetail>> groupDetailAf=group(map.getKey(),groupDetailBe);
			List<Map<String,OrderDetail>> groupDetailAf=group(groupDetailBe);
			
			//组建订单信息
			for (Map<String,OrderDetail> omap : groupDetailAf) {
				
				BigDecimal orderMoney = BigDecimal.ZERO;//重新计算订单金额
				
				List<OrderDetail> detailListNew= new ArrayList<OrderDetail>();
				for (Map.Entry<String,OrderDetail> map2 : omap.entrySet()) {
					OrderDetail detail = map2.getValue();
					detailListNew.add(detail);
					orderMoney=orderMoney.add(detail.getCostPrice().multiply(new BigDecimal(String.valueOf(detail.getSkuNum()))));
				}
				
				Order norder=new Order();//新订单
				norder.setOrderCode(order_code+"#"+(++order_code_seq));
				norder.setOrderMoney(orderMoney);
				
				norder.setAddress(order.getAddress());
				norder.setProductList(detailListNew);
				norder.setOcOrderPayList(order.getOcOrderPayList());
				
				orderList.add(norder);
			}
		}
		
		return orderList;
	}
	
	/**
	 * 同步跨境通订单，不拆单，直发
	 * @param order
	 */
	public boolean rsyncOrder(Order order){
		
		//判断订单状态  //领导强制添加：即使发货单写一半，也不再同步订单到跨境通
		if(DbUp.upTable("oc_orderinfo").count("order_code",order.getAddress().getOrderCode(),"order_status","4497153900010002")<1){
			return true;
		}
		
		OrderSoCreate orderSoCreate = new OrderSoCreate();
		
		OrderAddress orderAddress= order.getAddress();
		List<OrderDetail> detailList = order.getProductList();
		List<OcOrderPay> payList=order.getOcOrderPayList();
		
		OrderDetail orderDetail0=detailList.get(0);
		OcOrderPay ocOrderPay = payList.get(payList.size()-1);
		
		
		//组装报文
		RsyncRequestOrderSoCreate requestOrderSoCreate = orderSoCreate.upRsyncRequest();
		
		requestOrderSoCreate.setSaleChannelSysNo(Long.valueOf(bConfig("groupcenter.rsync_kjt_SaleChannelSysNo")));
		requestOrderSoCreate.setMerchantOrderID(order.getOrderCode());
		requestOrderSoCreate.setServerType(getServerType(orderDetail0.getProductCode())); // 直邮商品 S01   自贸商品 S02
		requestOrderSoCreate.setWarehouseID(getWarehouseID(orderDetail0.getStoreCode()));
		
		SOPayInfo soPayInfo = new SOPayInfo();
		soPayInfo.setShippingAmount(BigDecimal.ZERO);
		soPayInfo.setTaxAmount(BigDecimal.ZERO);
		soPayInfo.setCommissionAmount(BigDecimal.ZERO);
		soPayInfo.setProductAmount(order.getOrderMoney());
		
		
		//添加对0元订单的支持 ，没有支付信息时，默认为支付宝
		String payType=PayTypeMapper.get(ocOrderPay.getPayType());
		if(StringUtils.isBlank(payType)){
			soPayInfo.setPayTypeSysNo(112);
			soPayInfo.setPaySerialNumber(WebHelper.upCode("88")+WebHelper.upCode("88"));
		}else{
			soPayInfo.setPayTypeSysNo(Long.valueOf(payType));
			soPayInfo.setPaySerialNumber(WebHelper.upCode("88")+ocOrderPay.getPaySequenceid().substring(14));
		}
		
		
		requestOrderSoCreate.setPayInfo(soPayInfo);
		
		
		SOShippingInfo soShippingInfo = new SOShippingInfo();
		soShippingInfo.setReceiveName(orderAddress.getReceivePerson());
		soShippingInfo.setReceivePhone(orderAddress.getMobilephone());
		soShippingInfo.setReceiveAddress(orderAddress.getAddress());
		soShippingInfo.setReceiveAreaCode(orderAddress.getAreaCode());
		soShippingInfo.setReceiveZip(orderAddress.getPostCode());
		soShippingInfo.setSenderName("");
		soShippingInfo.setSenderTel("");
		soShippingInfo.setSenderCompanyName("");
		soShippingInfo.setSenderAddr("");
		soShippingInfo.setSenderZip("");
		soShippingInfo.setSenderCity("");
		soShippingInfo.setSenderProvince("");
		soShippingInfo.setSenderCountry("");
		soShippingInfo.setReceiveAreaName(getAreaName(orderAddress.getAreaCode()));
		
		String kjt_shipTypeID=bConfig("groupcenter.kjt_shipTypeID");
		soShippingInfo.setShipTypeID("-1".equals(kjt_shipTypeID)?"":kjt_shipTypeID);
		
		requestOrderSoCreate.setShippingInfo(soShippingInfo);
		
		SOAuthenticationInfo authenticationInfo = new SOAuthenticationInfo();
		if(StringUtils.isBlank(orderAddress.getAuthIdcardNumber())||StringUtils.isBlank(orderAddress.getAuthEmail())){
			//没有 从系统取
			//过度时期 ，需要系统认证
			AuthenticationInfo authen = getAuth(order.getOrderMoney());
			if(authen==null){
				return false;
			}
			
			authenticationInfo.setName(authen.getTrue_name());
			authenticationInfo.setIDCardType(Integer.valueOf(IDcardMapper.get(authen.getIdcard_type())));
			authenticationInfo.setIDCardNumber(authen.getIdcard_number());
			authenticationInfo.setPhoneNumber(authen.getPhone_number());
			authenticationInfo.setEmail(authen.getEmail());
			authenticationInfo.setAddress(authen.getAddress());
			
		}else{//如果有
			authenticationInfo.setName(orderAddress.getAuthTrueName());
			authenticationInfo.setIDCardType(Integer.valueOf(IDcardMapper.get(orderAddress.getAuthIdcardType())));
			authenticationInfo.setIDCardNumber(orderAddress.getAuthIdcardNumber());
			authenticationInfo.setPhoneNumber(orderAddress.getAuthPhoneNumber());
			authenticationInfo.setEmail(orderAddress.getAuthEmail());
			authenticationInfo.setAddress(orderAddress.getAuthAddress());
		}
		
		
		
		requestOrderSoCreate.setAuthenticationInfo(authenticationInfo);
		
		List<SOItemInfo> itemList= new ArrayList<SOItemInfo>();
		for (OrderDetail orderDetail : detailList) {
			SOItemInfo soItemInfo = new SOItemInfo();
			soItemInfo.setProductID((String)DbUp.upTable("pc_productinfo").dataGet("product_code_old", "", new MDataMap("product_code",orderDetail.getProductCode())));
			soItemInfo.setQuantity(orderDetail.getSkuNum());
			soItemInfo.setSalePrice(orderDetail.getCostPrice());//商品售价   Sum(SalePrice* Quantity)=PayInfo.ProductAmount
			
			//需求：TaxAmount=0 
			soItemInfo.setTaxPrice(BigDecimal.ZERO); // Sum(TaxPrice * Quantity)=PayInfo. TaxAmount
			itemList.add(soItemInfo);
		}
		
		requestOrderSoCreate.setItemList(itemList);
		
		//同步
		orderSoCreate.doRsync();
		
		return orderSoCreate.responseSucc();
	}
	
	private String getAreaName(String area_code){
		String prov=DbUp.upTable("sc_tmp").one("code", area_code.subSequence(0, 2)+ "0000").get("name");
		String city=DbUp.upTable("sc_tmp").one("code", area_code.subSequence(0, 4)+ "00").get("name");
		String area=DbUp.upTable("sc_tmp").one("code", area_code).get("name");
//		Map<String, Object> map = DbUp.upTable("sc_tmp").dataSqlOne(" SELECT CONCAT((SELECT name from sc_tmp WHERE code=:code1 LIMIT 0,1 ),',',(SELECT DISTINCT name from sc_tmp WHERE code=:code2 LIMIT 0,1 ),',',(SELECT DISTINCT name from sc_tmp WHERE code=:code3 LIMIT 0,1 )) as code from sc_tmp LIMIT 0,1  ",new MDataMap("code1", area_code.subSequence(0, 2)+ "0000", "code2", area_code.subSequence(0, 4)+ "00", "code3", area_code));
		return prov+","+(StringUtils.startsWith(city, "省直辖县级行政区划")?area:city)+","+area;
	}
	
	/**
	 * S01：一般进口 S02：保税区进口 为空默认 S02
	 * @param order
	 * @return
	 */
	private String getServerType(String product_code){
		String type="S02";
		//直邮商品 S01   自贸商品 S02
		String product_trade_type=(String)DbUp.upTable("pc_productinfo_ext").dataGet("product_trade_type", "product_code=:product_code", new MDataMap("product_code",product_code));
//		0 = 直邮 1 = 自贸
		if(StringUtils.isNotBlank(product_trade_type)){
			if("0".equals(product_trade_type)){
				type="S01";
			}
		}
		return type;
	}
	
	/**
	 * 订单出库仓库在Kjt平台的编号
	 * @param store_code
	 * @return
	 */
	private int getWarehouseID(String store_code) {
		if(StringUtils.isNotBlank(store_code)){
			return Integer.valueOf(store_code.substring(0,store_code.indexOf("_")));
		}
		return -1;
	}
	
	
	/**
	 * 支付方式
	 * @param payType
	 * @return
	 */
	private static MDataMap PayTypeMapper = new MDataMap("449746280003","112","449746280005","118");
//	private int getPayTypeSysNo(String payType){
//		
////		111: 东方支付
////		112: 支付宝
////		114: 财付通
////		117: 银联支付
////		118: 微信支付
//		int type=-1;
//		if(StringUtils.isNotBlank(payType)){
//			switch (payType) {
//			case "449746280003"://支付宝
//				type=112;
//				break;
//			case "449746280005"://微信
//				type=118;
//				break;
//			default:
//				break;
//			}
//		}
//		return type;
//	}
	
	/**
	 * 随机获取一个可以用的真实认证信息
	 * @param order_price
	 * @return
	 */
	private AuthenticationInfo getAuth(BigDecimal order_price){
		MDataMap dataMap = DbUp.upTable("mc_authenticationInfo").oneWhere("", "surmoney desc", "surmoney>=:order_price", "order_price",String.valueOf(order_price));
		
		if (dataMap!=null) {
			
			DbUp.upTable("mc_authenticationInfo").dataExec("update mc_authenticationInfo set surmoney=surmoney-"+order_price+" where auth_code=:auth_code", new MDataMap("auth_code",dataMap.get("auth_code")));
			
			SerializeSupport<AuthenticationInfo> ss = new SerializeSupport<AuthenticationInfo>();
			AuthenticationInfo authenticationInfo = new AuthenticationInfo();
			ss.serialize(dataMap, authenticationInfo);
			return authenticationInfo;
		}else{
			//发邮件通知
			sendMailForAuth();
		}
		return null;
	}
	
	/**
	 * 证件类型转换
	 * @param idcard_type
	 * @return
	 */
	private static MDataMap IDcardMapper = new MDataMap("4497465200090001","0");
//	private int getIDcardType(String idcard_type){
//		int type=0;
//		if(StringUtils.isNotBlank(idcard_type)){
//			switch (idcard_type) {
//			case "4497465200090001"://身份证
//				type=0;
//				break;
//			default:
//				break;
//			}
//		}
//		return type;
//	}
	
	
	/**
	 * 重新组合商品
	 * @param skuList
	 * @return
	 */
//	private List<Map<String,OrderDetail>> group (String type,List<OrderDetail> skuList){
//		sort(skuList);
//		List<Map<String,OrderDetail>> list = new ArrayList<Map<String,OrderDetail>>();
//		
//		BigDecimal max_sum_price=new BigDecimal("800");
//		if(type.startsWith("0")){
//			max_sum_price=new BigDecimal("1000");
//		}
//		
//		BigDecimal tprice = BigDecimal.ZERO;
//		Map<String,OrderDetail> skus=new HashMap<String,OrderDetail>();
//		
//		for (OrderDetail sku : skuList) {
//			
//			tprice=tprice.add(sku.getCostPrice());
//			
//			if(tprice.compareTo(max_sum_price)>0){
//				tprice = sku.getCostPrice();
//				skus=new HashMap<String,OrderDetail>();
//				list.add(skus);
//			}else{
//				if(list.size()<1){
//					list.add(skus);
//				}
//			}
//			OrderDetail sku_old=skus.get(sku.getSkuCode());
//			if(sku_old!=null){
//				sku_old.setSkuNum(sku_old.getSkuNum()+sku.getSkuNum());
//			}else{
//				skus.put(sku.getSkuCode(),sku);
//			}
//		}
//		
//		return list;
//	}
	
	
	/**
	 * 拆单规则变更： 每单商品总成本金额不能大于2000元
	 * @param skuList
	 * @return
	 */
	private List<Map<String,OrderDetail>> group (List<OrderDetail> skuList){
		
		sort(skuList);
		
		
		List<Map<String,OrderDetail>> list = new ArrayList<Map<String,OrderDetail>>();
		
		BigDecimal max_sum_price=new BigDecimal("50");
		// 单个订单最大订单金额
		BigDecimal maxCostPrice = new BigDecimal("2000");
		
		BigDecimal tprice = BigDecimal.ZERO;
		Map<String,OrderDetail> skus=new HashMap<String,OrderDetail>();
		
		for (OrderDetail sku : skuList) {
			
			if(BigDecimal.ZERO.compareTo(sku.getCostPrice())>=0||BigDecimal.ZERO.compareTo(sku.getTaxRate())>=0){
				
				String product_code=sku.getProductCode();
				MDataMap proMap=DbUp.upTable("pc_productinfo").one("product_code",product_code);
				sku.setCostPrice(new BigDecimal(proMap.get("cost_price")));
				sku.setTaxRate(new BigDecimal(proMap.get("tax_rate")));
			}
			
			/*
			tprice=tprice.add(sku.getCostPrice().multiply(sku.getTaxRate()));
			
			if(tprice.compareTo(max_sum_price)>=0){
				tprice = sku.getCostPrice().multiply(sku.getTaxRate());
				skus=new HashMap<String,OrderDetail>();
				list.add(skus);
			}else{
				if(list.size()<1){
					list.add(skus);
				}
			}*/
			
			// 根据完税价每单不超过2000的规则拆单
			tprice = tprice.add(sku.getCostPrice());
			if(tprice.compareTo(maxCostPrice) > 0){
				tprice = sku.getCostPrice();
				skus = new HashMap<String,OrderDetail>();
				list.add(skus);
			}
			
			if(list.size() < 1){
				list.add(skus);
			}
			
			OrderDetail sku_old=skus.get(sku.getSkuCode());
			if(sku_old!=null){
				sku_old.setSkuNum(sku_old.getSkuNum()+sku.getSkuNum());
			}else{
				skus.put(sku.getSkuCode(),sku);
			}
		}
		
		return list;
	}
	
//	private void sort(List<OrderDetail> list) {
//		Collections.sort(list, new Comparator<OrderDetail>() {
//			public int compare(OrderDetail sku1, OrderDetail sku2) {
//
//				if (sku1.getCostPrice().compareTo(sku2.getCostPrice()) > 0) {
//					return 1;
//				} else if (sku1.getCostPrice().compareTo(sku2.getCostPrice()) == 0) {
//					return 0;
//				} else {
//					return -1;
//				}
//			}
//		});
//	}
	
	private void sort(List<OrderDetail> list) {
		Collections.sort(list, new Comparator<OrderDetail>() {
			public int compare(OrderDetail sku1, OrderDetail sku2) {
				
				BigDecimal txp1=sku1.getCostPrice().multiply(sku1.getTaxRate());
				BigDecimal txp2=sku2.getCostPrice().multiply(sku2.getTaxRate());

				if (txp1.compareTo(txp2) > 0) {
					return 1;
				} else if (txp1.compareTo(txp2) == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}
	
//	-4	系统作废	交易失败
//	-1	作废	交易失败
//	0	待审核	下单成功-未发货
//	1	待出库	下单成功-未发货
//	4	已出库待申报	下单成功-未发货  已发货
//	41	已申报待通关	下单成功-未发货  已发货
//	45	已通过发往顾客	已发货
//	5	订单完成	交易成功
//	6	申报失败订单作废	交易失败
//	65	通关失败订单作废	交易失败
//	7	订单拒收	交易失败

//	4497153900010001	下单成功-未付款
//	4497153900010002	下单成功-未发货
//	4497153900010003	已发货
//	4497153900010004	已收货
//	4497153900010005	交易成功
//	4497153900010006	交易失败
	
	/**
	 * 订单状态映射
	 * @param ostatus
	 * @return
	 */
	public static String orderStatusMapper(int ostatus){
		
		String status=null;
		
		switch (ostatus) {
		case -4:
			status="4497153900010006";
			break;
		case -1:
			status="4497153900010006";
			break;
		case 0:
			status="4497153900010002";
			break;
		case 1:
			status="4497153900010002";
			break;
		case 4:
			status="4497153900010003";
			break;	
		case 41:
			status="4497153900010003";
			break;		
		case 45:
			status="4497153900010003";
			break;	
		case 5:
			status="4497153900010005";
			break;	
		case 6:
			status="4497153900010006";
			break;	
		case 65:
			status="4497153900010006";
			break;	
		case 7:
			status="4497153900010006";
			break;	
		default:
			 status="";
			break;
		}
		return status;
	}
	
	private void sendMailForAuth(){
		
		String receives[]= bConfig("groupcenter.kjt_auth_sendMail_receives").split(",");
		String title= bConfig("groupcenter.kjt_auth_sendMail_title");
		String content= bConfig("groupcenter.kjt_auth_sendMail_content");
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, title, content);
			}
		}
	}
	
	public static void main(String[] args) {
		new OrderForKJT().rsyncOrder("DD3576934102");
	}
	
	
}
