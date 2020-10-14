package com.cmall.ordercenter.alipay.process;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cmall.ordercenter.model.PayResult;
import com.cmall.ordercenter.service.AlipayProcessService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
/**
 * 支付宝接口
 * @author Administrator
 *
 */
public class AlipayProcess extends BaseClass {

	public static final String OC_ORDERINFO = "oc_orderinfo";//主表
	public static final String OC_PAYMENT = "oc_payment";//支付宝接口表名
	/**
	 * 调用支付宝接口
	 * @param sOrderCode
	 * @return
	 */
	public String upSubmitForm(String sOrderCode)
	{
		PayResult payResult = new PayResult();
		String requestForm = "";
		//判断订单号是否为空  
		if(!"".equals(sOrderCode) && sOrderCode != null){
			AlipayProcessService alipayProcessService = new AlipayProcessService();
			//requestForm = alipayProcessService.createForm(sOrderCode);
		}else{
			payResult.inErrorMessage(939301200);
		}
		return requestForm;
	}
	
	/**
	 * 
	 * @param out_trade_no
	 * @param trade_no
	 * @param trade_status
	 * @param notify_time
	 * @param notify_type
	 * @param notify_id
	 * @param buyer_email
	 * @param seller_email
	 * @param sign_type
	 * @param sign
	 * @param total_fee
	 * @param mark
	 * @param gmt_payment
	 * @param out_channel_inst
	 */
	
	public void resultForm(String mark){
//		WebSessionHelper webSession=new WebSessionHelper();
//		webSession.upRequest("");
		
		
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		
		Map mMaps=request.getParameterMap();
		
		
		StringBuffer str = new StringBuffer();
		String endStr = "";
		MDataMap insertDatamap = new MDataMap();
		List<String> list = new ArrayList<String>();
		
		for(Object oKey: mMaps.keySet())
		{
			insertDatamap.put(oKey.toString(), request.getParameter(oKey.toString()));
			insertDatamap.put("mark", mark);
			
			if(!"sign_type".equals(oKey.toString()) && !"sign".equals(oKey.toString())){
				list.add(oKey.toString()+"="+request.getParameter(oKey.toString()));
			}
			
		}
		 Collections.sort(list);   //对List内容进行排序
		 
		 for(String nameString : list){
				str.append(nameString+"&");
		 }
		
		endStr = str.substring(0, str.toString().length()-1);
		
		AlipayProcessService alipayProcessService = new AlipayProcessService();
		alipayProcessService.resultValue(insertDatamap,endStr);
	}
	/**
	 * 和包回调结果
	 * @param merchantId
	 * @param payNo
	 * @param returnCode
	 * @param message
	 * @param signType
	 * @param type
	 * @param version
	 * @param amount
	 * @param amtItem
	 * @param bankAbbr
	 * @param mobile
	 * @param orderId
	 * @param payDate
	 * @param accountDate
	 * @param reserved1
	 * @param reserved2
	 * @param status
	 * @param orderDate
	 * @param fee
	 * @param serverCert
	 * @param hmac
	 * @return
	 */
	public String unionpayResultSuccess(){
		
		String result = "";
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		Map map = request.getParameterMap();
		
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.putAll(map);
//		MDataMap insertDatamap = new MDataMap();
//		insertDatamap.put("merchantId", merchantId);
//		insertDatamap.put("payNo", payNo);
//		insertDatamap.put("returnCode", returnCode);
//		insertDatamap.put("message", message);
//		insertDatamap.put("signType", signType);
//		insertDatamap.put("type", type);
//		insertDatamap.put("version", version);
//		insertDatamap.put("amount", amount);
//		insertDatamap.put("amtItem", amtItem);
//		insertDatamap.put("bankAbbr", bankAbbr);
//		insertDatamap.put("mobile", mobile);
//		insertDatamap.put("orderId", orderId);
//		insertDatamap.put("payDate", payDate);
//		insertDatamap.put("accountDate", accountDate);
//		insertDatamap.put("reserved1", reserved1);
//		insertDatamap.put("reserved2", reserved2);
//		insertDatamap.put("status", status);
//		insertDatamap.put("orderDate", orderDate);
//		insertDatamap.put("fee", fee);
//		insertDatamap.put("serverCert", serverCert);
//		insertDatamap.put("hmac", hmac);
		
		AlipayProcessService alipayProcessService = new AlipayProcessService();
		result = alipayProcessService.unionpayResultValue(insertDatamap);
		
		return result;
	}
	
}
