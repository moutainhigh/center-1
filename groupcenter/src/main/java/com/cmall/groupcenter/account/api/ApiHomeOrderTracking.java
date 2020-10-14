package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.ApiHomeOrderTrackingInput;
import com.cmall.groupcenter.account.model.ApiHomeOrderTrackingListResult;
import com.cmall.groupcenter.account.model.ApiHomeOrderTrackingResult;
import com.cmall.groupcenter.homehas.RsyncGetOrderTracking;
import com.cmall.groupcenter.homehas.model.ResponseGetOrderTrackingList;
import com.cmall.groupcenter.service.GetOrderTrackingService;
import com.cmall.ordercenter.helper.OrderHelper;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmassystem.load.LoadSkuInfoSpread;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfoSpread;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForVersion;

/**
 * 订单跟踪轨迹表
 * 
 * @author wz
 * 
 */
public class ApiHomeOrderTracking extends RootApiForVersion<ApiHomeOrderTrackingResult, ApiHomeOrderTrackingInput> {

	public ApiHomeOrderTrackingResult Process(
			ApiHomeOrderTrackingInput inputParam, MDataMap mRequestMap) {

		ApiHomeOrderTrackingResult apiHomeOrderTrackingResult = new ApiHomeOrderTrackingResult();
		List<ApiHomeOrderTrackingListResult> trackingListResult = new ArrayList<ApiHomeOrderTrackingListResult>();

		String sOrderCode = "";
		
		/*
		Order or = new OrderService().getOrder(inputParam.getOrder_code());
		boolean flag = false;
		MDataMap mmw = new MDataMap();
		mmw.put("order_code", inputParam.getOrder_code());
		String sql = "select a.validate_flag as validate_flag FROM productcenter.pc_productinfo a,productcenter.pc_skuinfo b, ordercenter.oc_orderdetail c "
				+ "where  c.gift_flag='1' and a.product_code=b.product_code "
				+ "and b.sku_code = c.sku_code and c.order_code=:order_code ";
		List<Map<String, Object>> llw = DbUp.upTable("pc_productinfo")
				.dataSqlList(sql, mmw);
		if (llw != null && !llw.isEmpty()) {
			for (Map<String, Object> mas : llw) {
				if (mas.get("validate_flag") != null
						&& "N".equals(mas.get("validate_flag").toString())) {
					flag = true;
					break;
				}
			}
		}
		*/
		/*
		if ((MemberConst.MANAGE_CODE_HOMEHAS.equals(or.getSmallSellerCode()) || StringUtils
				.isBlank(or.getSmallSellerCode())) && flag) 
		*/
		//查询物流号和物流公司(在没有返回轨迹的情况下也返回)
		try{
			MDataMap orderShipment = DbUp.upTable("oc_order_shipments").oneWhere("waybill,logisticse_name", "-zid", "order_code=:order_code","order_code",inputParam.getOrder_code());
			if(orderShipment!=null){
				apiHomeOrderTrackingResult.setYc_express_num(orderShipment.get("waybill"));
				apiHomeOrderTrackingResult.setYc_delivergoods_user_name(orderShipment.get("logisticse_name"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		if(!"DD".equals(inputParam.getOrder_code().substring(0, 2)) && !"OS".equals(inputParam.getOrder_code().substring(0, 2)) && !"HH".equals(inputParam.getOrder_code().substring(0, 2))) {
			 //LD订单的物流轨迹处理
			sOrderCode = inputParam.getOrder_code();
			
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
					
					if(StringUtils.isBlank(apiHomeOrderTrackingResult.getYc_express_num())) {
						apiHomeOrderTrackingResult.setYc_express_num(StringUtils.trimToEmpty(track.getYc_express_num()));
						apiHomeOrderTrackingResult.setYc_delivergoods_user_name(StringUtils.trimToEmpty(track.getYc_delivergoods_user_name()));
					}
				}
				apiHomeOrderTrackingResult.setApiHomeOrderTrackingListResult(aor);
			}
			
			if(StringUtils.isBlank(apiHomeOrderTrackingResult.getLogisticsTips())
					&& apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty()){
				apiHomeOrderTrackingResult.setLogisticsTips("暂无物流信息!");
			}
		
		} else {
			if(DbUp.upTable("oc_express_detail").count("order_code", inputParam.getOrder_code())==0)
			{
				sOrderCode = OrderHelper.getOrderCodeByOutCode(inputParam
						.getOrder_code());
				if ("".equals(sOrderCode)) { // 因为获取订单轨迹 必须用out_order_code
					sOrderCode = inputParam.getOrder_code();
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
				map.put("order_code", inputParam.getOrder_code());
				
				//物流修改订单后要查询最新的运单号，而且要加上运单号去掉历史运单流水
				MDataMap waybillMap = DbUp.upTable("oc_order_shipments").one("order_code",inputParam.getOrder_code());
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
			String orderStatus = (String)DbUp.upTable("oc_orderinfo").dataGet("order_status", "", new MDataMap("order_code",inputParam.getOrder_code()));
			if(("4497153900010002".equals(orderStatus) 
					|| ("4497153900010003".equals(orderStatus)) && apiHomeOrderTrackingResult.getApiHomeOrderTrackingListResult().isEmpty())){
				LoadSkuInfoSpread loadSkuInfoSpread = new LoadSkuInfoSpread();
				PlusModelSkuQuery sq = new PlusModelSkuQuery();
				
				// 理论上只查询一个商品就行，因为直邮的商品已经被拆为单独的订单了
				MDataMap m = DbUp.upTable("oc_orderdetail").oneWhere("product_code", "", "", "order_code",inputParam.getOrder_code(),"gift_flag","1");
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
				apiHomeOrderTrackingResult.setLogisticsTips("等待揽收中，请您稍后查询!");
				List<ApiHomeOrderTrackingListResult> apiHomeOrderTrackingListResult = new ArrayList<ApiHomeOrderTrackingListResult>();
				ApiHomeOrderTrackingListResult tracking = new ApiHomeOrderTrackingListResult();
				tracking.setOrderTrackContent("等待揽收中，请您稍后查询!");
				apiHomeOrderTrackingListResult.add(tracking);
				apiHomeOrderTrackingResult.setApiHomeOrderTrackingListResult(apiHomeOrderTrackingListResult);
			}
		}		

		return apiHomeOrderTrackingResult;
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
