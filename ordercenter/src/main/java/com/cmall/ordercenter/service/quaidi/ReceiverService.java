package com.cmall.ordercenter.service.quaidi;

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

public class ReceiverService extends BaseClass{
	
	public String doReceiver(String param ,String sign){
		
		String msg = "";
		String result = "false";
		//System.out.println("param :"+param); 
		//System.out.println("sign :"+sign);
		
		if(param == null || param == "" || sign == null || sign == "" ){
			return this.responseCode(result,"params not allow null.");
		} 
		try { 
			//String md5 =  SecrurityHelper.getEncoderByMd5(param + bConfig("ordercenter.quaidi100_secure_key"));
			String md5 =  SecrurityHelper.MD5(param + bConfig("ordercenter.quaidi100_secure_key"));
			//System.out.println("md5 :"+md5);
			//System.out.println("key :"+bConfig("ordercenter.quaidi100_secure_key"));
			if(!md5.equals(sign)){
				//return this.responseCode(result,"authorize error.");
			}
		}  catch (Exception e1) {
			e1.printStackTrace();
		} 
		try {
			result = this.analyse(param);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return this.responseCode(result,msg);
	}
	/**
	 * 解析 快递100 推送来的数据，并把最新的数据插入库中
	 * @param jsonStr
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public String analyse(String jsonStr) throws JsonParseException, JsonMappingException, IOException{
		DbTemplate dt = DbUp.upTable("oc_express_detail").upTemplate();
		 
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String,Object> allMap = objectMapper.readValue(jsonStr, Map.class);
		Map<String,Object> lastResultMap = (Map<String,Object>)allMap.get("lastResult");
		String com = lastResultMap.get("com").toString();//快递公司编码
		String nu = lastResultMap.get("nu").toString();//单号
		List<Map<String,String>> list = (List<Map<String,String>>)lastResultMap.get("data");
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("com", com);
		paramMap.put("nu", nu);
		String orderCode = dt.queryForObject("select order_code from oc_order_shipments where logisticse_code = :com and waybill = :nu", paramMap, String.class);
		paramMap.put("order_code", orderCode);
		
		MDataMap logisticsMap = new  MDataMap();
		for(Map<String,String> map : list ){
			//System.out.println("analyse :" +map.get("context") + " : "+map.get("ftime"));
			paramMap.put("context",  map.get("context"));
			paramMap.put("ftime", map.get("ftime"));
			// 548需求添加字段：状态、城市编码、城市名称
			paramMap.put("status", StringUtils.trimToEmpty(map.get("status")));
			paramMap.put("areaCode", StringUtils.trimToEmpty(map.get("areaCode")));
			paramMap.put("areaName", StringUtils.trimToEmpty(map.get("areaName")));
          
          int n = dt.queryForObject("select count(zid) from oc_express_detail where order_code = :order_code and time = :ftime", paramMap, Integer.class);
          //只插入数据库中不存在的
          if(n == 0){
        	  // 548添加,根据订单号查询最后一条物流信息
        	  Map<String, Object> lastExpress = DbUp.upTable("oc_express_detail").dataSqlOne
        			  ("SELECT * FROM oc_express_detail WHERE order_code = '" + orderCode + "' ORDER BY time DESC LIMIT 1", new MDataMap());
          	
        	  dt.update("insert into oc_express_detail(order_code,logisticse_code,waybill,context,time,status,areaCode,areaName) "
          			+ " value (:order_code,:com,:nu,:context,:ftime,:status,:areaCode,:areaName)", paramMap);
          	
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
		if(null != logisticsMap && logisticsMap.size() > 0) {				
			DbUp.upTable("nc_logistics_notice_push_news").dataInsert(logisticsMap);
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
	
	public String analysexxx(String jsonStr) {
		DbTemplate dt = DbUp.upTable("oc_express_detail").upTemplate();
		 
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("com", "111");
		paramMap.put("nu", "222");
		Object orderCode = dt.queryForObject("select order_code from oc_order_shipments where logisticse_code = :com and waybill = :nu", paramMap, Object.class);
		paramMap.put("order_code", orderCode);
		return "ok";
	}
	public void test(String jsonStr){
		//Map<String,Object> paramMap = new HashMap<String,Object>();
		ObjectMapper objectMapper=new ObjectMapper();
		//objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			Map<String,Object> allMap = objectMapper.readValue(jsonStr, Map.class);
			Map<String,Object> lastResultMap = (Map<String,Object>)allMap.get("lastResult");
			
			//System.out.println("lastResult:" +lastResultMap);
			
			List<Map<String,String>> list = (List<Map<String,String>>)lastResultMap.get("data");
			for(Map<String,String> map : list ){
				//System.out.println("s:" +map.get("context") + " : "+map.get("ftime"));
			}
	
			//System.out.println("data:" +lastResultMap.get("data"));
			//System.out.println("com:" +lastResultMap.get("com"));
			//System.out.println("nu:" +lastResultMap.get("nu"));
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 向快递100返回数据
	 * @param processStatus
	 * @return
	 */
	public String responseCode(String processStatus,String msg){
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String,String> map = new HashMap<String,String>();
		map.put("result", processStatus);
		map.put("returnCode", "");
		map.put("message", msg);
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
