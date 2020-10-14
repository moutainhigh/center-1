package com.cmall.ordercenter.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;
import com.cmall.ordercenter.alipay.sign.RSA;
import com.cmall.ordercenter.alipay.util.AlipaySubmit;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.ApiAlipayMoveProcessOrderResult;
import com.srnpr.xmassystem.support.PlusSupportPay;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 支付宝移动支付
 * 
 * @author wz
 * 
 */
public class ApiAlipayMoveProcessService extends BaseClass {
	public static final String OC_ORDERINFO = "oc_orderinfo";
	public static final String OC_PAYMENT = "oc_payment";// 支付宝接口表名
	public static final String HC_ORDER = "hc_order"; // 约TA 订单单表
	public static final String OC_UNIONPAY = "oc_Unionpay";// 和包回调信息日志表
	public static final String moveMark = "001";  //移动支付标示
	public static final String webMark = "002";  //移动支付标示

	public ApiAlipayMoveProcessOrderResult alipayMoveProcessRequest(
			String out_trade_no, String body, String subject) {
		ApiAlipayMoveProcessOrderResult result = new ApiAlipayMoveProcessOrderResult();
		String resultValue = null;
		Map<String, String> map = new HashMap<String, String>();
		String unionResultValue = "";

		if (!"".equals(out_trade_no) && out_trade_no != null) {
			MDataMap queryMap = new MDataMap();
			queryMap.put("order_code", out_trade_no);

			MDataMap unionQueryMap = new MDataMap();
			unionQueryMap.put("orderId", out_trade_no);

			// 查看支付宝支付接口日志
			List<MDataMap> paymentList = DbUp.upTable(OC_PAYMENT).queryAll("",
					"", "", queryMap);
			// 查询此订单是否调用过和包接口
			List<MDataMap> unionPaymentList = DbUp.upTable(OC_UNIONPAY)
					.queryAll("", "", "", unionQueryMap);

			for (MDataMap payment : paymentList) {
				if ("T".equals(payment.get("is_success"))) {
					result.setResultCode(941901095);
					result.setResultMessage(bInfo(941901095));
					return result;
				} else {
					resultValue = "可支付";
				}
			}

			for (MDataMap payment : unionPaymentList) {
				if ("SUCCESS".equals(payment.get("status"))) {
					result.setResultCode(941901095);
					result.setResultMessage(bInfo(941901095));
					return result;
				} else {
					unionResultValue = "可支付";
				}
			}

			if ((paymentList.isEmpty() && unionPaymentList.isEmpty())
					|| (paymentList.isEmpty() && "可支付".equals(unionResultValue))
					|| ("可支付".equals(resultValue) && unionPaymentList.isEmpty())
					|| ("可支付".equals(resultValue) && "可支付"
							.equals(unionResultValue))) {

				MDataMap queryHcOrder = new MDataMap();
				queryHcOrder.put("order_code", out_trade_no);

				// 通过订单号查询订单金额
				List<MDataMap> orderinfoList = DbUp.upTable(OC_ORDERINFO)
						.queryAll("", "", "", queryHcOrder);

				if (orderinfoList != null && !orderinfoList.isEmpty()) {
					Map<String, String>  alipayMoveConfigMap = new HashMap<String,String>();
					AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();   
					
					for (MDataMap orderin : orderinfoList) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
						// 生成要请求给支付宝的参数数组
						map = objectToMapValues(orderin, out_trade_no, body,
								subject,alipayMoveConfigMap,moveMark);

						result.setService(map.get("service"));
						result.setPartner(map.get("partner"));
						result.set_input_charset(map.get("_input_charset"));
						result.setSign_type(map.get("sign_type"));
						result.setSign(map.get("sign"));
						result.setNotify_url(map.get("notify_url"));
						result.setOut_trade_no(map.get("out_trade_no"));
						result.setSubject(map.get("subject"));
						result.setPayment_type(map.get("payment_type"));
						result.setSeller_id(map.get("seller_id"));
						result.setTotal_fee(map.get("total_fee"));
						result.setBody(map.get("body"));
					}
					return result;
				} else {
					result.setResultCode(941901096);
					result.setResultMessage(bInfo(941901096));
					return result;
				}
			}
		}
		return null;
	}

	/**
	 * 支付宝移动支付链接
	 * 
	 * @param orderCode
	 * @param flag  true: 以把支付类型放入缓存中
	 * @return
	 */
	public String alipayMoveParameter(String orderCode,boolean flag) {
		Map<String, String> map = new HashMap<String, String>();
		String strParam = null;
		String value = null;
		String sOut = "";
		//商品名称
		String productNameAll =  bConfig("ordercenter.PRODUCT_NAME");
		
		
		if (orderCode != null && !"".equals(orderCode)) {
			List<MDataMap> orderinfoList = new ArrayList<MDataMap>();
			Map<String, String>  alipayMoveConfigMap = new HashMap<String,String>();
			AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig(); 
			//大订单
			if("OS".equals(orderCode.substring(0, 2))){
				MDataMap bigOrder = new MDataMap();
				bigOrder.put("big_order_code", orderCode);
				bigOrder.put("delete_flag", "0");
				
				orderinfoList = DbUp.upTable("oc_orderinfo_upper").queryAll("", "", "", bigOrder);
				if (orderinfoList != null && !"".equals(orderinfoList) && !orderinfoList.isEmpty()) {
					for (MDataMap orderin : orderinfoList) {
						/*
						 * 目前只有惠家有  有订单拆分
						 */
						if("SI2003".equals(orderin.get("seller_code")) || "SI3003".equals(orderin.get("seller_code"))){   //判断是哪个项目
							alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
							
							if(alipayMoveConfigMap!=null && !"".equals(alipayMoveConfigMap) && alipayMoveConfigMap.size()>0){
								//获取所有小订单的商品名称
								//String productNameAll =  orderService.productNameAll(orderCode);
								
								map = objectToMapValues(orderin, orderin.get("big_order_code"),
										"", productNameAll,alipayMoveConfigMap,moveMark);
								
							}
						}
					}
					
					if(!flag){
						//把此订单的支付方式  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
						new PlusSupportPay().fixPayFrom(orderCode, "449746280003");   
					}
					
				}
				
			}else if("DD".equals(orderCode.substring(0, 2))){    //小订单
				MDataMap queryHcOrder = new MDataMap();
				queryHcOrder.put("order_code", orderCode);
				queryHcOrder.put("delete_flag", "0");
				
				// 通过订单号查询订单金额
				orderinfoList = DbUp.upTable(OC_ORDERINFO).queryAll("", "", "", queryHcOrder);
				
				if (orderinfoList != null && !"".equals(orderinfoList) && !orderinfoList.isEmpty()) {
					for (MDataMap orderin : orderinfoList) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
						
						if(alipayMoveConfigMap!=null && !"".equals(alipayMoveConfigMap) && alipayMoveConfigMap.size()>0){
//							map = objectToMapValues(orderin, orderin.get("order_code"),
//									"", orderin.get("product_name"),alipayMoveConfigMap,moveMark);
							
							map = objectToMapValues(orderin, orderin.get("order_code"),
									"", productNameAll,alipayMoveConfigMap,moveMark);
						}
					}
				}
			}
			
			
			
			String timeParam = null;
			
			if(map!=null && !"".equals(map) && map.size()>0){
				
				timeParam = getOrderCancelTime(orderCode,getCreateTime(orderCode));
				
				//为闪够订单
				if(StringUtils.isBlank(timeParam)){   
					
					
					return "";
					
				}
				/*
				 *以下计算往支付传的失效时间 ，都是基于order_Info_upper表的create_time进行计算的   
				 */
				if(VersionHelper.checkServerVersion("3.5.51.53")){
					
					strParam = "partner=\"" + map.get("partner") + 
							"\"&seller_id=\"" + map.get("seller_id") + 
							"\"&out_trade_no=\"" + map.get("out_trade_no") + 
							"\"&subject=\"" + map.get("subject") + 
							
							" \"&total_fee=\"" + map.get("total_fee") +     //  map.get("total_fee")
							"\"&notify_url=\"" + map.get("notify_url") + 
							"\"&service=\"mobile.securitypay.pay\"&payment_type=\"" + map.get("payment_type") + 
							//"\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"";
					//"\"&_input_charset=\"utf-8\"&it_b_pay=\"2m\"&show_url=\"m.alipay.com\"&success=\"true\"";
					"\"&_input_charset=\"utf-8\"&it_b_pay=\""+timeParam+"\"&show_url=\"m.alipay.com\"&success=\"true\"";
				}else{
					strParam = "partner=\"" + map.get("partner") + 
							"\"&seller_id=\"" + map.get("seller_id") + 
							"\"&out_trade_no=\"" + map.get("out_trade_no") + 
							"\"&subject=\"" + map.get("subject") + 
							
							" \"&total_fee=\"" + map.get("total_fee") +     //  map.get("total_fee")
							"\"&notify_url=\"" + map.get("notify_url") + 
							"\"&service=\"mobile.securitypay.pay\"&payment_type=\"" + map.get("payment_type") + 
							//"\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"";
					"\"&_input_charset=\"utf-8\"&it_b_pay=\""+timeParam+"\"&show_url=\"m.alipay.com\"&success=\"true\"";
					//"\"&_input_charset=\"utf-8\"&it_b_pay=\""+timeParam+"\"&show_url=\"m.alipay.com\"&success=\"true\"";
				}
				
				
				//strb.append("body=\"" + map.get("body") + "\"");
				value = RSA.sign(strParam,alipayMoveConfigMap.get("key") , "UTF-8");
				sOut = strParam + "&sign=\"" + URLEncoder.encode(value)
						+ "\"&sign_type=\"RSA\"";
				
			}
		}
		return sOut;
	} 
	
	/**
	 * 支付宝移动支付链接(最新)
	 * 
	 * @param orderCode
	 * @return
	 */
	public String alipayMoveParameterNew(String payCode) {
		Map<String, String> map = new HashMap<String, String>();
		String strParam = null;
		String value = null;
		String sOut = "";
		
		//商品名称
		String productNameAll =  bConfig("ordercenter.PRODUCT_NAME");
		
		
		if (payCode != null && !"".equals(payCode)) {
			List<MDataMap> orderinfoList = new ArrayList<MDataMap>();
			Map<String, String>  alipayMoveConfigMap = new HashMap<String,String>();
			AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();  
			
			//按照支付单号支付
			if("PP".equals(payCode.substring(0, 2))){    
				MDataMap queryHcOrder = new MDataMap();
				queryHcOrder.put("pay_code", payCode);
				queryHcOrder.put("state", "0");
				
				// 通过订单号查询订单金额
				orderinfoList = DbUp.upTable("oc_pay_info").queryAll("", "", "", queryHcOrder);
				
				if (orderinfoList != null && !"".equals(orderinfoList) && !orderinfoList.isEmpty()) {
					for (MDataMap orderin : orderinfoList) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
						
						if(alipayMoveConfigMap!=null && !"".equals(alipayMoveConfigMap) && alipayMoveConfigMap.size()>0){
//							map = objectToMapValues(orderin, orderin.get("order_code"),
//									"", orderin.get("product_name"),alipayMoveConfigMap,moveMark);
							
							map = objectToMapValues(orderin, orderin.get("pay_code"),
									"", productNameAll,alipayMoveConfigMap,moveMark);
						}
					}
				}
			}
			
			if(map!=null && !"".equals(map) && map.size()>0){
				strParam = "partner=\"" + map.get("partner") + 
						"\"&seller_id=\"" + map.get("seller_id") + 
						"\"&out_trade_no=\"" + map.get("out_trade_no") + 
						"\"&subject=\"" + map.get("subject") + 
						
						" \"&total_fee=\"" + map.get("total_fee") +     //  map.get("total_fee")
						"\"&notify_url=\"" + map.get("notify_url") + 
						"\"&service=\"mobile.securitypay.pay\"&payment_type=\"" + map.get("payment_type") + 
						//"\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"";
				"\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"&it_b_pay=\"2m\"";
				
				//strb.append("body=\"" + map.get("body") + "\"");
				value = RSA.sign(strParam,alipayMoveConfigMap.get("key") , "UTF-8");
				sOut = strParam + "&sign=\"" + URLEncoder.encode(value)
						+ "\"&sign_type=\"RSA\"";
				
				
				
			}
		}
		return sOut;
	} 
	
	/**
	 * 获取签名后的sign   
	 * @param orderCode
	 * @return
	 */
	public Map<String, String> alipaySign(String orderCode) {
		Map<String, String> map = new HashMap<String, String>();
		List<MDataMap> orderinfoList = new ArrayList<MDataMap>();
		
		Map<String, String>  alipayMoveConfigMap = new HashMap<String,String>();
		AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();   
		OrderService orderService = new OrderService();
		
		MDataMap queryHcOrder = new MDataMap();
		
		if (orderCode != null && !"".equals(orderCode)) {
			//区分大订单小订单
			if("OS".equals(orderCode.substring(0, 2))){
				queryHcOrder.put("big_order_code", orderCode);  
				// 通过订单号查询订单金额
				orderinfoList = DbUp.upTable("oc_orderinfo_upper").queryAll(
						"", "", "", queryHcOrder);
				
				if (orderinfoList != null && !orderinfoList.isEmpty()) {
					for (MDataMap orderin : orderinfoList) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
						//获取所有小订单的商品名称
						String productNameAll =  orderService.productNameAll(orderCode);
						
						map = objectToMapValues(orderin, orderin.get("big_order_code"),
								"", productNameAll,alipayMoveConfigMap,moveMark);
						return map;
					}
				}
			}else if("DD".equals(orderCode.substring(0, 2))){
				queryHcOrder.put("order_code", orderCode);   
				// 通过订单号查询订单金额
				orderinfoList = DbUp.upTable(OC_ORDERINFO).queryAll(
						"", "", "", queryHcOrder);
				
				if (orderinfoList != null && !orderinfoList.isEmpty()) {
					for (MDataMap orderin : orderinfoList) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(orderin.get("seller_code"));  //获取支付相关配置信息
						map = objectToMapValues(orderin, orderin.get("order_code"),
								"", orderin.get("product_name"),alipayMoveConfigMap,moveMark);
						return map;
					}
				}
			}
			
			
		}
		return null;
	}

	private Map<String, String> objectToMapValues(MDataMap orderin,
			String out_trade_no, String body, String subject,Map<String, String> alipayMoveConfigMap,String mark) {
		MDataMap map = new MDataMap();
		if("001".equals(mark)){   //移动支付
			map.put("service", bConfig("ordercenter.serviceMove"));
			map.put("notify_url",
					bConfig("ordercenter.alipay_serviceMove_response"));
		}else if("002".equals(mark)){   //网页支付
			map.put("service", bConfig("ordercenter.serviceWeb"));
			map.put("notify_url",
					bConfig("ordercenter.alipay_serviceWeb_response"));
		}
		map.put("partner", alipayMoveConfigMap.get("partner"));
		//map.put("partner", "2088501700700194");
		map.put("_input_charset", "UTF-8");

		map.put("out_trade_no", out_trade_no);
		map.put("subject", subject);
		map.put("payment_type", "1");
		map.put("seller_id", alipayMoveConfigMap.get("seller_email"));  
		//map.put("seller_id", "webpay2011@jiayougo.com");
		map.put("total_fee", orderin.get("due_money")); // 库里查的
		map.put("body", body);
		/*
		 *  生成要请求给支付宝的参数数组
		 */
		Map<String, String> sPara = AlipaySubmit.buildRequestPara(map);
		return sPara;
	}
	
	/**
	 * 获取交易取消的时间
	 * @param orderCode
	 * 		订单编号
	 * @return 交易取消的时间
	 */
	public String getOrderCancelTime(String orderCode,String createTime) {

		String timeStr = "";
		int time = 0;
		/*相差分钟*/
		int diffTime = calAlipayValidTime(createTime);

		List<MDataMap> ics = DbUp.upTable("oc_order_activity").queryAll("activity_code,out_active_code", "",
				" activity_code like 'CX%' and order_code in (select order_code from oc_orderinfo where big_order_code=:order_code)",
				new MDataMap("order_code", orderCode));

		if (ics != null && !ics.isEmpty()) {
			List<String> cxCodes = new ArrayList<String>();
			for (int jj = 0; jj < ics.size(); jj++) {
				cxCodes.add(ics.get(jj).get("activity_code"));
			}
			
			String value = new PlusSupportProduct().getQrCodeAging(cxCodes);
			
			if(StringUtils.isNotEmpty(value)){
				String type = value.split("&")[0];
				String va = value.split("&")[1];
				int temp = Integer.parseInt(va);
				
				if ("449747280001".equals(type)) {
					
					int diffHour = diffTime%60 > 0 ? (diffTime/60)+1 :diffTime/60;
					
					time = temp - diffHour;
					if(time > 0){
						
						timeStr = time + "h";
						
					}
				} else if ("449747280002".equals(type)) {
					time = temp - diffTime;
					if(time>0){
						timeStr = time+"m";
					}
				} else {
					int diffMill = diffTime*60;
					time = (temp/60)-diffMill;
					if(time>0){
						timeStr = time+"m";
					}
				}
			}
			
		}else{
			
			time = diffTime%60>0?(diffTime/60)+1:diffTime/60;
			
			if(24 - time > 0){
				
				timeStr = (24-time)+"h";
				
			}
			
		}
		
		
		return timeStr;

	}
	
	/**
	 * 订单编号
	 * @param orderCode
	 * 		订单编号
	 * @return 返回创建时间
	 */
	public String getCreateTime(String orderCode){
		
		String timeStr = "";
		
		if(StringUtils.startsWith(orderCode, "OS")){
			
			MDataMap bigOrderMap = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",orderCode);
			
			if(bigOrderMap != null){
				
				timeStr = bigOrderMap.get("create_time");
				
			}
			
		}
		
		if(StringUtils.startsWith(orderCode, "DD")){
			
			MDataMap orderMap = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
			
			if(orderMap != null){
				
				timeStr = orderMap.get("create_time");
				
			}
			
			
		}
		
		return timeStr;
		
	}
	
	/**
	 * 计算两个时间之间相差的分钟
	 * @param createTime
	 * 		创建时间
	 * @return 两者相差时间
	 */
	public int calAlipayValidTime(String createTime){
		
		int diff = 0;
		
		Date currDate = new Date();
		
		Date createDate = DateUtil.toDate(createTime, DateUtil.DATE_FORMAT_DATETIME);
		
		Calendar currCalendar = Calendar.getInstance();
		
		currCalendar.setTime(currDate);
		
		Calendar createCalendar = Calendar.getInstance();
		
		createCalendar.setTime(createDate);
		
		long diffMin = (currCalendar.getTimeInMillis() - createCalendar.getTimeInMillis())/(1000*60);
		
		long mod = (currCalendar.getTimeInMillis() - createCalendar.getTimeInMillis())%(1000*60);
		
		diff = Integer.parseInt(String.valueOf(diffMin));
		
		if(mod > 0){
			
			diff++;
			
		}
		
		return diff;
		
	}
	
}
