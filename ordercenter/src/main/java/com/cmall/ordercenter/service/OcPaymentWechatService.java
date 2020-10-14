package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 微信支付回调记录表
 * @author wz
 *
 */
public class OcPaymentWechatService  extends BaseClass{
	/**
	 * 
	 * @param request
	 * @param sPayCode
	 * @param mark  
	 */
	public void insertOcPaymentWechat(HttpServletRequest request,String sPayCode,String mark){
		StringBuffer str = new StringBuffer();
		String endStr = "";
		List<String> list = new ArrayList<String>();
		
		Map mMaps=request.getParameterMap();
		for(Object oKey: mMaps.keySet())
		{
			if(!"sign_type".equals(oKey.toString()) && !"sign".equals(oKey.toString())){
				list.add(oKey.toString()+"="+request.getParameter(oKey.toString()));
			}
			
		}
		 Collections.sort(list);   //对List内容进行排序
		 
		 for(String nameString : list){
				str.append(nameString+"&");
		 }
		 
		 /*
		  * //由于微信以分为单位，库中以元为单位，在此把分转换成元
		  * BigDecimal中setScale(x,y)   x是控制小数位数的
		  */
		 
		String dueMoney = String.valueOf(((new BigDecimal(request.getParameter("total_fee"))).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
		endStr = str.substring(0, str.toString().length()-1);
		
		MDataMap insertDataMapNew = new MDataMap();
		insertDataMapNew.put("sign", request.getParameter("sign"));
		insertDataMapNew.put("trade_mode", request.getParameter("trade_mode"));
		insertDataMapNew.put("trade_state", request.getParameter("trade_state"));
		insertDataMapNew.put("partner", request.getParameter("partner"));
		insertDataMapNew.put("total_fee", dueMoney);   
		insertDataMapNew.put("fee_type", request.getParameter("fee_type"));
		insertDataMapNew.put("notify_id", request.getParameter("notify_id"));
		insertDataMapNew.put("transaction_id", request.getParameter("transaction_id"));
		insertDataMapNew.put("out_trade_no", request.getParameter("out_trade_no"));
		insertDataMapNew.put("time_end", request.getParameter("time_end"));
		insertDataMapNew.put("bank_type", "WX");
		
		insertDataMapNew.put("payment_code", sPayCode);
		insertDataMapNew.put("param_value", endStr);
		insertDataMapNew.put("mark", mark);
		
		
		
		//insertDataMapNew.put("sign_type", request.getParameter("sign_type"));
//		insertDataMapNew.put("pay_info", request.getParameter("pay_info"));
//		insertDataMapNew.put("bank_billno", request.getParameter("bank_billno"));
//		insertDataMapNew.put("transport_fee", request.getParameter("transport_fee"));
//		insertDataMapNew.put("product_fee", request.getParameter("product_fee"));
//		insertDataMapNew.put("discount", request.getParameter("discount"));
//		insertDataMapNew.put("buyer_alias", request.getParameter("buyer_alias"));
		
		if(insertDataMapNew!=null && !"".equals(insertDataMapNew) && insertDataMapNew.size()>0){
			DbUp.upTable("oc_payment_wechat").dataInsert(insertDataMapNew);
		}
		
	}
	
	
	
	/**
	 * 插入微信支付最新版本回调信息
	 * @param request
	 * @param sPayCode
	 * @param mark
	 */
	public void insertOcPaymentWechatNew(Map wechatMoveParamsMap,String sPayCode,String mark){
		StringBuffer str = new StringBuffer();
		String endStr = "";
		List<String> list = new ArrayList<String>();
		
		for(Object oKey: wechatMoveParamsMap.keySet())
		{
				list.add(oKey.toString()+"="+wechatMoveParamsMap.get(oKey.toString()));
		}
		 Collections.sort(list);   //对List内容进行排序
		 
		 for(String nameString : list){
				str.append(nameString+"&");
		 }
		 
		 
		 
		 /*
		  * //由于微信以分为单位，库中以元为单位，在此把分转换成元
		  * BigDecimal中setScale(x,y)   x是控制小数位数的
		  */
		String dueMoney = String.valueOf(((new BigDecimal(String.valueOf(wechatMoveParamsMap.get("total_fee")))).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
		
		endStr = str.substring(0, str.toString().length()-1);
		
		MDataMap insertDataMapNew = new MDataMap();
		insertDataMapNew.put("appid", String.valueOf(wechatMoveParamsMap.get("appid")));
		insertDataMapNew.put("mch_id", String.valueOf(wechatMoveParamsMap.get("mch_id")));
		insertDataMapNew.put("out_trade_no", String.valueOf(wechatMoveParamsMap.get("out_trade_no")));
		insertDataMapNew.put("transaction_id", String.valueOf(wechatMoveParamsMap.get("transaction_id")));
		insertDataMapNew.put("time_end", String.valueOf(wechatMoveParamsMap.get("time_end")));
		insertDataMapNew.put("sign", String.valueOf(wechatMoveParamsMap.get("sign")));
		insertDataMapNew.put("trade_type", String.valueOf(wechatMoveParamsMap.get("trade_type")));
		insertDataMapNew.put("bank_type", String.valueOf(wechatMoveParamsMap.get("bank_type")));
		insertDataMapNew.put("total_fee", dueMoney);
		insertDataMapNew.put("result_code", String.valueOf(wechatMoveParamsMap.get("result_code")));
		insertDataMapNew.put("process_time", "");
		insertDataMapNew.put("process_result", "");
		insertDataMapNew.put("param_value", endStr);
		insertDataMapNew.put("payment_code", sPayCode);
		insertDataMapNew.put("mark", mark);
		//insertDataMapNew.put("flag_success", request.getParameter("flag_success"));		
		
		
		if(insertDataMapNew!=null && !"".equals(insertDataMapNew) && insertDataMapNew.size()>0){
			DbUp.upTable("oc_payment_wechatNew").dataInsert(insertDataMapNew);
		}
		
	}
}
