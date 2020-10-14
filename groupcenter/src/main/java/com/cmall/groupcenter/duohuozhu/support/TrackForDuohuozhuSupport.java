package com.cmall.groupcenter.duohuozhu.support;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.srnpr.xmassystem.duohuozhu.model.RequestModel;
import com.srnpr.xmassystem.duohuozhu.model.ResponseModel;
import com.srnpr.xmassystem.duohuozhu.support.RsyncDuohuozhuSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

public class TrackForDuohuozhuSupport {

	/**
	 * 同步订单的配送轨迹
	 * @param orderCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RootResult rsyncOrderTrack(String orderCode) {
		RootResult result = new RootResult();
		
		LinkedHashMap<String,Object> obj = new LinkedHashMap<String, Object>();
		obj.put("cp_ord_id", orderCode);
		
		LinkedHashMap<String,Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("step", Arrays.asList(obj));
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000009");
		reqModel.setBody(bodyMap);
		
		ResponseModel respModel = new RsyncDuohuozhuSupport().callGateway(reqModel);
		
		if(respModel == null) {
			result.setResultCode(0);
			result.setResultMessage("接口调用异常");
			return result;
		}
		
		if(!"00".equals(respModel.getHeader().getResp_code())) {
			result.setResultCode(0);
			result.setResultMessage(respModel.getHeader().getResp_msg());
			return result;
		}
		
		List<Map<String, Object>> stepList = (List<Map<String, Object>> )respModel.getBody().get("step");
		if(stepList == null || stepList.isEmpty()) {
			return result;
		}
		
		String ordId,invcId;
		String content,time;
		Date date;
		List<Map<String, Object>> detailList;
		for(Map<String, Object> step : stepList) {
			if(!"00".equals(step.get("err_code"))) {
				continue;
			}
			ordId = step.get("cp_ord_id")+"";
			invcId = StringUtils.trimToEmpty((String)step.get("invc_id"));
			detailList = (List<Map<String, Object>>)step.get("detail");
			
			if(StringUtils.isBlank(invcId)) {
				continue;
			}
			
			// 保存快递单号
			if(DbUp.upTable("oc_order_shipments").count("order_code",ordId) == 0) {
				DbUp.upTable("oc_order_shipments").dataInsert(new MDataMap(
							"order_code", ordId,
							"logisticse_name", StringUtils.trimToEmpty((String)step.get("dlver_nm")),
							"waybill", invcId,
							"creator", "system",
							"create_time", FormatHelper.upDateTime(),
							"is_send100_flag", "1"
						));
			}
			
			if(detailList != null) {
				// 配送轨迹明细
				for(Map<String, Object> m : detailList) {
					content = StringUtils.trimToEmpty((String)m.get("step_address"));
					time = StringUtils.trimToEmpty((String)m.get("step_time"));
					
					// 转一下日志格式
					try {
						date = DateUtils.parseDate(time, "yyyyMMddHHmmss");
						time = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					
					// 不存在则保存
					if(DbUp.upTable("oc_express_detail").count("order_code",ordId,"time",time,"context", content) == 0) {
						String sSql = "insert into oc_express_detail(order_code,waybill,time,context) values(:order_code,:waybill,:time,:context)";
						DbUp.upTable("oc_express_detail").dataExec(sSql,new MDataMap(
								"order_code",ordId,
								"logisticse_code","",
								"waybill",invcId,
								"time",time,
								"context",content
								));
					}
				}				
			}
		}
		
		return result;
	}
}
