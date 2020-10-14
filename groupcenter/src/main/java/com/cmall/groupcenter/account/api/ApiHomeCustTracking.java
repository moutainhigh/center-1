package com.cmall.groupcenter.account.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.ApiHomeCustTrackingListResult;
import com.cmall.groupcenter.account.model.ApiHomeCustTrackingResult;
import com.cmall.groupcenter.account.model.ApiHomeNoEvaluationOrderListResult;
import com.cmall.groupcenter.account.model.ApiHomeOrderTrackingListResult;
import com.cmall.groupcenter.account.model.ApiSellerListResult;
import com.cmall.groupcenter.account.model.ApiSellerStandardAndStyleResult;
import com.cmall.groupcenter.homehas.RsyncGetOrderTracking;
import com.cmall.groupcenter.homehas.RsyncGetThirdOrderDetail;
import com.cmall.groupcenter.homehas.RsyncGetThirdOrderList;
import com.cmall.groupcenter.homehas.model.ResponseGetOrderTrackingList;
import com.cmall.groupcenter.homehas.model.RsyncModelThirdOrder;
import com.cmall.groupcenter.homehas.model.RsyncModelThirdOrderDetail;
import com.cmall.groupcenter.service.GetOrderTrackingService;
import com.cmall.ordercenter.helper.OrderHelper;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmassystem.Constants;
import com.srnpr.xmassystem.load.LoadSkuInfoSpread;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfoSpread;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 订单跟踪轨迹表
 * 
 * @author sunyan
 * 
 */
