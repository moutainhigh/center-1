package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;



import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.util.DateTimeUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 安沃广告
 * 
 * @author wz
 * 
 */
public class JobSendAnWo extends RootJob {

	public void doExecute(JobExecutionContext context) {

		try {
			//System.out.println("===================anwohuidiaorukou========================");
			String url = "";
			String returnValue = "";
			// 当前时间减一小时
			String minusCreateTime = DateTimeUtil.minusDate(1);
			WebClientSupport webClient = new WebClientSupport();
			// 查询安沃传入的未匹配数据
			List<MDataMap> listInput = DbUp.upTable("lc_input_ad").queryAll("","", "", new MDataMap("status", "0"));
			// 用户设备信息(大于小于当前时间的所有数据)
			List<Map<String, Object>> listClient = DbUp.upTable("lc_client_info").dataSqlList(
					"select * from lc_client_info " + "where create_time > '"
							+ minusCreateTime + "' order by create_time desc",
					new MDataMap());
			
			for (MDataMap mapInput : listInput) {
				String adalias = mapInput.get("adalias");
				String mac = mapInput.get("mac");
				String idfa = mapInput.get("idfa");
				String create_time = mapInput.get("create_time"); // 获取安沃传入数据时间
				
				// 一条匹配信息如果匹配出多条,调用一次安沃回调接口(参数取时间最靠近的那一条)
				for (Map<String, Object> mapClient : listClient) {
					if (idfa.equals(String.valueOf(mapClient.get("idfa")))) {
						String uniqid = String.valueOf(mapClient.get("uniqid"));
						
						url = "http://offer.adwo.com/iofferwallcharge/ia?"
								+ "adalias=" + adalias + "" + 
								"&uid="+ String.valueOf(mapClient.get("mac")) + "" + 
								"&idfa=" + idfa + ""; 
								//"&acts=" + String.valueOf(mapClient.get("create_time")).substring(0, 10)
								 // 创建时间(自认为激活时间)

						// 调用安沃回调接口
						Map mapResponse = (Map) JSONObject.parse(webClient.doGet(url));
						if(mapResponse!=null && !"".equals(mapResponse) && mapResponse.size()>0){
							returnValue = mapResponse.toString();
						}
						
						// 记录安沃回调接口日志
						DbUp.upTable("lc_response_ad").dataInsert(new MDataMap("uniqid", uniqid, "idfa", idfa,
										"adalias", adalias, "create_time",create_time, "response_information",returnValue));
						
						// 200为调用安沃回调接口返回的成功信息
						if ("true".equals(String.valueOf(mapResponse.get("success")))) {
						//	System.out.println("===================anwohuidiao  zuihoudezuihou========================");
							MDataMap updateData = new MDataMap();
							updateData.put("idfa", mapInput.get("idfa"));
							updateData.put("status", "1");

							// 安沃传入数据更新至已匹配状态(会把多个重读的uniqid都更新为已匹配状态)
							DbUp.upTable("lc_input_ad").dataUpdate(updateData,"status", "idfa");

							break;
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
