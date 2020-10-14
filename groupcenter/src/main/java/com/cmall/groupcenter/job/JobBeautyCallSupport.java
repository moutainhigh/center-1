package com.cmall.groupcenter.job;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.func.JaxbUtil;
import com.cmall.groupcenter.func.OrderInformation;
import com.cmall.groupcenter.func.OrderInformationService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * @author shiyz
 * 
 */
public class JobBeautyCallSupport extends RootJob {

	public void doExecute(JobExecutionContext context) {

		/**
		 * 调用E店宝API-导入订单信息
		 * @throws Exception
		 */

		String orderListXML = "";

		String flag = "0";

		String sCode = "";// WebHelper.upCode("CLOG");

		LinkedHashMap<String, String> apiparamsMap = new LinkedHashMap<String, String>();

		apiparamsMap.put("dbhost", bConfig("groupcenter.dbhost"));// 添加请求参数——主帐号

		apiparamsMap.put("appkey", bConfig("groupcenter.appkey"));// 添加请求参数——appkey

		apiparamsMap.put("method", "edbTradeAdd");// 添加请求参数——接口名称

		apiparamsMap.put("format", bConfig("groupcenter.format"));// 添加请求参数——返回格式

		apiparamsMap.put("fields", bConfig("groupcenter.fields"));

		apiparamsMap.put("v", bConfig("groupcenter.v"));// 添加请求参数——版本号（目前只提供2.0版本）

		//apiparamsMap.put("sign", new JaxbUtil().sing(bConfig("newscenter.shop_id")).get(0));// 参数sign

		//apiparamsMap.put("timestamp", new JaxbUtil().sing(bConfig("newscenter.shop_id")).get(1));// 添加请求参数——时间戳

		apiparamsMap.put("slencry", bConfig("groupcenter.slencry"));// 添加请求参数——返回结果是否加密（0，为不加密
																	// ，1.加密）

		apiparamsMap.put("ip", bConfig("groupcenter.ip"));// 添加请求参数——IP地址

		apiparamsMap.put("appscret", bConfig("groupcenter.secret"));// 添加请求参数——appscret

		apiparamsMap.put("token", bConfig("groupcenter.token"));// 添加请求参数——token

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String timestamp = sdf.format(new Date());

		String sCallString;

		try {

			OrderInformationService orderinfo = new OrderInformationService();

			List<OrderInformation> order = orderinfo.rsyncOrder(bConfig("newscenter.shop_id"));

			//List<Map> list = new ArrayList<Map>();

			for (int i = 0; i < order.size(); i++) {

			Map<String, Object> xml = beanMap(order.get(i));
				
				//list.add(xml);

			orderListXML = JaxbUtil.maptoXml(xml);

			//apiparamsMap.put("xmlValues", orderListXML);
				
			apiparamsMap.put("xmlValues", orderListXML);

			StringBuilder param = new StringBuilder();

			for (Iterator<Map.Entry<String, String>> it = apiparamsMap
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> e = it.next();
				if (e.getKey() != "appscret" && e.getKey() != "token") {
					if (e.getKey() == "xmlValues") {
						try {
							param.append("&").append(e.getKey()).append("=")
									.append(JaxbUtil.encodeUri(e.getValue()));
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						param.append("&").append(e.getKey()).append("=")
								.append(e.getValue());
					}
				}
			}
			String PostData = "";

			PostData = param.toString().substring(1);

			sCallString = JaxbUtil.getResult(bConfig("groupcenter.testUrl"),
					PostData);

			bLogDebug(0, sCallString);

			if (sCallString.contains("订单导入成功")) {

				flag = "0";

			} else {

				flag = "1";

			}

			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "request_data", orderListXML,
					"rsync_url", bConfig("groupcenter.testUrl"),
					"response_data", sCallString, "flag_success", flag,
					"response_time", FormatHelper.upDateTime(), "request_time",
					timestamp,"request_ordercode",order.get(i).getOut_tid());

			DbUp.upTable("lc_betabeauty_log").dataInsert(mInsertMap);
			
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static Map<String, Object> bean2Map(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj != null) {
			Class c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			for (Field field : fs) {
				String fieldName = field.getName();
				String getname = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				Object value = c.getMethod(getname, null).invoke(obj, null);

				if (value instanceof Collection) {
					List<Object> list = new ArrayList<Object>();
					for (Object o : (Collection) value) {
						list.add(bean2Map(o));
					}
					value = list;
				}

				map.put(fieldName, value);
			}
		}
		return map;
	}
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	private static Map<String, Object> beanMap(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj != null) {
			Class c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			for (Field field : fs) {
				String fieldName = field.getName();
				String getname = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				Object value = c.getMethod(getname, null).invoke(obj, null);

				map.put(fieldName, value);
			}
		}
		return map;
	}
	
	
	public static void main(String[] args) {

		JobBeautyCallSupport jobOrderForBeauty = new JobBeautyCallSupport();

		jobOrderForBeauty.doExecute(null);
	}

}
