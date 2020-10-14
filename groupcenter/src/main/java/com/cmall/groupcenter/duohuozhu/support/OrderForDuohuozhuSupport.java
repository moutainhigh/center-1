package com.cmall.groupcenter.duohuozhu.support;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.srnpr.xmassystem.duohuozhu.model.RequestModel;
import com.srnpr.xmassystem.duohuozhu.model.ResponseModel;
import com.srnpr.xmassystem.duohuozhu.support.RsyncDuohuozhuSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class OrderForDuohuozhuSupport {

	/**
	 * 取消订单
	 * @param sInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MWebResult cancelOrder(String sInfo) {
		MWebResult result = new MWebResult();
		
		MDataMap orderDuohz = DbUp.upTable("oc_order_duohz").one("order_code",sInfo);
		if(orderDuohz == null || orderDuohz.get("cod_status").equals("-1") || orderDuohz.get("cod_status").equals("0") ) {
			return result;
		}
		
		List<MDataMap> detailList = DbUp.upTable("oc_order_duohz_detail").queryByWhere("order_code",sInfo);
		
		List<LinkedHashMap<String,Object>> objList = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> obj,cancelorderMap;
		
		for(MDataMap sku : detailList) {
			obj = new LinkedHashMap<String, Object>();
			obj.put("cp_ord_seq", sku.get("seq"));
			objList.add(obj);
		}
		
		cancelorderMap = new LinkedHashMap<String, Object>(); 
		cancelorderMap.put("cp_ord_id", sInfo);
		cancelorderMap.put("cancel_reason", "取消");
		cancelorderMap.put("detail", objList);
		
		LinkedHashMap<String,Object> bodyMap = new LinkedHashMap<String, Object>(); 
		bodyMap.put("cancelorder", Arrays.asList(cancelorderMap));
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000003");
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
		
		List<Map<String, Object>> cancelorderList = (List<Map<String, Object>>)respModel.getBody().get("cancelorder");
		if(cancelorderList != null && !cancelorderList.isEmpty()) {
			for(Map<String, Object> map : cancelorderList) {
				if(!"00".equals(map.get("err_code"))) {
					result.setResultCode(0);
					result.setResultMessage((String)map.get("err_msg"));
				}
			}
		}
		
		if(!result.upFlagTrue()) {
			return result;
		}
		
		orderDuohz.put("cod_status", "-1");
		orderDuohz.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("oc_order_duohz").dataUpdate(orderDuohz, "cod_status,update_time", "zid");
		return result;
	}
	
	/**
	 * 取消退货
	 * @param sInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MWebResult cancelReturnGoods(String afterSaleCode) {
		MWebResult result = new MWebResult();
		MDataMap orderDuohz = DbUp.upTable("oc_order_duohz_after").one("asale_code",afterSaleCode);
		if(orderDuohz == null || "-1".equalsIgnoreCase(orderDuohz.get("cod_status"))) {
			return result;
		}
		
		if(StringUtils.isBlank(orderDuohz.get("cod_status")) || "0".equals(orderDuohz.get("cod_status"))) {
			orderDuohz.put("cod_status", "-1");
			DbUp.upTable("oc_order_duohz_after").dataUpdate(orderDuohz, "cod_status", "zid");
			return result;
		}
		
		List<MDataMap> mapList = DbUp.upTable("oc_order_duohz_after_detail").queryByWhere("asale_code",afterSaleCode);
		
		List<LinkedHashMap<String,Object>> objList = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> obj,cancelorderMap;
		
		for(MDataMap sku : mapList) {
			obj = new LinkedHashMap<String, Object>();
			obj.put("cp_ord_seq", sku.get("seq"));
			objList.add(obj);
		}
		
		cancelorderMap = new LinkedHashMap<String, Object>(); 
		cancelorderMap.put("cp_rtn_id", afterSaleCode);
		cancelorderMap.put("cancel_reason", "取消");
		cancelorderMap.put("detail", objList);
		
		LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("cancelrtnorder", Arrays.asList(cancelorderMap));
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000005");
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
		
		List<Map<String, Object>> cancelorderList = (List<Map<String, Object>>)respModel.getBody().get("cancelrtnorder");
		if(cancelorderList != null && !cancelorderList.isEmpty()) {
			for(Map<String, Object> map : cancelorderList) {
				if(!"00".equals(map.get("err_code"))) {
					result.setResultCode(0);
					result.setResultMessage((String)map.get("err_msg"));
				}
			}
		}
		
		if(!result.upFlagTrue()) {
			return result;
		}
		
		orderDuohz.put("cod_status", "-1");
		orderDuohz.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("oc_order_duohz_after").dataUpdate(orderDuohz, "cod_status,update_time", "zid");
		return result;
	}
	
	/**
	 * 取消换货
	 * @param sInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MWebResult cancelExchangeGoods(String afterSaleCode) {
		MWebResult result = new MWebResult();
		MDataMap orderDuohz = DbUp.upTable("oc_order_duohz_after").one("asale_code",afterSaleCode);
		if(orderDuohz == null || "-1".equalsIgnoreCase(orderDuohz.get("cod_status"))) {
			return result;
		}
		
		if(StringUtils.isBlank(orderDuohz.get("cod_status")) || "0".equals(orderDuohz.get("cod_status"))) {
			orderDuohz.put("cod_status", "-1");
			DbUp.upTable("oc_order_duohz_after").dataUpdate(orderDuohz, "cod_status", "zid");
			return result;
		}
		
		List<MDataMap> mapList = DbUp.upTable("oc_order_duohz_after_detail").queryByWhere("asale_code",afterSaleCode);
		
		List<LinkedHashMap<String,Object>> objList = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> obj,cancelorderMap;
		
		for(MDataMap sku : mapList) {
			obj = new LinkedHashMap<String, Object>();
			obj.put("cp_ord_seq", sku.get("seq"));
			objList.add(obj);
		}
		
		cancelorderMap = new LinkedHashMap<String, Object>(); 
		cancelorderMap.put("cp_ord_id", orderDuohz.get("order_code"));
		cancelorderMap.put("cp_rtn_id", afterSaleCode);
		cancelorderMap.put("cp_new_ord_id", afterSaleCode);
		cancelorderMap.put("cancel_reason", "取消");
		cancelorderMap.put("detail", objList);
		
		LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("cancelchangeorder", Arrays.asList(cancelorderMap));
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000007");
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
		
		List<Map<String, Object>> cancelorderList = (List<Map<String, Object>>)respModel.getBody().get("cancelchangeorder");
		if(cancelorderList != null && !cancelorderList.isEmpty()) {
			for(Map<String, Object> map : cancelorderList) {
				if(!"00".equals(map.get("err_code"))) {
					result.setResultCode(0);
					result.setResultMessage((String)map.get("err_msg"));
				}
			}
		}
		
		if(!result.upFlagTrue()) {
			return result;
		}
		
		orderDuohz.put("cod_status", "-1");
		orderDuohz.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("oc_order_duohz_after").dataUpdate(orderDuohz, "cod_status,update_time", "zid");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<MDataMap> getOrderTrackList(String code) {
		List<MDataMap> list = new ArrayList<MDataMap>();
		
		LinkedHashMap<String,Object> obj = new LinkedHashMap<String, Object>();
		obj.put("cp_ord_id", code);
		
		LinkedHashMap<String,Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("step", Arrays.asList(obj));
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000009");
		reqModel.setBody(bodyMap);
		
		ResponseModel respModel = new RsyncDuohuozhuSupport().callGateway(reqModel);
		
		if(respModel == null) {
			return list;
		}
		
		if(!"00".equals(respModel.getHeader().getResp_code())) {
			return list;
		}
		
		List<Map<String, Object>> stepList = (List<Map<String, Object>> )respModel.getBody().get("step");
		if(stepList == null || stepList.isEmpty()) {
			return list;
		}
		
		MDataMap mDataMap = new MDataMap();
		String ordId,invcId,dlverName;
		String content,time;
		Date date;
		List<Map<String, Object>> detailList;
		for(Map<String, Object> step : stepList) {
			if(!"00".equals(step.get("err_code"))) {
				continue;
			}
			ordId = step.get("cp_ord_id")+"";
			invcId = StringUtils.trimToEmpty((String)step.get("invc_id"));
			dlverName = StringUtils.trimToEmpty((String)step.get("dlver_nm"));
			detailList = (List<Map<String, Object>>)step.get("detail");
			
			if(StringUtils.isBlank(invcId)) {
				continue;
			}
			
			if(!ordId.equals(code)) {
				continue;
			}
			
			if(detailList != null) {
				// 配送轨迹明细
				for(Map<String, Object> m : detailList) {
					mDataMap = new MDataMap();
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
					
					mDataMap.put("time", time);
					mDataMap.put("context", content);
					mDataMap.put("dlverName", dlverName);
					mDataMap.put("invcId", invcId);
					list.add(mDataMap);
				}				
			}
		}		
		return list;
	}
}
