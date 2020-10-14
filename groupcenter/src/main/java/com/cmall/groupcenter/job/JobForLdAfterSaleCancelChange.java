package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.util.HttpUtil;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 异步换货取消申请推送LD系统
 * @author cc
 *
 */
public class JobForLdAfterSaleCancelChange extends RootJob  {	
	@Override
	public void doExecute(JobExecutionContext context) {
		/**
		 * 取消中的订单推送
		 * 售后单状态为取消中状态，推送状态为未推送 2
		 */
		
		String sql = "SELECT * FROM ordercenter.oc_after_sale_ld WHERE after_sale_status = '06' and if_post = '2' and after_sale_type = '2' LIMIT 0,2000";
		List<Map<String, Object>> list = DbUp.upTable("oc_after_sale_ld").dataSqlList(sql, null);
		List<String> errList = new ArrayList<String>();
		List<Map<String, Object>> postParams = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> postMap = new HashMap<String, Object>();
			postMap.put("saleCodeApp", map.get("after_sale_code_app"));
			postMap.put("ordId", Integer.parseInt(map.get("order_code").toString()));
			postMap.put("ordSeq", Integer.parseInt(map.get("order_seq") != null ? map.get("order_seq").toString() : "0"));
			postMap.put("goodId", Integer.parseInt(map.get("product_code").toString()));
			postMap.put("rtnReason", map.get("reason")!=null?map.get("reason").toString():"");
			postMap.put("rtnDesc", map.get("remark")!=null?map.get("remark").toString():"");
			postMap.put("picUrl", map.get("after_image")!=null?map.get("after_image").toString():"");
			String sku_code = map.get("sku_code") != null ? map.get("sku_code").toString() : "";
			try {
				if (sku_code != null) {
					MDataMap skuDetail = DbUp.upTable("pc_skuinfo").one("sku_code", sku_code);
					if (skuDetail != null) {
						String skuColor = skuDetail.get("sku_key");
						String[] colors = skuColor.split("&");
						Integer colorId = Integer.parseInt(colors[0].replace("color_id=", ""));
						Integer styleId = Integer.parseInt(colors[1].replace("style_id=", ""));
						postMap.put("colorId", colorId);
						postMap.put("styleId", styleId);
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
			String ordSeqStr = map.get("order_seq") != null ? map.get("order_seq").toString() : "0";
			if ("0".equals(ordSeqStr) && postMap.get("colorId") == null) {
				errList.add(map.get("order_code").toString());
				continue;
			}
			postMap.put("doType", "04");
			postMap.put("goodCnt", map.get("good_cnt"));
			postParams.add(postMap);
		}
		if(postParams.size() == 0){
			return;
		}
		String url = bConfig("groupcenter.rsync_homehas_url")+"syncAfterService";
		Date requestDate = new Date();
		String result = HttpUtil.post(url, JSONArray.toJSONString(postParams), "UTF-8");
		Date responseDate = new Date();
		JSONObject jo = JSONObject.parseObject(result);
		// 记录日志开始
		MDataMap log = new MDataMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.put("push_param", JSONArray.toJSONString(postParams));
		log.put("push_time", sdf.format(requestDate));
		log.put("result_param", result);
		log.put("result_time", sdf.format(responseDate));
		log.put("http_url", url);
		log.put("uid", UUID.randomUUID().toString().replace("-", "").toLowerCase());
		String code = jo.getString("code");
		log.put("code", code!=null?code:"500");
		DbUp.upTable("lc_ld_aftersale_http_log").dataInsert(log);
		// 记录日志结束
		if (!"0".equals(code)) {
			return;
		}
		// 处理推送数据回写
		String resultStr = jo.getString("result");
		if (StringUtils.isEmpty(resultStr)) {// 返回结果为空后续不处理
			return;
		}
		JSONArray ja = JSONArray.parseArray(resultStr);
		Iterator it = ja.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			String saleCodeApp = jsonObject.getString("saleCodeApp") != null ? jsonObject.getString("saleCodeApp") : "";
			String saleCodeLd = jsonObject.getString("saleCodeLd") != null ? jsonObject.getString("saleCodeLd") : "";
			String afterSaleCd = jsonObject.getString("afterSaleCd") !=null? jsonObject.getString("afterSaleCd"):"";
			String subCode = jsonObject.getString("subCode");
			MDataMap afterSaleMap = new MDataMap();
			afterSaleMap.put("after_sale_code_app", saleCodeApp);
			if(!StringUtils.isEmpty(saleCodeLd)){
				afterSaleMap.put("after_sale_code_ld", saleCodeLd);
			}
			afterSaleMap.put("if_post", "1");// 设置成推送
			afterSaleMap.put("modif_time", DateUtil.getSysDateTimeString());
			if(!StringUtils.isEmpty(afterSaleCd)){
				afterSaleMap.put("after_sale_status", afterSaleCd);
			}
			if ("0".equals(subCode)) {// 此条数据处理成功
				DbUp.upTable("oc_after_sale_ld").dataUpdate(afterSaleMap, "", "after_sale_code_app");
			} else {
				afterSaleMap.put("after_sale_status", "00");// 设置成为异常单，并且不再推送
				DbUp.upTable("oc_after_sale_ld").dataUpdate(afterSaleMap, "", "after_sale_code_app");
			}
		}		
	}
}
