package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.OrderItemList;
import com.cmall.ordercenter.model.UserInfo;
import com.srnpr.xmasorder.enumer.ETeslaExec;
import com.srnpr.xmasorder.model.kaola.BookKaolaOrderResponse;
import com.srnpr.xmasorder.model.kaola.ConfirmKaolaOrderResponse;
import com.srnpr.xmasorder.model.kaola.OrderForm;
import com.srnpr.xmasorder.model.kaola.Packages;
import com.srnpr.xmasorder.x.TeslaXOrder;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.util.ProCityLoader;
import com.srnpr.zapcom.basehelper.ALibabaJsonHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class KaolaOrderService {

	//===================================================================					
	//调用考拉确认订单接口
	public boolean upKaolaConfirmOrderInterface(List<OrderItemList> orderItemList, UserInfo userInfo, TeslaXOrder teslaXOrder) {
		String apiName = "orderConfirm";
		TreeMap<String, String> params = new TreeMap<String, String>();
		//由于加价购商品出现请求参数集合中出现相同的OrderItemList导致接口返回异常，需要把相同的合并处理
		orderItemList = validateParam(orderItemList);
		//channelId,timestamp,v,sign_method,app_key,sign
		params.put("orderItemList", "{\"orderItemList\":" + ALibabaJsonHelper.toJson(orderItemList) + "}");
		params.put("userInfo", "{\"userInfo\":" + ALibabaJsonHelper.toJson(userInfo) + "}");
		
		String cacheKey = userInfo.getAccountId()+DigestUtils.md5Hex(params.get("orderItemList")+params.get("userInfo"));
		String result = XmasKv.upFactory(EKvSchema.KaoLaOrderConfirm).get(cacheKey);
		boolean formCache = true;
		
		if(StringUtils.isBlank(result)) {
			result = RsyncKaoLaSupport.doPostRequest(apiName, "source", params);
			formCache = false;
		}
		
		JSONObject resultJson = JSON.parseObject(result);
		if(resultJson.getInteger("recCode") == 200) {
			ConfirmKaolaOrderResponse res = new ConfirmKaolaOrderResponse();
			JsonHelper<ConfirmKaolaOrderResponse> responseJsonHelper = new JsonHelper<ConfirmKaolaOrderResponse>();
			res = responseJsonHelper.GsonFromJson(resultJson.toString(), res);
			if(res != null && res.getOrderForm() != null) {
				OrderForm orderForm = res.getOrderForm();
				teslaXOrder.setOrderForm(orderForm);
			}		
			
			// 如果是订单确认调用则把考拉接口响应放入缓存
			if(!formCache && teslaXOrder.getStatus().getExecStep() == ETeslaExec.Confirm) {
				XmasKv.upFactory(EKvSchema.KaoLaOrderConfirm).setex(cacheKey, 10, result);
			}
		} else {			
			return false;
		}
		return true;
	}
	

	private List<OrderItemList> validateParam(List<OrderItemList> orderItemList) {
		// TODO Auto-generated method stub
		List<OrderItemList> newOrderItemList = new ArrayList<OrderItemList>();
		List<String> skuIdList = new ArrayList<String>();
		for(Iterator<OrderItemList> iterator = orderItemList.iterator();iterator.hasNext();) {
			OrderItemList nextOrderItem = iterator.next();
			if(skuIdList.contains(nextOrderItem.getSkuId().toString())) {	
				OrderItemList temItem = newOrderItemList.get(skuIdList.indexOf(nextOrderItem.getSkuId().toString()));
				temItem.setBuyAmount(temItem.getBuyAmount()+1);	
			}else {
				skuIdList.add(nextOrderItem.getSkuId());
				newOrderItemList.add(nextOrderItem);
			}
		};
		return newOrderItemList;
	}


	//===================================================================					
	//调用考拉下单接口
	public boolean upKaolaBookOrderInterface(List<OrderItemList> orderItemList, UserInfo userInfo, String orderCode) {
		String apiName = "bookorder";
		TreeMap<String, String> params = new TreeMap<String, String>();
		//channelId,timestamp,v,sign_method,app_key,sign
		params.put("thirdPartOrderId", orderCode);
		params.put("orderItemList", "{\"orderItemList\":" + ALibabaJsonHelper.toJson(orderItemList) + "}");
		params.put("userInfo", "{\"userInfo\":" + ALibabaJsonHelper.toJson(userInfo) + "}");
		String result = RsyncKaoLaSupport.doPostRequest(apiName, "source", params);
		JSONObject resultJson = JSON.parseObject(result);
		if(resultJson.getInteger("recCode") == 200) {
			BookKaolaOrderResponse res = new BookKaolaOrderResponse();
			JsonHelper<BookKaolaOrderResponse> responseJsonHelper = new JsonHelper<BookKaolaOrderResponse>();
			res = responseJsonHelper.GsonFromJson(resultJson.toString(), res);
			if(res != null && res.getGorder() != null) {
				String id = res.getGorder().getId();
				//String status = new KaolaOrderService().upKaolaQueryOrderInterface(orderCode);
				DbUp.upTable("oc_order_kaola_list").dataUpdate(new MDataMap("out_order_code",id,"status","1","order_code",orderCode), "out_order_code,status", "order_code");
				//处理零元单
				//MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",orderCode,"buyer_code",userInfo.getAccountId());
				MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				if(orderInfo != null && "4497153900010002".equals(orderInfo.get("order_status"))){
					//直接写入支付定时
					new KaolaOrderService().payKaolaOrder(orderCode);
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	//===================================================================					
	//调用考拉查询订单接口
	public String upKaolaQueryOrderInterface(String orderCode) {
		String apiName = "queryOrderStatus";
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("thirdPartOrderId", orderCode);
		String result = RsyncKaoLaSupport.doPostRequest(apiName, "channelId", params);
		if(!StringUtils.isBlank(result)) {
			JSONObject resultJson = JSON.parseObject(result);
			if(resultJson.getInteger("recCode") == 200) {
				String resultList = resultJson.getString("result");
				List<String> list = JSONArray.parseArray(resultList, String.class);
				JSONObject orderInfo = JSON.parseObject(list.get(0));
				String status = orderInfo.getString("status");
				return KaolaOrderService.statusMapper(status);
			} else {
				return "";
			}			
		} else {
			return "";
		}		
	}
	
	//===================================================================
	/**
	 * 将网易考拉支付订单的任务写入定时
	 * @param order_code
	 */
	public void payKaolaOrder(String order_code) {
		//写入定时任务，定时执行返还微公社金额
		MDataMap jobMap = new MDataMap();
		jobMap.put("uid", WebHelper.upUuid());
		jobMap.put("exec_code", WebHelper.upCode("ET"));
		jobMap.put("exec_type", "449746990013");
		jobMap.put("exec_info", order_code);
		jobMap.put("create_time", DateUtil.getSysDateTimeString());
		jobMap.put("begin_time", "");
		jobMap.put("end_time", "");
		jobMap.put("exec_time", DateUtil.getSysDateTimeString());
		jobMap.put("flag_success","0");
		jobMap.put("remark", "KaolaOrderService line 126");
		jobMap.put("exec_number", "0");
		DbUp.upTable("za_exectimer").dataInsert(jobMap);
	}
	
	/**
	 * 将网易考拉取消订单的任务写入定时
	 * @param order_code
	 */
	public void cancelKaolaOrder(String order_code) {
		//写入定时任务，定时执行返还微公社金额
		MDataMap jobMap = new MDataMap();
		jobMap.put("uid", WebHelper.upUuid());
		jobMap.put("exec_code", WebHelper.upCode("ET"));
		jobMap.put("exec_type", "449746990011");
		jobMap.put("exec_info", order_code);
		jobMap.put("create_time", DateUtil.getSysDateTimeString());
		jobMap.put("begin_time", "");
		jobMap.put("end_time", "");
		jobMap.put("exec_time", DateUtil.getSysDateTimeString());
		jobMap.put("flag_success","0");
		jobMap.put("remark", "KaolaOrderService line 147");
		jobMap.put("exec_number", "0");
		DbUp.upTable("za_exectimer").dataInsert(jobMap);
	}
	
	//设置用户的地址
	public static boolean setOrderAddress(UserInfo userInfo, String areaCode) {
		MDataMap areaMap = DbUp.upTable("sc_tmp").one("code", areaCode);
		String contyCode = areaCode;
		String cityCode = "";
		String provinceCode = "";
		
		// 4级转3级地址
		if(areaMap != null && NumberUtils.toInt(areaMap.get("code_lvl")) == 4) {
			contyCode = areaMap.get("p_code");
			areaMap = DbUp.upTable("sc_tmp").one("code", contyCode);
		}
		
		// 二级地址
		cityCode = StringUtils.left(areaCode, 4)+"00";
		areaMap = DbUp.upTable("sc_tmp").one("code", areaMap.get("p_code"));
		if(areaMap != null && NumberUtils.toInt(areaMap.get("code_lvl")) == 2) {
			cityCode = areaMap.get("code");
		}
		
		// 一级地址
		provinceCode = StringUtils.left(contyCode, 2)+"0000";
		areaMap = DbUp.upTable("sc_tmp").one("code", areaMap.get("p_code"));
		if(areaMap != null && NumberUtils.toInt(areaMap.get("code_lvl")) == 1) {
			provinceCode = areaMap.get("code");
		}
		
		userInfo.setProvinceCode(provinceCode);   
		userInfo.setProvinceName(ProCityLoader.getName(provinceCode));
		userInfo.setCityCode(cityCode);
		userInfo.setCityName(ProCityLoader.getName(cityCode));
		userInfo.setDistrictCode(contyCode);
		userInfo.setDistrictName(ProCityLoader.getName(contyCode));
		return true;
	}
	
	/**
	 * 根据活动编号获取sku编号
	 * @param itemCode
	 * @return
	 */
	public static String upSkuCode(String itemCode) {
		MDataMap mItemMap = DbUp.upTable("sc_event_item_product").one("item_code", itemCode);
		if(mItemMap != null && mItemMap.get("sku_code") != null) {
			return mItemMap.get("sku_code");
		}
		return "";
	}
	
	/**
	 *  0, "订单同步失败" 
		1, "订单同步成功（等待支付）" 
		2, "订单支付成功（等待发货）" 
		3, "订单支付失败"
		4, "订单已发货"
		5, "交易成功" 
		6, "订单交易失败（用户支付后不能发货）【最终状态】"
		7, "订单关闭"
		8, "退款成功"(分销走线下不做更新)
		9, "退款失败"(分销走线下不做更新)
	 * @param status
	 * @return
	 */
	private static String statusMapper(String status) {
		String res = "";
		if(StringUtils.isBlank(status)) {
			return res;
		}
		switch(status) {
			case "0" : res = "";break;
			case "1" : res = "4497153900010001";break;
			case "2" : res = "4497153900010002";break;
			case "3" : res = "";break;
			case "4" : res = "4497153900010003";break;
			case "5" : res = "4497153900010005";break;
			case "6" : res = "4497153900010006";break;
			case "7" : res = "4497153900010005";break;
			case "8" : res = "";break;
			case "9" : res = "";break;
			default : res = "";
		}
		return res;
	}
}
