package com.cmall.ordercenter.service.quaidi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 快递100 发送接口
 * 
 * @author wangkecheng
 *
 */
public class SendServiceJob   extends RootJob{

	public void doExecute(JobExecutionContext context) {
		this.sendExpressNo();
	} 
	public void sendExpressNo(){
		DbTemplate dt = DbUp.upTable("oc_order_shipments").upTemplate();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		List<Map<String,Object>> list = dt.queryForList("select zid,order_code ,logisticse_code as company,waybill as number from oc_order_shipments where is_send100_flag = 0 and send_count < 5", paramMap);
		if(list.size() == 0){
			//System.out.println("no data need to be sent.");
		}
		for(Map<String,Object> map : list){
			try {
				//取快递地址 
				MDataMap one = DbUp.upTable("oc_orderadress").one("order_code",map.get("order_code").toString());
				if(one != null){
					//System.out.println("address :"+one.get("address"));
					map.put("to", one.get("address"));
				}
				String ret = this.doSend(map);
			//	System.out.println("kuaidi100 return str :"+ret);
				int sendSTatus = this.isSendSuccess(ret);
				
				//paramMap.put("is_send100_flag", sendSTatus);
				paramMap.put("send_remark", ret);
				paramMap.put("zid", map.get("zid"));
				if(sendSTatus == 1){
					dt.update("update oc_order_shipments set is_send100_flag = 1,send_remark = '' where zid = :zid", paramMap);
				}else{
					dt.update("update oc_order_shipments set send_remark = :send_remark,send_count = send_count + 1 where zid = :zid", paramMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	/**
	 * 向快递100 发数据
	 * @param map
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String doSend(Map<String,Object> map) throws JsonProcessingException{
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, String> parameters = new HashMap<String, String>();
		
		jsonMap.put("company", map.get("company"));
		jsonMap.put("number", map.get("number"));//快递编号
		//jsonMap.put("from", "广东深圳南山区");
		jsonMap.put("to", map.get("to"));
		jsonMap.put("key", "kZTYgQsC6194");
		
		parameters.put("callbackurl", bConfig("ordercenter.quaidi100_callbackurl"));
		parameters.put("salt", bConfig("ordercenter.quaidi100_secure_key"));
		parameters.put("resultv2", "1");
		
		jsonMap.put("parameters",parameters);
		
		String param = objectMapper.writeValueAsString(jsonMap);
		//System.out.println("param :"+param);
		
		MDataMap p = new MDataMap();
		p.put("schema", "json");
		p.put("param", param);
		
		try {
			return WebClientSupport.upPost("http://www.kuaidi100.com/poll", p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 解析 快递100 返回结果
	 * @param str
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public int isSendSuccess(String str) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper objectMapper=new ObjectMapper();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
		if("true".equals(map.get("result").toString())){
			return 1;
		}
		return 0;
	}
}