public class ApiHomeCustTracking extends RootApiForToken<ApiHomeCustTrackingResult, RootInput> {
	PlusServiceAccm plusServiceAccm = new PlusServiceAccm();
	public ApiHomeCustTrackingResult Process(
			RootInput inputParam, MDataMap mRequestMap) {

		ApiHomeCustTrackingResult apiHomeCustTrackingResult = new ApiHomeCustTrackingResult();
		List<ApiHomeCustTrackingListResult> sList = new ArrayList<ApiHomeCustTrackingListResult>();
		List<ApiHomeNoEvaluationOrderListResult> noList = new ArrayList<ApiHomeNoEvaluationOrderListResult>();
		OrderService orderService = new OrderService();

		String buyer_code = getUserCode();
		/**
		 * 556版本增加增加签收未评价的订单数据 
		 */
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("buyer_code", buyer_code);
		String sql = "SELECT od.product_picurl,od.sku_name,od.sku_code,od.product_code,od.give_integral_money,o.order_code FROM oc_orderinfo o INNER JOIN oc_orderdetail od ON o.order_code = od.order_code LEFT JOIN newscenter.nc_order_evaluation n ON o.order_code = n.order_code AND od.sku_code = n.order_skuid "
				+ " WHERE od.gift_flag = '1'  AND o.delete_flag = '0' AND o.buyer_code = :buyer_code AND o.order_source not in('449715190014') AND o.order_status = '4497153900010005'"
				   + " AND o.order_type NOT IN(" + new OrderService().getNotInOrderType() + ") AND n.zid IS NULL ORDER BY o.create_time DESC";
		List<Map<String,Object>> dataSqlList = DbUp.upTable("oc_orderinfo").dataSqlList(sql, mWhereMap);
		String order_code = "";
		if(dataSqlList.size() > 0) {
			order_code = MapUtils.getString(dataSqlList.get(0), "order_code");
		}
		for(Map<String,Object> map : dataSqlList) {
			if(!order_code.equals(MapUtils.getString(map, "order_code"))) {
				break;
			}
			//评论送积分		
			List<MDataMap> evaluateList = DbUp.upTable("sc_evaluate_configure").queryAll("evaluate_type,integral_value,tip,limit_value", "", "", new MDataMap());
			BigDecimal integral = new BigDecimal(0);
			// 分享到买家秀赠送积分总量
			int buyerShowIntegral = 0;
			for(MDataMap map1:evaluateList){
				if(map1.get("evaluate_type").equals("买家秀")){
					buyerShowIntegral = MapUtils.getIntValue(map1, "integral_value");
				}else {
					integral = integral.add(new BigDecimal(map1.get("integral_value")));					
				}
			}
			ApiHomeNoEvaluationOrderListResult apiHomeNoEvaluationOrderListResult = new ApiHomeNoEvaluationOrderListResult();
			apiHomeNoEvaluationOrderListResult.setMainpic_url(MapUtils.getString(map, "product_picurl",""));
			apiHomeNoEvaluationOrderListResult.setSku_code(MapUtils.getString(map, "sku_code",""));
			apiHomeNoEvaluationOrderListResult.setOrder_code(MapUtils.getString(map, "order_code",""));
			apiHomeNoEvaluationOrderListResult.setIntegral(String.valueOf(integral.intValue()));
			apiHomeNoEvaluationOrderListResult.setProduct_code(MapUtils.getString(map, "product_code",""));
			apiHomeNoEvaluationOrderListResult.setProduct_name(MapUtils.getString(map, "sku_name",""));
			apiHomeNoEvaluationOrderListResult.setBuyerShowIntegral(buyerShowIntegral+"");
			noList.add(apiHomeNoEvaluationOrderListResult);
		}
		apiHomeCustTrackingResult.setApiHomeNoEvaluationOrderListResult(noList);
		String sOrderCode = "";
		List<String> orderCodeList = new ArrayList<String>();
		List<Map<String, Object>> orderList = orderService.orderInformationSmallV2(buyer_code,"4497153900010003", "1",getManageCode(),false); // 订单信息
		if(orderList!=null&&orderList.size()>0){
			for(Map<String, Object> orderMap:orderList){
				orderCodeList.add(orderMap.get("order_code").toString());
			}
		}
		MDataMap memberDataMap=DbUp.upTable("mc_login_info").one("member_code",buyer_code);
		/**
		 * 调取LD订单列表
		 */
		RsyncGetThirdOrderList rsyncGetThirdOrderList = new RsyncGetThirdOrderList();
		rsyncGetThirdOrderList.upRsyncRequest().setTel(memberDataMap.get("login_name").toString());
		rsyncGetThirdOrderList.upRsyncRequest().setOrd_type("03");
		rsyncGetThirdOrderList.doRsync();
		if(rsyncGetThirdOrderList.getResponseObject() != null && rsyncGetThirdOrderList.getResponseObject().getResult() != null && rsyncGetThirdOrderList.getResponseObject().getResult().size() > 0) {
			for(RsyncModelThirdOrder order : rsyncGetThirdOrderList.getResponseObject().getResult()) {
				if(!orderCodeList.contains(order.getOrd_id().toString())){
					orderCodeList.add(order.getOrd_id().toString());
				}				
			}
		}
		
		if(orderCodeList.size()==0){
			apiHomeCustTrackingResult.setResultCode(1);
			apiHomeCustTrackingResult.setResultMessage("用户没有待收货订单");
			return apiHomeCustTrackingResult;
		}
		for(String orderCode:orderCodeList){
			ApiHomeCustTrackingListResult apiHomeOrderTrackingResult = new ApiHomeCustTrackingListResult();
			List<ApiHomeOrderTrackingListResult> trackingListResult = new ArrayList<ApiHomeOrderTrackingListResult>();
			sOrderCode = orderCode;
			List<ApiSellerListResult> sellerResultList = new ArrayList<ApiSellerListResult>();
			Map<String, Object> orderMap = new HashMap<String, Object>();
			orderMap.put("order_code", sOrderCode);
			List<Map<String, Object>> orderSellerList = orderService.orderSellerNumber(orderMap);
			if (orderSellerList != null && !orderSellerList.isEmpty()) {
				for (Map<String, Object> orderSellerMap:orderSellerList) {
					List<Map<String, Object>> sellerList = orderService.sellerInformation(orderSellerMap.get("sku_code").toString()); // 查询商品信息
					if (sellerList != null && !sellerList.isEmpty()) {
						for (int k = 0; k < sellerList.size(); k++) {
							ApiSellerListResult apiSellerListResult = new ApiSellerListResult();
							apiSellerListResult.setProduct_code(sellerList.get(k).get("product_code").toString());
							apiSellerListResult.setSku_code(sellerList.get(k).get("sku_code").toString());
							apiSellerListResult.setProduct_name(sellerList.get(k).get("sku_name").toString());
							if (!"null".equals(String.valueOf(sellerList.get(k).get("sku_picurl")))) {
								apiSellerListResult.setMainpic_url(sellerList.get(k).get("sku_picurl").toString());
							}
							List<PcPropertyinfoForFamily> standardAndStyleList = orderService.sellerStandardAndStyle(sellerList.get(k).get("sku_keyvalue").toString());
							if (standardAndStyleList != null && standardAndStyleList.size() > 0) {
								List<ApiSellerStandardAndStyleResult> apiSellerStandardAndStyleResultList = new ArrayList<ApiSellerStandardAndStyleResult>();
								for (PcPropertyinfoForFamily pcPropertyinfoForFamily : standardAndStyleList) {
									ApiSellerStandardAndStyleResult apiSellerStandardAndStyleResult = new ApiSellerStandardAndStyleResult();
									apiSellerStandardAndStyleResult.setStandardAndStyleKey(pcPropertyinfoForFamily.getPropertyKey());
									apiSellerStandardAndStyleResult.setStandardAndStyleValue(pcPropertyinfoForFamily.getPropertyValue());
									apiSellerStandardAndStyleResultList.add(apiSellerStandardAndStyleResult);
								}
								apiSellerListResult.setStandardAndStyleList(apiSellerStandardAndStyleResultList);
							}
							sellerResultList.add(apiSellerListResult);										
						}
					}
				}
			}
			apiHomeOrderTrackingResult.setApiSellerList(sellerResultList);
			apiHomeOrderTrackingResult.setOrder_code(sOrderCode);
			
			//查询物流号和物流公司(在没有返回轨迹的情况下也返回)
			try{
				MDataMap orderShipment = DbUp.upTable("oc_order_shipments").oneWhere("waybill,logisticse_name", "-zid", "order_code=:order_code","order_code",sOrderCode);
				if(orderShipment!=null){
					apiHomeOrderTrackingResult.setYc_express_num(orderShipment.get("waybill"));
					apiHomeOrderTrackingResult.setYc_delivergoods_user_name(orderShipment.get("logisticse_name"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			if(!"DD".equals(sOrderCode.substring(0, 2)) && !"OS".equals(sOrderCode.substring(0, 2)) && !"HH".equals(sOrderCode.substring(0, 2))) {
				 //LD订单的物流轨迹处理
				String order_status = "";
				String product_code = "";
				apiHomeOrderTrackingResult.setOrder_source("1");
				RsyncGetThirdOrderDetail rsyncGetThirdOrderDetail = new RsyncGetThirdOrderDetail();
				rsyncGetThirdOrderDetail.upRsyncRequest().setOrd_id(sOrderCode);
				rsyncGetThirdOrderDetail.upRsyncRequest().setOrd_seq("");
				rsyncGetThirdOrderDetail.doRsync();
				if(rsyncGetThirdOrderDetail.getResponseObject() != null && rsyncGetThirdOrderDetail.getResponseObject().getResult() != null && rsyncGetThirdOrderDetail.getResponseObject().getResult().size() > 0 ) {
					RsyncModelThirdOrderDetail orderdetail = rsyncGetThirdOrderDetail.getResponseObject().getResult().get(0);
					String sku_key = "color_id=" + orderdetail.getColor_id().toString() + "&style_id=" + orderdetail.getStyle_id().toString();					
					order_status = convertOrderStatus(orderdetail.getOrd_stat());
					product_code = orderdetail.getGood_id().toString();
					apiHomeOrderTrackingResult.setYc_express_num(orderdetail.getInvc_id() == null ? "" : orderdetail.getInvc_id().toString());
					apiHomeOrderTrackingResult.setYc_delivergoods_user_name(orderdetail.getDl_cd_desc() == null ? "" : orderdetail.getDl_cd_desc().toString());
					
					List<Map<String, Object>> sellerList = orderService.sellerInformation1(product_code,sku_key); // 查询商品信息
					if (sellerList != null && !sellerList.isEmpty()) {
						for (int k = 0; k < sellerList.size(); k++) {
							ApiSellerListResult apiSellerListResult = new ApiSellerListResult();
							apiSellerListResult.setProduct_code(sellerList.get(k).get("product_code").toString());
							apiSellerListResult.setSku_code(sellerList.get(k).get("sku_code").toString());
							apiSellerListResult.setProduct_name(sellerList.get(k).get("sku_name").toString());
							if (!"null".equals(String.valueOf(sellerList.get(k).get("sku_picurl")))) {
								apiSellerListResult.setMainpic_url(sellerList.get(k).get("sku_picurl").toString());
							}
							List<PcPropertyinfoForFamily> standardAndStyleList = orderService.sellerStandardAndStyle(sellerList.get(k).get("sku_keyvalue").toString());
							if (standardAndStyleList != null && standardAndStyleList.size() > 0) {
								List<ApiSellerStandardAndStyleResult> apiSellerStandardAndStyleResultList = new ArrayList<ApiSellerStandardAndStyleResult>();
								for (PcPropertyinfoForFamily pcPropertyinfoForFamily : standardAndStyleList) {
									ApiSellerStandardAndStyleResult apiSellerStandardAndStyleResult = new ApiSellerStandardAndStyleResult();
									apiSellerStandardAndStyleResult.setStandardAndStyleKey(pcPropertyinfoForFamily.getPropertyKey());
									apiSellerStandardAndStyleResult.setStandardAndStyleValue(pcPropertyinfoForFamily.getPropertyValue());
									apiSellerStandardAndStyleResultList.add(apiSellerStandardAndStyleResult);
								}
								apiSellerListResult.setStandardAndStyleList(apiSellerStandardAndStyleResultList);
							}
							sellerResultList.add(apiSellerListResult);										
						}
					}
				}
				
				RsyncGetOrderTracking  rsyncGetOrderTracking = new RsyncGetOrderTracking();
				rsyncGetOrderTracking.upRsyncRequest().setOrd_id(sOrderCode);
				rsyncGetOrderTracking.doRsync();
				if(rsyncGetOrderTracking.getResponseObject() != null && rsyncGetOrderTracking.getResponseObject().getResult() != null && rsyncGetOrderTracking.getResponseObject().getResult().size() > 0 ) {
					List<ResponseGetOrderTrackingList> result = rsyncGetOrderTracking.getResponseObject().getResult();
					
					Collections.sort(result, new Comparator<ResponseGetOrderTrackingList>() {
						@Override
						public int compare(ResponseGetOrderTrackingList t1, ResponseGetOrderTrackingList t2) {
							String yc_update_time1 = t1.getYc_update_time();
							String yc_update_time2 = t2.getYc_update_time();
							if(yc_update_time1.compareTo(yc_update_time2) < 0)
								return 1;
							else 
								return -1;
						}							
					});
					List<ApiHomeOrderTrackingListResult> aor = new ArrayList<ApiHomeOrderTrackingListResult>();
					for(ResponseGetOrderTrackingList track : result){
						ApiHomeOrderTrackingListResult apiHomeOrderTrackingListResult = new ApiHomeOrderTrackingListResult();
						apiHomeOrderTrackingListResult.setOrderTrackContent(track.getOutgo_no());
						apiHomeOrderTrackingListResult.setOrderTrackTime(track.getOutgo_time());
						apiHomeOrderTrackingListResult.setYc_dis_time(track.getYc_dis_time());
						apiHomeOrderTrackingListResult.setYc_update_time(track.getYc_update_time());
						aor.add(apiHomeOrderTrackingListResult);
					}
					apiHomeOrderTrackingResult.setApiHomeOrderTrackingListResult(aor);
				}
				
				// 直邮商品的物流提示
				if(("4497153900010002".equals(order_status) 
						|| ("4497153900010003".equals(order_status)) && apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty())){
					LoadSkuInfoSpread loadSkuInfoSpread = new LoadSkuInfoSpread();
					PlusModelSkuQuery sq = new PlusModelSkuQuery();
					
					// 理论上只查询一个商品就行，因为直邮的商品已经被拆为单独的订单了
					if(StringUtils.isNotBlank(product_code) && !"".equals(product_code)){
						sq.setCode(product_code);
						PlusModelSkuInfoSpread plusModelSkuInfoSpread = loadSkuInfoSpread.upInfoByCode(sq);
						if(plusModelSkuInfoSpread != null && "1".equals(plusModelSkuInfoSpread.getProductTradeType())){
							apiHomeOrderTrackingResult.setLogisticsTips(bConfig("familyhas.product_tips_zhiyou"));
						}
					}
				}
				
				if(StringUtils.isBlank(apiHomeOrderTrackingResult.getLogisticsTips())
						&& apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty()
						&& "4497153900010003".equals(order_status)){
					apiHomeOrderTrackingResult.setLogisticsTips("暂无物流信息!");
				}
			
			} else {
				if(DbUp.upTable("oc_express_detail").count("order_code", sOrderCode)==0)
				{
					sOrderCode = OrderHelper.getOrderCodeByOutCode(sOrderCode);
					if ("".equals(sOrderCode)) { // 因为获取订单轨迹 必须用out_order_code
						sOrderCode = orderCode;
					}
					GetOrderTrackingService getOrderTrackingService = new GetOrderTrackingService();
					boolean bol = getOrderTrackingService
							.synchronizationGetOrderTracking(sOrderCode); // 从家有获取订单跟踪信息

					if (bol == true) { // 获取订单跟踪信息成功进入
						MDataMap map = new MDataMap();
						map.put("out_order_code", sOrderCode);

						List<MDataMap> list = DbUp.upTable("oc_order_tracking")
								.queryAll("", "outgo_time desc", "", map); // 查询订单跟踪信息表 
						if (list != null && !"".equals(list) && list.size() > 0) {
							for (MDataMap trackingMap : list) {
								apiHomeOrderTrackingResult
										.setYc_delivergoods_user_name(trackingMap
												.get("yc_delivergoods_user_name"));
								apiHomeOrderTrackingResult
										.setYc_express_num(trackingMap
												.get("yc_express_num"));

								ApiHomeOrderTrackingListResult apiHomeOrderTrackingListResult = new ApiHomeOrderTrackingListResult();
								apiHomeOrderTrackingListResult
										.setOrderTrackContent(trackingMap
												.get("outgo_no"));
								apiHomeOrderTrackingListResult
										.setOrderTrackTime(trackingMap
												.get("outgo_time"));
								apiHomeOrderTrackingListResult
										.setYc_dis_time(trackingMap.get("yc_dis_time"));
								apiHomeOrderTrackingListResult
										.setYc_update_time(trackingMap
												.get("yc_update_time"));
								trackingListResult.add(apiHomeOrderTrackingListResult);
							}
							apiHomeOrderTrackingResult
									.setApiHomeOrderTrackingListResult(trackingListResult);
						}
					}
				} else {
					MDataMap map = new MDataMap();
					map.put("order_code", sOrderCode);
					
					//物流修改订单后要查询最新的运单号，而且要加上运单号去掉历史运单流水
					MDataMap waybillMap = DbUp.upTable("oc_order_shipments").one("order_code",sOrderCode);
					if(null != waybillMap && !waybillMap.isEmpty() && waybillMap.size() > 0){
						map.put("waybill", waybillMap.get("waybill"));
					}

					List<MDataMap> list = DbUp.upTable("oc_express_detail").queryAll(
							"", "time desc", "", map); // 查询订单跟踪信息表
					if (list != null && !"".equals(list) && list.size() > 0) {
						for (MDataMap trackingMap : list) {
							if (StringUtility.isNotNull(trackingMap
									.get("logisticse_code"))) {
								MDataMap one = DbUp.upTable("sc_logisticscompany").one(
										"company_code",
										trackingMap.get("logisticse_code"));
								if (one != null && !one.isEmpty()) {
									apiHomeOrderTrackingResult
											.setYc_delivergoods_user_name(one
													.get("company_name"));
								} else {
									apiHomeOrderTrackingResult
											.setYc_delivergoods_user_name("快递");
								}
							} else {
								apiHomeOrderTrackingResult
										.setYc_delivergoods_user_name("快递");
							}
							apiHomeOrderTrackingResult.setYc_express_num(trackingMap
									.get("waybill"));

							ApiHomeOrderTrackingListResult apiHomeOrderTrackingListResult = new ApiHomeOrderTrackingListResult();
							apiHomeOrderTrackingListResult
									.setOrderTrackContent(trackingMap.get("context"));
							apiHomeOrderTrackingListResult
									.setOrderTrackTime(trackingMap.get("time"));
							apiHomeOrderTrackingListResult.setYc_dis_time(trackingMap
									.get("time"));
							apiHomeOrderTrackingListResult
									.setYc_update_time(trackingMap.get("time"));
							trackingListResult.add(apiHomeOrderTrackingListResult);
						}
						apiHomeOrderTrackingResult
								.setApiHomeOrderTrackingListResult(trackingListResult);
					}
				}
				
				// 直邮商品的物流提示
				String orderStatus = (String)DbUp.upTable("oc_orderinfo").dataGet("order_status", "", new MDataMap("order_code",sOrderCode));
				if(("4497153900010002".equals(orderStatus) 
						|| ("4497153900010003".equals(orderStatus)) && apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty())){
					LoadSkuInfoSpread loadSkuInfoSpread = new LoadSkuInfoSpread();
					PlusModelSkuQuery sq = new PlusModelSkuQuery();
					
					// 理论上只查询一个商品就行，因为直邮的商品已经被拆为单独的订单了
					MDataMap m = DbUp.upTable("oc_orderdetail").oneWhere("product_code", "", "", "order_code",sOrderCode,"gift_flag","1");
					if(m != null && StringUtils.isNotBlank(m.get("product_code"))){
						sq.setCode(m.get("product_code"));
						PlusModelSkuInfoSpread plusModelSkuInfoSpread = loadSkuInfoSpread.upInfoByCode(sq);
						if(plusModelSkuInfoSpread != null && "1".equals(plusModelSkuInfoSpread.getProductTradeType())){
							apiHomeOrderTrackingResult.setLogisticsTips(bConfig("familyhas.product_tips_zhiyou"));
						}
					}
				}
				
				// 只在已发货的情况显示默认提示
				if(StringUtils.isBlank(apiHomeOrderTrackingResult.getLogisticsTips())
						&& apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty()
						&& "4497153900010003".equals(orderStatus)){
					apiHomeOrderTrackingResult.setLogisticsTips("暂无物流信息!");
				}
				
				String smallSellerCode = (String)DbUp.upTable("oc_orderinfo").dataGet("small_seller_code", "", new MDataMap("order_code",sOrderCode));
				// 京东的订单需要屏蔽前端的确认收货按钮
				if(Constants.SMALL_SELLER_CODE_JD.equals(smallSellerCode)) {
					apiHomeOrderTrackingResult.setOrder_source("1");
				}
			}
			if(sList.size()<=99){
				sList.add(apiHomeOrderTrackingResult);
			}
			
		}
		apiHomeCustTrackingResult.setApiHomeCustTrackingListResult(sList);
		
				

		return apiHomeCustTrackingResult;
	}

	/**
	 * 转换LD订单状态
	 * @param order_status
	 * @return
	 */
	private String convertOrderStatus(String order_status) {
		switch(order_status) {
			case "01": return "4497153900010001";
			case "02": return "4497153900010002";
			case "03": return "4497153900010003";
			case "04": return "4497153900010006";
			case "05": return "4497153900010005";
			case "06": return "4497153900010008";
			default : return "";
		}
	}
}
