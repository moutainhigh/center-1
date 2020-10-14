package com.ordercenter.express.service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ReceiveCallback extends BaseClass{
	MWebResult logger = new MWebResult();
	public String doReceiver(String param){
		String msg = "";
		String result ="false";
		String return_code="";
		if(param == null || param == "" ){
			logger.inErrorMessage(939303004);
			return this.responseCode(result,"params not allow null.","失败");
		} 
		try { 
			String md5 =  SecrurityHelper.MD5(param + bConfig("ordercenter.quaidi100_secure_key"));
			//System.out.println("md5 :"+md5);
			//System.out.println("key :"+bConfig("ordercenter.quaidi100_secure_key"));
//			if(!md5.equals(sign)){
//				logger.inErrorMessage(939303005);//加密方式不对
//				return this.responseCode(result,"sign error","失败");
//				
//			}
		}  catch (Exception e1) {
			logger.inErrorMessage(939303006);//加密出现异常
			e1.printStackTrace();
		} 
		try {
			//解析回调参数
			result = this.analyse(param);
			if(result.equals("true")){
				return_code="200";
				msg="成功";
			}else{
				return_code="500";
				msg="失败";
			}
			//System.out.println("解析完成");
		} catch (Exception e) {
			logger.inErrorMessage(939303007);//解析回调参数出现异常
			e.printStackTrace();
		} 
		return this.responseCode(result,return_code,msg);
	}
	
	/**
	 * 解析 快递100 推送来的数据，并把最新的数据插入库中
	 * @param jsonStr
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public String analyse(String jsonStr) throws JsonParseException, JsonMappingException, IOException{
		//System.out.println("开始解析数据");
		DbTemplate dt = DbUp.upTable("oc_express_detail").upTemplate();
		DbTemplate dt1 = DbUp.upTable("oc_order_shipments").upTemplate();
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String,Object> allMap = objectMapper.readValue(jsonStr, Map.class);
		Map<String,Object> lastResultMap = (Map<String,Object>)allMap.get("lastResult");
		String com = lastResultMap.get("com").toString();//快递公司编码
		String nu = lastResultMap.get("nu").toString();//单号
		List<Map<String,String>> list = (List<Map<String,String>>)lastResultMap.get("data");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("com", com);
		paramMap.put("nu", nu);
		String orderCode;
		List<String> list1 =dt1.queryForList("select order_code from oc_order_shipments where logisticse_code = :com and waybill = :nu", paramMap, String.class);
//		 if(list1!=null &&list1.size()>0){
//			 orderCode = list1.get(0);
//			  }else {
//				  orderCode = null;
//			  }
		System.out.println("订单数量------------"+list1.size());
		if (list1 != null && list1.size() > 0) {
			MDataMap logisticsMap = new  MDataMap();
			for (int i = 0; i < list1.size(); i++) {
				orderCode = list1.get(i);
				System.out.println(i+"----------------"+orderCode);
				paramMap.put("order_code", orderCode);
				
				//for (Map<String, String> map : list) {
				for(int j = list.size()-1;j>=0;j--) {
					Map<String, String> map = list.get(j);
					// System.out.println("analyse :" +map.get("context") +
					// " : "+map.get("ftime"));
					paramMap.put("context", map.get("context"));
					paramMap.put("ftime", map.get("ftime"));
					// 548需求添加字段：状态、城市编码、城市名称
					paramMap.put("status", StringUtils.trimToEmpty(map.get("status")));
					paramMap.put("areaCode", StringUtils.trimToEmpty(map.get("areaCode")));
					paramMap.put("areaName", StringUtils.trimToEmpty(map.get("areaName")));
					List<Integer> count = dt.queryForList("select zid from oc_express_detail where order_code = :order_code and time = :ftime",	
							paramMap, Integer.class);
					// 只插入数据库中不存在的
					if (count.size() == 0) {
						// 548添加,根据订单号查询最后一条物流信息
						Map<String, Object> lastExpress = DbUp.upTable("oc_express_detail").dataSqlOne
								("SELECT * FROM oc_express_detail WHERE order_code = '" + orderCode + "' ORDER BY time DESC LIMIT 1", new MDataMap());
						
						dt.update("insert into oc_express_detail(order_code,logisticse_code,waybill,context,time,status,areaCode,areaName) value "
								+ "(:order_code,:com,:nu,:context,:ftime,:status,:areaCode,:areaName)",	paramMap);
						
						// 548添加,城市发生变化，或者是物流签收状态变化，则往push消息表插入数据
			          	if(null != lastExpress) {
			          		if(lastExpress.get("time").toString().compareTo(paramMap.get("ftime").toString()) < 0) {
			          			
			          			String areaCode = (String) lastExpress.get("areaCode");
			          			String status = (String) lastExpress.get("status");
			          			String newAreaCode = (String) paramMap.get("areaCode");
			          			String newStatus = (String) paramMap.get("status");
			          			if(newAreaCode.equals(areaCode)) { // 城市相同则看物流状态
			          				if(newStatus.equals(status)){
			          					// 物流状态不变,不发通知
			          				}else { // 发送通知
			          					logisticsMap = this.saveLogisticsNotice(paramMap);
			          				}
			          			}else { //城市不同,发送通知
			          				if("在途".equals(paramMap.get("status"))) {
			          					logisticsMap = this.saveLogisticsNotice(paramMap);
			          				}else {
			          					if(newStatus.equals(status)){
			          						// 物流状态不变,不发通知
			          					}else { // 发送通知
			          						logisticsMap = this.saveLogisticsNotice(paramMap);
			          					}
			          				}
			          			}
			          		}
			          	}else { // 没有物流信息,直接新增
			          		logisticsMap = this.saveLogisticsNotice(paramMap);
			          	}
					}
				}
			}
			if(null != logisticsMap && logisticsMap.size() > 0) {				
				DbUp.upTable("nc_logistics_notice_push_news").dataInsert(logisticsMap);
			}
		}
		return "true";
	}
	
	// 548添加,封装要发送的物流通知存入push表中
	public MDataMap saveLogisticsNotice(Map<String,Object> paramMap) {
		MDataMap logisticsMap = new  MDataMap();
		DbTemplate dt = DbUp.upTable("oc_express_detail").upTemplate();
		// 推送用户(购买人)
  		String member_code = (String)DbUp.upTable("oc_orderinfo").dataGet("buyer_code", "", new MDataMap("order_code",(String)paramMap.get("order_code")));
  		// 根据快递公司编号查询快递名称
  		String company_name = (String)DbUp.upTable("sc_logisticscompany").dataGet("company_name", "", new MDataMap("company_code",(String)paramMap.get("com")));
  		String title = "";
  		String message = "";
  		if("揽收".equals(paramMap.get("status"))) {
  			title = "商品发货通知";
  			message = "您的商品已经通过"+company_name+"快递发货";
  		}else if("签收".equals(paramMap.get("status"))) {
  			title = "商品签收提醒";
  			message = company_name+"快递显示您的订单已签收";
  		}else if("派件".equals(paramMap.get("status"))) {
  			title = "商品派件通知";
  			message = company_name+"快递已安排为您配送，请注意查收";
  		}else if("退回".equals(paramMap.get("status"))) {
  			title = "商品拒收提醒";
  			message = company_name+"快递显示您的订单拒绝签收，请知悉";
  		}else if("在途".equals(paramMap.get("status"))) {
  			if(StringUtils.isNotBlank((String)paramMap.get("areaName"))) {  				
  				title = "商品物流通知";
  				message = "您购买的商品已经抵达"+paramMap.get("areaName");
  			}
  		}
  		// 商品主图
  		List<String> prod_main_pic = dt.queryForList("SELECT mainpic_url FROM productcenter.pc_productinfo pp WHERE pp.product_code = "
  				+ "( SELECT oo.product_code FROM ordercenter.oc_orderdetail oo WHERE oo.order_code = :order_code LIMIT 1 )", paramMap, String.class);
  		
  		if(!"".equals(title)) {
  			logisticsMap.put("uid", UUID.randomUUID().toString().replaceAll("-", ""));
  			logisticsMap.put("member_code", StringUtils.trimToEmpty(member_code));
  			logisticsMap.put("title", title);
  			logisticsMap.put("message", message);
  			logisticsMap.put("prod_main_pic", prod_main_pic.isEmpty()?"":prod_main_pic.get(0));
  			logisticsMap.put("create_time", DateUtil.getSysDateTimeString());
  			logisticsMap.put("push_times", "0");
  			logisticsMap.put("order_code", (String)paramMap.get("order_code"));
  			logisticsMap.put("waybill", (String) paramMap.get("nu"));
  			logisticsMap.put("to_page", "14");
  			logisticsMap.put("if_read", "0");
  			
  			//DbUp.upTable("nc_logistics_notice_push_news").dataInsert(logisticsMap);
  		}
  		return logisticsMap;
	}
	
	
	/**
	 * 向快递100返回数据
	 * @param processStatus
	 * @return
	 */
	public String responseCode(String processStatus,String return_code,String msg){
		//System.out.println("返回快递100数据");
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String,String> map = new HashMap<String,String>();
		map.put("result", processStatus);
		map.put("returnCode", return_code);
		map.put("message", msg);
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			logger.inErrorMessage(939303008);//返回快递100数据失败
			e.printStackTrace();
		}
		return "";
	}
	//测试方法
	public static void main(String[] args) {
		String josnstr1=
				"{\"status\":\"polling\",\"billstatus\":\"sending\",\"message\":\"正在派件\",\"lastResult\":{\"message\":\"ok\",\"nu\":\"111111111111111111\",\"ischeck\":\"0\",\"condition\":\"H100\",\"com\":\"shunfeng\",\"status\":\"200\",\"state\":\"5\",\"data\":[" + 
				"{\"time\":\"2015-08-13 12:28:00\",\"ftime\":\"2015-08-13 12:28:00\",\"status\":\"派件\",\"areaCode\":\"370600\",\"areaName\":\"烟台市\",\"context\":\"烟台市|派件|烟台市【烟台莱山区二部】，【宋德鑫18354556186】正在派件\"}," + 
				"{\"time\":\"2015-08-13 06:40:23\",\"ftime\":\"2015-08-13 06:40:23\",\"status\":\"在途\",\"areaCode\":\"370600\",\"areaName\":\"烟台市\",\"context\":\"烟台市|到件|到烟台市【烟台莱山区二部】\"}," + 
				"{\"time\":\"2015-08-13 04:58:56\",\"ftime\":\"2015-08-13 04:58:56\",\"status\":\"在途\",\"areaCode\":\"370600\",\"areaName\":\"烟台市\",\"context\":\"烟台市|发件|烟台市【烟台分拨中心】，正发往【烟台莱山区二部】\"}," + 
				"{\"time\":\"2015-08-13 02:37:46\",\"ftime\":\"2015-08-13 02:37:46\",\"status\":\"在途\",\"areaCode\":\"370600\",\"areaName\":\"烟台市\",\"context\":\"烟台市|到件|到烟台市【烟台分拨中心】\"},\n" + 
				"{\"time\":\"2015-08-12 12:25:27\",\"ftime\":\"2015-08-12 12:25:27\",\"status\":\"在途\",\"areaCode\":\"370100\",\"areaName\":\"济南市\",\"context\":\"济南市|发件|济南市【济南分拨中心】，正发往【烟台分拨中心】\"}," + 
				"{\"time\":\"2015-08-12 11:07:34\",\"ftime\":\"2015-08-12 11:07:34\",\"status\":\"在途\",\"areaCode\":\"370100\",\"areaName\":\"济南市\",\"context\":\"济南市|到件|到济南市【济南分拨中心】\"},\n" + 
				"{\"time\":\"2015-08-12 02:47:33\",\"ftime\":\"2015-08-12 02:47:33\",\"status\":\"在途\",\"areaCode\":\"410100\",\"areaName\":\"郑州市\",\"context\":\"郑州市|发件|郑州市【郑州分拨中心】，正发往【济南分拨中心】\"}," + 
				"{\"time\":\"2015-08-12 02:45:37\",\"ftime\":\"2015-08-12 02:45:37\",\"status\":\"在途\",\"areaCode\":\"410100\",\"areaName\":\"郑州市\",\"context\":\"郑州市|到件|到郑州市【郑州分拨中心】\"},\n" + 
				"{\"time\":\"2015-08-11 21:38:40\",\"ftime\":\"2015-08-11 21:38:40\",\"status\":\"在途\",\"areaCode\":\"410700\",\"areaName\":\"新乡市\",\"context\":\"新乡市|发件|新乡市【新乡分拨仓】，正发往【郑州分拨中心】\"}," + 
				"{\"time\":\"2015-08-11 21:32:13\",\"ftime\":\"2015-08-11 21:32:13\",\"status\":\"在途\",\"areaCode\":\"410700\",\"areaName\":\"新乡市\",\"context\":\"新乡市|到件|到新乡市【新乡分拨仓】\"},\n" + 
				"{\"time\":\"2015-08-11 20:41:32\",\"ftime\":\"2015-08-11 20:41:32\",\"status\":\"在途\",\"areaCode\":\"410700\",\"areaName\":\"新乡市\",\"context\":\"新乡市|发件|新乡市【新乡市区六部】，正发往【新乡分拨仓】\"}," + 
				"{\"time\":\"2015-08-11 19:05:50\",\"ftime\":\"2015-08-11 19:05:50\",\"status\":\"在途\",\"areaCode\":\"410700\",\"areaName\":\"新乡市\",\"context\":\"新乡市|到件|到新乡市【新乡市区六部】\"}," + 
				"{\"time\":\"2015-08-11 19:05:17\",\"ftime\":\"2015-08-11 19:05:17\",\"status\":\"揽收\",\"areaCode\":\"410700\",\"areaName\":\"新乡市\",\"context\":\"新乡市|收件|新乡市【新乡市区六部】，【焦志强/0373-5218088】已揽收\"}]}}";
				//"{\"status\":\"polling\",\"billstatus\":\"sending\",\"message\":\"正在派件\",\"lastResult\":{\"message\":\"ok\",\"nu\":\"3100564910594\",\"ischeck\":\"0\",\"condition\":\"H100\",\"com\":\"yunda\",\"status\":\"200\",\"state\":\"5\",\"data\":[{\"time\":\"2015-08-13 12:28:00\",\"ftime\":\"2015-08-13 12:28:00\",\"context\":\"烟台市|派件|烟台市【烟台莱山区二部】，【宋德鑫18354556186】正在派件\"},{\"time\":\"2015-08-13 06:40:23\",\"ftime\":\"2015-08-13 06:40:23\",\"context\":\"烟台市|到件|到烟台市【烟台莱山区二部】\"},{\"time\":\"2015-08-13 04:58:56\",\"ftime\":\"2015-08-13 04:58:56\",\"context\":\"烟台市|发件|烟台市【烟台分拨中心】，正发往【烟台莱山区二部】\"},{\"time\":\"2015-08-13 02:37:46\",\"ftime\":\"2015-08-13 02:37:46\",\"context\":\"烟台市|到件|到烟台市【烟台分拨中心】\"},{\"time\":\"2015-08-12 12:25:27\",\"ftime\":\"2015-08-12 12:25:27\",\"context\":\"济南市|发件|济南市【济南分拨中心】，正发往【烟台分拨中心】\"},{\"time\":\"2015-08-12 11:07:34\",\"ftime\":\"2015-08-12 11:07:34\",\"context\":\"济南市|到件|到济南市【济南分拨中心】\"},{\"time\":\"2015-08-12 02:47:33\",\"ftime\":\"2015-08-12 02:47:33\",\"context\":\"郑州市|发件|郑州市【郑州分拨中心】，正发往【济南分拨中心】\"},{\"time\":\"2015-08-12 02:45:37\",\"ftime\":\"2015-08-12 02:45:37\",\"context\":\"郑州市|到件|到郑州市【郑州分拨中心】\"},{\"time\":\"2015-08-11 21:38:40\",\"ftime\":\"2015-08-11 21:38:40\",\"context\":\"新乡市|发件|新乡市【新乡分拨仓】，正发往【郑州分拨中心】\"},{\"time\":\"2015-08-11 21:32:13\",\"ftime\":\"2015-08-11 21:32:13\",\"context\":\"新乡市|到件|到新乡市【新乡分拨仓】\"},{\"time\":\"2015-08-11 20:41:32\",\"ftime\":\"2015-08-11 20:41:32\",\"context\":\"新乡市|发件|新乡市【新乡市区六部】，正发往【新乡分拨仓】\"},{\"time\":\"2015-08-11 19:05:50\",\"ftime\":\"2015-08-11 19:05:50\",\"context\":\"新乡市|到件|到新乡市【新乡市区六部】\"},{\"time\":\"2015-08-11 19:05:17\",\"ftime\":\"2015-08-11 19:05:17\",\"context\":\"新乡市|收件|新乡市【新乡市区六部】，【焦志强/0373-5218088】已揽收\"}]}}";
     //   String josnstr="{\"message\":\"到达\",\"status\":\"check\",\"billstatus\":\"polling\",\"lastResult\":{\"message\":\"ok\",\"state\":\"0\",\"status\":\"200\",\"data\":[{\"context\":\"上海分拨中心/装件入车扫描 \",\"time\":\"2012-08-28 16:33:19\",\"ftime\":\"2015-08-13 13:33:19\"},{\"context\":\"上海分拨中心/下车扫描\",\"time\":\"2012-08-27 23:22:42\",\"ftime\":\"2012-08-27 23:22:42\"}],\"com\":\"yuantong\",\"ischeck\":\"0\",\"nu\":\"1234567654321\",\"condition\":\"F00\"}}";
        ReceiveCallback rs =new ReceiveCallback();
        rs.doReceiver(josnstr1);
	}
}
