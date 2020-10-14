package com.ordercenter.express.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.service.quaidi.SendServiceJob;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 向快递100传送数据
 * @author zmm
 *
 */
public class PostOrderService extends RootJob {

	public void doExecute(JobExecutionContext context) {
		this.sendExpressNo();
	}
	//MWebResult result = new MWebResult();
	public void sendExpressNo() {
		DbTemplate dt = DbUp.upTable("oc_order_shipments").upTemplate();
		Map<String, Object> paramMap = new HashMap<String, Object>();
//		String sql = "select zid,order_code ,logisticse_code as company,waybill as number from oc_order_shipments where is_send100_flag = 0 and send_count < 5";
//		System.out.println(sql);
//		List<Map<String, Object>> list = DbUp.upTable("oc_order_shipments").dataSqlList(sql, null);
		List<Map<String,Object>> list = dt.queryForList("select zid,order_code ,logisticse_code as company,waybill as number from oc_order_shipments where logisticse_code!='' and is_send100_flag = 0 and send_count < 5", paramMap);
		if (list.size() == 0) {
			//System.out.println("没有查询到需要传送的数据");
			//result.inErrorMessage(939303001);// 没有需要传送的数据
		}
		
		OrderShipmentsService shipmentsService = new OrderShipmentsService();
		for (Map<String, Object> map : list) {
			try {
				// 取快递地址
				MDataMap orderAddressInfo = DbUp.upTable("oc_orderadress").one("order_code",map.get("order_code").toString());
				if (orderAddressInfo != null) {
					map.put("to", orderAddressInfo.get("address"));
					// 快递100接口要求顺风的快递查询单独添加一个手机号
					if("shunfeng".equalsIgnoreCase(map.get("company")+"")){
						map.put("mobiletelephone", orderAddressInfo.get("mobilephone"));
					}
				}
				//向快递100传送数据
				//System.out.println("向快递100传送数据");
				String ret = this.doSend(map);
				int sendSTatus = this.isSendSuccess(ret);
				paramMap.put("send_remark", ret);
				paramMap.put("zid", map.get("zid"));
				UUID uuid = UUID.randomUUID();
				MDataMap insertDatamap = new MDataMap();
				if (sendSTatus == 1) {
					shipmentsService.onCallKuaidi100(OrderShipmentsService.CALL_TYPE_POST, (String)map.get("order_code"), (String)map.get("company"), (String)map.get("number"));
					
					//System.out.println("数据提交成功");
					insertDatamap.put("uid", uuid.toString().replace("-", ""));
					if(orderAddressInfo == null || StringUtils.isEmpty(orderAddressInfo.get("order_code"))) {
						insertDatamap.put("order_code", map.get("order_code").toString());
					} else {
						insertDatamap.put("order_code",orderAddressInfo.get("order_code").toString());
					}
					
					insertDatamap.put("return_status", "1");
					insertDatamap.put("return_msg", "成功订阅快递100");
					DbUp.upTable("oc_express_loginfo").dataInsert(insertDatamap);
					dt.update("update oc_order_shipments set is_send100_flag = 1,send_remark = '' where zid = :zid", paramMap);
					
					//DbUp.upTable("oc_order_shipments").dataExec("update oc_order_shipments set is_send100_flag = 1,send_remark = '' where zid = :zid", null);	
				} else {
					//System.out.println("数据重复提交");
					insertDatamap.put("uid", uuid.toString().replace("-", ""));
					
					if(orderAddressInfo == null || StringUtils.isEmpty(orderAddressInfo.get("order_code"))) {
						insertDatamap.put("order_code", map.get("order_code").toString());
					} else {
						insertDatamap.put("order_code",orderAddressInfo.get("order_code").toString());
					}
					
					insertDatamap.put("return_status", "0");
					insertDatamap.put("return_msg", "重复订阅快递100");
					DbUp.upTable("oc_express_loginfo").dataInsert(insertDatamap);
					dt.update("update oc_order_shipments set send_remark = :send_remark,send_count = send_count + 1 where zid = :zid", paramMap);
					//DbUp.upTable("oc_order_shipments").dataExec("update oc_order_shipments set send_remark = :send_remark,send_count = send_count + 1 where zid = :zid",null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 向快递100 发数据
	 * 
	 * @param map
	 * @return
	 * @throws JsonProcessingException
	 */
	public String doSend(Map<String, Object> map) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, String> parameters = new HashMap<String, String>();
		jsonMap.put("company", map.get("company"));
		jsonMap.put("number", map.get("number"));// 快递编号
		jsonMap.put("to", map.get("to"));// kZTYgQsC6194
		jsonMap.put("key", "RXcJgYVX6346");
		parameters.put("callbackurl",bConfig("ordercenter.quaidi100_callbackurl"));
		parameters.put("salt", bConfig("ordercenter.quaidi100_secure_key"));
		parameters.put("resultv2", "1");
		parameters.put("mobiletelephone", map.get("mobiletelephone") == null ? "" : map.get("mobiletelephone").toString());
		jsonMap.put("parameters", parameters);
		String param = objectMapper.writeValueAsString(jsonMap);
		MDataMap p = new MDataMap();
		p.put("schema", "json");
		p.put("param", param);
		try {
			return WebClientSupport.upPost("http://www.kuaidi100.com/poll", p);
		} catch (Exception e) {
			//System.out.println("访问快递100失败");
			MDataMap insertDatamap = new MDataMap();
			UUID uuid = UUID.randomUUID();
			insertDatamap.put("uid", uuid.toString().replace("-", ""));
			insertDatamap.put("order_code",map.get("order_code").toString());
			insertDatamap.put("return_status", "0");
			insertDatamap.put("return_msg", "无法访问快递100服务器");
			//result.inErrorMessage(939303002);//无法访问快递100
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析 快递100 返回结果
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public int isSendSuccess(String str) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(str, Map.class);
		if ("true".equals(map.get("result").toString())) {
			return 1;
		}
		//result.inErrorMessage(939303003);//快递100返回数据失败
		return 0;
	}
	//测试
	public static void main(String[] args) {
		PostOrderService ss=new PostOrderService();
		JobExecutionContext context = null;
		ss.doExecute(context);
	}
}
