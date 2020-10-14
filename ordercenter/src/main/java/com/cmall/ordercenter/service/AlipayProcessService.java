package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.alipay.config.AlipayConfig;
import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;
import com.cmall.ordercenter.alipay.config.UnionpayConfig;
import com.cmall.ordercenter.alipay.sign.RSA;
import com.cmall.ordercenter.alipay.util.AlipaySubmit;
import com.cmall.ordercenter.alipay.util.HttpRequester;
import com.cmall.ordercenter.alipay.util.UnionpayHiiposmUtil;
import com.cmall.ordercenter.model.AlipayProcessInput;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.PayResult;
import com.cmall.ordercenter.model.UnionpayProcessInput;
import com.cmall.systemcenter.ali.util.AlipayNotify;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 支付宝接口实现类
 * 
 * @author Administrator
 * 
 */
public class AlipayProcessService extends BaseClass {

	public static final String OC_ORDERINFO_UPPER = "oc_orderinfo_upper";
	public static final String OC_ORDERINFO = "oc_orderinfo";
	public static final String OC_PAYMENT = "oc_payment";// 支付宝接口表名
	public static final String OC_UNIONPAY = "oc_Unionpay";// 和包回调信息日志表
	public static final String _input_charset = "UTF-8";//
	public static final String PAYTYPE = "449716200001"; // 支付宝支付
	public static final String UNIONPAYTYPE = "449746280005"; // 和包支付
	public static final String OC_PAYMENT_PASS = "oc_payment_pass"; // 传递支付宝参数日志表
	public static final String LC_PAY_LOG = "lc_pay_log"; // 支付错误日志表
	public static final String OC_ORDER_PAY_PAYTYPE_Alipay = "449746280003";  //oc_order_pay中记录的支付宝支付类型
	public static final String OC_ORDER_PAY_PAYTYPE_WECHAT = "449746280005";  //oc_order_pay中记录的微信支付类型

	/**
	 * 调用支付宝/和包 接口
	 * 
	 * @param sOrderCode
	 * @return
	 */
	public String createForm(String sOrderCode,String domainName) {
		String type = "";
		
		if(sOrderCode!=null && !"".equals(sOrderCode) && "wap".equals(sOrderCode.substring(sOrderCode.length()-3, sOrderCode.length()))){
			sOrderCode = sOrderCode.substring(0, sOrderCode.length()-3);
			type = "javaWap";
		}
		
		String resultValue = "";
		String subject = "";
		
		boolean bool = false;
		boolean unionBool = false;
		PayResult payResult = new PayResult();
		AlipayProcessInput alipayProcessInput = new AlipayProcessInput();
		Map<String, String> configWeb = new HashMap<String, String>();
		OrderService orderService = new OrderService();

		MDataMap queryMap = new MDataMap();
		queryMap.put("out_trade_no", sOrderCode);

		MDataMap unionQueryMap = new MDataMap();
		unionQueryMap.put("orderId", sOrderCode);
		try {
			/**
			 * 查看支付宝支付接口内容
			 */
			MDataMap dm = DbUp.upTable(OC_PAYMENT).one("out_trade_no",queryMap.get("out_trade_no")); // 查询订单

			if (dm != null) {
				if (!"1".equals(dm.get("flag_success"))) { // 判断此订单是否已支付
					bool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/**
			 * 查看和包支付内容(暂时没用)
			 */
			// 查询此订单是否调用过和包接口
			MDataMap unionDm = DbUp.upTable(OC_UNIONPAY).one("orderId",unionQueryMap.get("orderId"));
			if (unionDm != null) {
				if (!"SUCCESS".equals(unionDm.get("status"))) {
					unionBool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/*
			 * 订单 没有支付过 或者 支付没有成功 可以继续进行支付
			 */
			if ((dm == null || bool == true)
					&& (unionDm == null || unionBool == true)) {
				UnionpayProcessInput unionpayProcessInput = new UnionpayProcessInput();
				AlipayConfig alipayConfig = new AlipayConfig();
				MDataMap orderMap = new MDataMap();

				if ("DD".equals(sOrderCode.substring(0, 2))) {
					// 通过订单号查询订单金额
					orderMap = DbUp.upTable(OC_ORDERINFO).one("order_code",sOrderCode);
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}
				} else if ("OS".equals(sOrderCode.substring(0, 2))) {
					orderMap = DbUp.upTable(OC_ORDERINFO_UPPER).one("big_order_code", sOrderCode);
					
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}

				}

				// 获取支付参数
				if (!configWeb.isEmpty()) {
					String payType = orderMap.get("pay_type"); // 支付方式
					if (PAYTYPE.equals(payType)||"449746280003".equals(payType)) { // 支付宝支付
						alipayProcessInput.setSeller_email(configWeb.get("seller_email"));
						alipayProcessInput.setService(configWeb.get("service"));
//						if("DD".equals(sOrderCode.substring(0, 2))){
////							if (!"".equals(orderMap.get("product_name")) && orderMap.get("product_name") != null
////									&& orderMap.get("product_name").length() < 128) {
////								subject = orderMap.get("product_name");
////							} else {
////								subject = orderMap.get("product_name").substring(0,127);
////							}
//							subject = "商品名称";
//						}else if("OS".equals(sOrderCode.substring(0, 2))){
//							//String productNameAll =  orderService.productNameAll(sOrderCode);
//							subject = "商品名称";
//						}

//						alipayProcessInput.setSubject(bConfig("ordercenter.PRODUCT_NAME"));
						alipayProcessInput.setSubject("商品1");
						alipayProcessInput.setTotal_fee(orderMap.get("due_money"));
						alipayProcessInput.setPartner(configWeb.get("partner"));// AlipayConfig.partner
						alipayProcessInput.setInput_charset(configWeb.get("input_charset"));
						alipayProcessInput.setOut_trade_no(sOrderCode);
						alipayProcessInput.setNotify_url(configWeb.get("notify_url"));
						
//						alipayProcessInput.setPayment_type(orderMap.get("pay_type"));
						alipayProcessInput.setPayment_type("1");
//						alipayProcessInput.setDefaultbank("CMB-DEBIT");   //默认网银   (招商银行)
						alipayProcessInput.setPaymethod("directPay");  //默认支付方式 (bankPay（网银支付）、directPay（余额宝支付）)
						
						if("SI2009".equals(orderMap.get("seller_code"))){
							// 跳转成功页
							if(domainName!=null && !"".equals(domainName)){
								//家有惠跳转成功页配置参数  防止域名切换    域名由前台传递
	 							alipayProcessInput.setReturn_url(domainName + bConfig("ordercenter.return_url_JiaYouHuiParamWeb")); 
							}else{
								//家有惠跳转成功页面的全路径   防止前台传过来的域名是空
								alipayProcessInput.setReturn_url(configWeb.get("return_url")); 
							}
						}else if("SI2003".equals(orderMap.get("seller_code"))){
							//和新坤对接的支付宝WAP
							if("javaWap".equals(type)){
								// 跳转成功页
								//if(domainName!=null && !"".equals(domainName)){
									//惠家有跳转成功页配置参数  防止域名切换    域名由前台传递
								//	String finalDomain = domainName;
//									if(finalDomain.indexOf("&c=") != -1){
//										String[] domainArray = finalDomain.split("&c=");
//										finalDomain = domainArray[0] + (domainArray.length >= 2 ? bConfig("ordercenter.return_url_HuiJiaYouParamWeb") +"?c="+domainArray[1]: bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));
//									}
									alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));    
								//}
							}else{
								//和.net对接的支付宝WAP
								alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouWap")); 
							
							}
						}else if("SI3003".equals(orderMap.get("seller_code"))){
							
							//沙皮狗支付宝WAP支付
							if("javaWap".equals(type)){
								
								alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_ShaPiGouParamWeb"));     
							}else{
								//和.net对接的支付宝WAP
								alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouWap")); 
							
							}
							
						}
						
						MDataMap orderTypeMap = new MDataMap();
						// 查询订单的订单类型
						if(sOrderCode.startsWith("OS")){
							orderTypeMap = DbUp.upTable(OC_ORDERINFO).oneWhere("order_type", "", "", "big_order_code", sOrderCode);
						}else{
							orderTypeMap = DbUp.upTable(OC_ORDERINFO).oneWhere("order_type", "", "", "order_code", sOrderCode);
						}
						
						// 如果是拼好货订单则重设为对应的跳转地址
						if("449715200013".equals(orderTypeMap.get("order_type"))){
							alipayProcessInput.setReturn_url(configWeb.get("return_url_pinhaohuo"));
						}

						resultValue = AlipaySubmit.buildRequest(
								objectToMap(alipayProcessInput), "get", "支付",
								orderMap.get("seller_code"));

					} else if (UNIONPAYTYPE.equals(payType)) { // 银联支付
						resultValue = unionpayPayment(orderMap);
					}
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);

			MDataMap queryMapPass = new MDataMap();
			queryMapPass.put("out_trade_no", sOrderCode);
			queryMapPass.put("error_date", FormatHelper.upDateTime());
//			queryMapPass.put("error_expection", e.getMessage());
			DbUp.upTable(LC_PAY_LOG).dataInsert(queryMapPass);

			resultValue = "报错!";
		}
		return resultValue;
	}
	
	
	/**
	 * 支付宝纯网关支付
	 * @param sOrderCode 订单号
	 * @param domainName
	 * @param defaultbank   银行网关(每个网关所传的值由，支付宝定义)，  如果要是支付宝支付(alipay（自定义）)
	 * @return
	 */
	public String createFormBank(String sOrderCode,String domainName,String defaultbank) {

		String type = "";
		
		if(sOrderCode!=null && !"".equals(sOrderCode) && "wap".equals(sOrderCode.substring(sOrderCode.length()-3, sOrderCode.length()))){
			sOrderCode = sOrderCode.substring(0, sOrderCode.length()-3);
			type = "javaWap";
		}
		
		String resultValue = "";
		String subject = "";
		
		boolean bool = false;
		boolean unionBool = false;
		PayResult payResult = new PayResult();
		AlipayProcessInput alipayProcessInput = new AlipayProcessInput();
		Map<String, String> configWeb = new HashMap<String, String>();
		OrderService orderService = new OrderService();

		MDataMap queryMap = new MDataMap();
		queryMap.put("out_trade_no", sOrderCode);

		MDataMap unionQueryMap = new MDataMap();
		unionQueryMap.put("orderId", sOrderCode);
		try {
			/**
			 * 查看支付宝支付接口内容
			 */
			MDataMap dm = DbUp.upTable(OC_PAYMENT).one("out_trade_no",queryMap.get("out_trade_no")); // 查询订单

			if (dm != null) {
				if (!"1".equals(dm.get("flag_success"))) { // 判断此订单是否已支付
					bool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/**
			 * 查看和包支付内容
			 */
			// 查询此订单是否调用过和包接口
			MDataMap unionDm = DbUp.upTable(OC_UNIONPAY).one("orderId",unionQueryMap.get("orderId"));
			if (unionDm != null) {
				if (!"SUCCESS".equals(unionDm.get("status"))) {
					unionBool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/*
			 * 订单 没有支付过 或者 支付没有成功 可以继续进行支付
			 */
			if ((dm == null || bool == true)
					&& (unionDm == null || unionBool == true)) {
				UnionpayProcessInput unionpayProcessInput = new UnionpayProcessInput();
				AlipayConfig alipayConfig = new AlipayConfig();
				MDataMap orderMap = new MDataMap();

				if ("DD".equals(sOrderCode.substring(0, 2))) {
					// 通过订单号查询订单金额
					orderMap = DbUp.upTable(OC_ORDERINFO).one("order_code",sOrderCode);
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}
				} else if ("OS".equals(sOrderCode.substring(0, 2))) {
					orderMap = DbUp.upTable(OC_ORDERINFO_UPPER).one("big_order_code", sOrderCode);
					
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}

				}

				// 获取支付参数
				if (!configWeb.isEmpty()) {
					String payType = orderMap.get("pay_type"); // 支付方式
					if (PAYTYPE.equals(payType)) { // 支付宝支付
						alipayProcessInput.setSeller_email(configWeb.get("seller_email"));
						alipayProcessInput.setService(configWeb.get("service"));
//						if("DD".equals(sOrderCode.substring(0, 2))){
////							if (!"".equals(orderMap.get("product_name")) && orderMap.get("product_name") != null
////									&& orderMap.get("product_name").length() < 128) {
////								subject = orderMap.get("product_name");
////							} else {
////								subject = orderMap.get("product_name").substring(0,127);
////							}
//							subject = "商品名称";
//						}else if("OS".equals(sOrderCode.substring(0, 2))){
//							//String productNameAll =  orderService.productNameAll(sOrderCode);
//							subject = "商品名称";
//						}

//						alipayProcessInput.setSubject(bConfig("ordercenter.PRODUCT_NAME"));
						alipayProcessInput.setSubject("商品1");
						alipayProcessInput.setTotal_fee(orderMap.get("due_money"));
						alipayProcessInput.setPartner(configWeb.get("partner"));// AlipayConfig.partner
						alipayProcessInput.setInput_charset(configWeb.get("input_charset"));
						alipayProcessInput.setOut_trade_no(sOrderCode);
						alipayProcessInput.setNotify_url(configWeb.get("notify_url"));
						
//						alipayProcessInput.setPayment_type(orderMap.get("pay_type"));
						alipayProcessInput.setPayment_type("1");
						if("alipay".equals(defaultbank)){
							alipayProcessInput.setPaymethod("directPay");  //默认支付方式 (bankPay（网银支付）、directPay（余额宝支付）)
						}else{
							alipayProcessInput.setDefaultbank(defaultbank);   //默认网银   (招商银行)
							alipayProcessInput.setPaymethod("bankPay");  //默认支付方式 (bankPay（网银支付）、directPay（余额宝支付）)
						}
						
						if("SI2009".equals(orderMap.get("seller_code"))){
							// 跳转成功页
							if(domainName!=null && !"".equals(domainName)){
								//家有惠跳转成功页配置参数  防止域名切换    域名由前台传递
	 							alipayProcessInput.setReturn_url(domainName + bConfig("ordercenter.return_url_JiaYouHuiParamWeb")); 
							}else{
								//家有惠跳转成功页面的全路径   防止前台传过来的域名是空
								alipayProcessInput.setReturn_url(configWeb.get("return_url")); 
							}
						}else if("SI2003".equals(orderMap.get("seller_code"))){
							//和新坤对接的支付宝WAP
							if("javaWap".equals(type)){
								// 跳转成功页
								//if(domainName!=null && !"".equals(domainName)){
									//惠家有跳转成功页配置参数  防止域名切换    域名由前台传递
								//	String finalDomain = domainName;
//									if(finalDomain.indexOf("&c=") != -1){
//										String[] domainArray = finalDomain.split("&c=");
//										finalDomain = domainArray[0] + (domainArray.length >= 2 ? bConfig("ordercenter.return_url_HuiJiaYouParamWeb") +"?c="+domainArray[1]: bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));
//									}
									alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));    
								//}
							}else{
								//和.net对接的支付宝WAP
								alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouWap")); 
							
							}
							
						}
						

						resultValue = AlipaySubmit.buildRequest(
								objectToMap(alipayProcessInput), "get", "支付",
								orderMap.get("seller_code"));

					} else if (UNIONPAYTYPE.equals(payType)) { // 银联支付
						resultValue = unionpayPayment(orderMap);
					}
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

			MDataMap queryMapPass = new MDataMap();
			queryMapPass.put("out_trade_no", sOrderCode);
			queryMapPass.put("error_date", FormatHelper.upDateTime());
			queryMapPass.put("error_expection", e.getMessage());
			DbUp.upTable(LC_PAY_LOG).dataInsert(queryMapPass);

			resultValue = "报错!";
		}
		return resultValue;
	
	}
	@Deprecated
	public String createFromWangGuan(String sOrderCode,String domainName){

		String type = "";
		
		if(sOrderCode!=null && !"".equals(sOrderCode) && "wap".equals(sOrderCode.substring(sOrderCode.length()-3, sOrderCode.length()))){
			sOrderCode = sOrderCode.substring(0, sOrderCode.length()-3);
			type = "javaWap";
		}
		
		String resultValue = "";
		String subject = "";
		
		boolean bool = false;
		boolean unionBool = false;
		PayResult payResult = new PayResult();
		AlipayProcessInput alipayProcessInput = new AlipayProcessInput();
		Map<String, String> configWeb = new HashMap<String, String>();
		OrderService orderService = new OrderService();

		MDataMap queryMap = new MDataMap();
		queryMap.put("out_trade_no", sOrderCode);

		MDataMap unionQueryMap = new MDataMap();
		unionQueryMap.put("orderId", sOrderCode);
		try {
			/**
			 * 查看支付宝支付接口内容
			 */
			MDataMap dm = DbUp.upTable(OC_PAYMENT).one("out_trade_no",queryMap.get("out_trade_no")); // 查询订单

			if (dm != null) {
				if (!"1".equals(dm.get("flag_success"))) { // 判断此订单是否已支付
					bool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/**
			 * 查看和包支付内容
			 */
			// 查询此订单是否调用过和包接口
			MDataMap unionDm = DbUp.upTable(OC_UNIONPAY).one("orderId",unionQueryMap.get("orderId"));
			if (unionDm != null) {
				if (!"SUCCESS".equals(unionDm.get("status"))) {
					unionBool = true;
				} else {
					payResult.inErrorMessage(939301205);
				}
			}
			/*
			 * 订单 没有支付过 或者 支付没有成功 可以继续进行支付
			 */
			if ((dm == null || bool == true)
					&& (unionDm == null || unionBool == true)) {
				UnionpayProcessInput unionpayProcessInput = new UnionpayProcessInput();
				AlipayConfig alipayConfig = new AlipayConfig();
				MDataMap orderMap = new MDataMap();

				if ("DD".equals(sOrderCode.substring(0, 2))) {
					// 通过订单号查询订单金额
					orderMap = DbUp.upTable(OC_ORDERINFO).one("order_code",sOrderCode);
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}
				} else if ("OS".equals(sOrderCode.substring(0, 2))) {
					orderMap = DbUp.upTable(OC_ORDERINFO_UPPER).one("big_order_code", sOrderCode);
					
					if (!orderMap.isEmpty()) {
						configWeb = alipayConfig.alipayWebConfig(orderMap.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
					} else {
						resultValue = "订单号不存在";
					}

				}

				// 获取支付参数
				if (!configWeb.isEmpty()) {
					String payType = orderMap.get("pay_type"); // 支付方式
					if (PAYTYPE.equals(payType)) { // 支付宝支付
						alipayProcessInput.setSeller_email(configWeb.get("seller_email"));
						alipayProcessInput.setService(configWeb.get("service"));
//						if("DD".equals(sOrderCode.substring(0, 2))){
////							if (!"".equals(orderMap.get("product_name")) && orderMap.get("product_name") != null
////									&& orderMap.get("product_name").length() < 128) {
////								subject = orderMap.get("product_name");
////							} else {
////								subject = orderMap.get("product_name").substring(0,127);
////							}
//							subject = "商品名称";
//						}else if("OS".equals(sOrderCode.substring(0, 2))){
//							//String productNameAll =  orderService.productNameAll(sOrderCode);
//							subject = "商品名称";
//						}

//						alipayProcessInput.setSubject(bConfig("ordercenter.PRODUCT_NAME"));
						alipayProcessInput.setSubject("商品");
						alipayProcessInput.setTotal_fee(orderMap.get("due_money"));
						alipayProcessInput.setPartner(configWeb.get("partner"));// AlipayConfig.partner
						alipayProcessInput.setInput_charset(configWeb.get("input_charset"));
						alipayProcessInput.setOut_trade_no(sOrderCode);
						alipayProcessInput.setNotify_url(configWeb.get("notify_url"));
						
//						alipayProcessInput.setPayment_type(orderMap.get("pay_type"));
						alipayProcessInput.setPayment_type("1");
						alipayProcessInput.setDefaultbank("CMB-DEBIT");   //默认网银   (招商银行)
						alipayProcessInput.setPaymethod("bankPay");  //默认支付方式 (bankPay（网银支付）、directPay（余额宝支付）)
						
						if("SI2009".equals(orderMap.get("seller_code"))){
							// 跳转成功页
							if(domainName!=null && !"".equals(domainName)){
								//家有惠跳转成功页配置参数  防止域名切换    域名由前台传递
	 							alipayProcessInput.setReturn_url(domainName + bConfig("ordercenter.return_url_JiaYouHuiParamWeb")); 
							}else{
								//家有惠跳转成功页面的全路径   防止前台传过来的域名是空
								alipayProcessInput.setReturn_url(configWeb.get("return_url")); 
							}
						}else if("SI2003".equals(orderMap.get("seller_code"))){
							//和新坤对接的支付宝WAP
							if("javaWap".equals(type)){
								// 跳转成功页
								//if(domainName!=null && !"".equals(domainName)){
									//惠家有跳转成功页配置参数  防止域名切换    域名由前台传递
								//	String finalDomain = domainName;
//									if(finalDomain.indexOf("&c=") != -1){
//										String[] domainArray = finalDomain.split("&c=");
//										finalDomain = domainArray[0] + (domainArray.length >= 2 ? bConfig("ordercenter.return_url_HuiJiaYouParamWeb") +"?c="+domainArray[1]: bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));
//									}
									alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouParamWeb"));    
								//}
							}else{
								//和.net对接的支付宝WAP
								alipayProcessInput.setReturn_url(bConfig("ordercenter.return_url_HuiJiaYouWap")); 
							
							}
							
						}
						

						resultValue = AlipaySubmit.buildRequest(
								objectToMap(alipayProcessInput), "get", "支付",
								orderMap.get("seller_code"));

					} else if (UNIONPAYTYPE.equals(payType)) { // 银联支付
						resultValue = unionpayPayment(orderMap);
					}
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

			MDataMap queryMapPass = new MDataMap();
			queryMapPass.put("out_trade_no", sOrderCode);
			queryMapPass.put("error_date", FormatHelper.upDateTime());
			queryMapPass.put("error_expection", e.getMessage());
			DbUp.upTable(LC_PAY_LOG).dataInsert(queryMapPass);

			resultValue = "报错!";
		}
		return resultValue;
	
	}
	
	public static void main(String[] args) {
		AlipayProcessService a = new AlipayProcessService();
		a.createForm("DD150910800333", "SI2009");
	}
	
	
	/**
	 * 调用支付宝/和包 接口
	 * 
	 * @param sOrderCode
	 * @return
	 */
	@Deprecated
	public String createFormNew(String payCode) {
		String resultValue = "";
		AlipayProcessInput alipayProcessInput = new AlipayProcessInput();
		Map<String, String> configWeb = new HashMap<String, String>();
		
		MDataMap queryMap = new MDataMap();
		queryMap.put("out_trade_no", payCode);

		try {
			
			MDataMap queryHcOrder = new MDataMap();
			queryHcOrder.put("pay_code", payCode);
			queryHcOrder.put("state", "0");
			
			// 通过订单号查询订单金额
			MDataMap dm = DbUp.upTable("oc_pay_info").one("pay_code",payCode,"state","0"); // 查询订单

			/*
			 * 订单 没有支付过 或者 支付没有成功 可以继续进行支付
			 */
			if (dm!=null && !"".equals(dm) && dm.size()>0) {
				AlipayConfig alipayConfig = new AlipayConfig();
				
				configWeb = alipayConfig.alipayWebConfig(dm.get("seller_code")); // 获取支付所需的必要参数(判断此订单是用乾和、家有、刘嘉玲)
				// 获取支付参数
				if (!configWeb.isEmpty()) {
					String payType = dm.get("pay_type"); // 支付方式
					if (PAYTYPE.equals(payType)) { // 支付宝支付
						alipayProcessInput.setSeller_email(configWeb.get("seller_email"));
						alipayProcessInput.setService(configWeb.get("service"));

						alipayProcessInput.setSubject(bConfig("ordercenter.PRODUCT_NAME"));
						alipayProcessInput.setPayment_type(dm.get("pay_type"));
						alipayProcessInput.setTotal_fee(dm.get("due_money"));
						alipayProcessInput.setPartner(configWeb.get("partner"));// AlipayConfig.partner
						alipayProcessInput.setInput_charset(configWeb.get("input_charset"));
						alipayProcessInput.setOut_trade_no(payCode);
						alipayProcessInput.setNotify_url(configWeb.get("notify_url"));
						alipayProcessInput.setReturn_url(configWeb.get("return_url")); // 跳转成功页

						resultValue = AlipaySubmit.buildRequest(
								objectToMap(alipayProcessInput), "get", "支付",
								dm.get("seller_code"));
					} 
				}

			}else{
				resultValue = "支付单号为空!";
			}
		} catch (Exception e) {
			e.printStackTrace();

			MDataMap queryMapPass = new MDataMap();
			queryMapPass.put("out_trade_no", payCode);
			queryMapPass.put("error_date", FormatHelper.upDateTime());
			queryMapPass.put("error_expection", e.getMessage());
			DbUp.upTable(LC_PAY_LOG).dataInsert(queryMapPass);
			
			resultValue = "错误";
		}
		return resultValue;
	}
	/**
	 * 网页支付(回调)
	 * 
	 * @param insertDatamap
	 * @param paramValue
	 * @return
	 */

	public PayResult resultWebValue(MDataMap insertDatamap, String paramValue) {
		PayResult payResult = new PayResult();
		try {
			OcOrderPay ocOrderPay = null;
			String mark = "002"; // 支付标示(web:网页支付

			String sPayCode = WebHelper.upCode("PM");
//			System.out.println("extra_common_param==============================="+insertDatamap.get("extra_common_param"));
			MDataMap insertDataMapNew = new MDataMap();
			insertDataMapNew.put("out_trade_no",insertDatamap.get("out_trade_no")); //
			insertDataMapNew.put("trade_status",insertDatamap.get("trade_status")); //
			insertDataMapNew.put("sign_type", insertDatamap.get("sign_type"));
			insertDataMapNew.put("sign", insertDatamap.get("sign"));
			insertDataMapNew.put("trade_no", insertDatamap.get("trade_no")); //
			insertDataMapNew.put("buyer_email",insertDatamap.get("buyer_email"));
			insertDataMapNew.put("total_fee", insertDatamap.get("total_fee")); //
			insertDataMapNew.put("seller_email",insertDatamap.get("seller_email")); //
			if (insertDatamap.containsKey("gmt_payment")) {
				insertDataMapNew.put("gmt_payment",insertDatamap.get("gmt_payment"));
			} else {
				insertDataMapNew.put("gmt_payment", "");
			}

			if (insertDatamap.containsKey("gmt_create")) {
				insertDataMapNew.put("gmt_create",insertDatamap.get("gmt_create"));
			} else {
				insertDataMapNew.put("gmt_create", "");
			}
			insertDataMapNew.put("param_value", paramValue);
			insertDataMapNew.put("mark", mark);
			insertDataMapNew.put("create_time", FormatHelper.upDateTime()); //
			insertDataMapNew.put("payment_code", sPayCode);

			MDataMap queryMap = new MDataMap();
			queryMap.put("out_trade_no", insertDatamap.get("out_trade_no"));

			// 插入日志流水
			if (payResult.upFlagTrue()) {
				DbUp.upTable(OC_PAYMENT).dataInsert(insertDataMapNew); // 插入支付日志流水信息
			}

			// 执行校验规则
			if (payResult.upFlagTrue()) {
				MDataMap dm = null;
				Map<String, String> alipayMoveConfigMap = new HashMap<String, String>();
				AlipayConfig alipayConfig = new AlipayConfig(); // 网页支付配置文件

				if ("DD".equals(insertDatamap.get("out_trade_no").substring(0,2))) {
					dm = DbUp.upTable("oc_orderinfo").one("order_code",insertDatamap.get("out_trade_no")); // 查询订单

				} else if ("OS".equals(insertDatamap.get("out_trade_no").substring(0, 2))) {
					dm = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",insertDatamap.get("out_trade_no")); // 查询订单
				}

				if (dm!=null) {
					alipayMoveConfigMap = alipayConfig.alipayWebConfig(dm.get("seller_code")); // 获取网页支付相关配置信息
					AlipayNotify alipayNotify = new AlipayNotify();
					// 验签
					if (alipayNotify.verify(insertDatamap)) {
						// 判断交易状态是否为成功
						if ("TRADE_SUCCESS".equals(insertDatamap.get("trade_status"))
								|| "TRADE_FINISHED".equals(insertDatamap.get("trade_status"))) {

							// 判断金额是否和应付一致
							if (dm.get("due_money").equals(insertDatamap.get("total_fee"))) {

								// 定义即将锁定的编号
								String sLockKey = insertDatamap.get("out_trade_no")+ insertDatamap.get("trade_status");

								// 获取锁定唯一约束值
								String sLockUuid = WebHelper.addLock(60,sLockKey);

								// 开始锁定交易的流水号和交易类型60秒
								if (StringUtils.isNotEmpty(sLockUuid)) {
									if("DD".equals(insertDatamap.get("out_trade_no").substring(0, 2))){
										// 判断是否有成功支付流水的记录
										if (DbUp.upTable("oc_order_pay").count(
												"order_code", dm.get("order_code"), "pay_type",OC_ORDER_PAY_PAYTYPE_Alipay) == 0) {

											insertOcOrderPayVoid(insertDatamap,mark); // 插入数据

											// 查询大订单
											MDataMap bigOrderMap = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",dm.get("big_order_code"));
											if (bigOrderMap != null) {
													// 已付的款
													BigDecimal payed_money = new BigDecimal(bigOrderMap.get("payed_money")).add(new BigDecimal(insertDatamap.get("total_fee")));
													// 应该要付的款
													BigDecimal due_money = new BigDecimal(bigOrderMap.get("order_money")).min(new BigDecimal(bigOrderMap.get("payed_money")));

													MDataMap updateDataBigMap = new MDataMap();
													updateDataBigMap.put("payed_money",String.valueOf(payed_money));
													updateDataBigMap.put("due_money",String.valueOf(due_money));
													updateDataBigMap.put("update_time",FormatHelper.upDateTime());
													updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
													// 更新大订单表
													DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
												
											}
											payResult.setOrderCode(dm.get("order_code"));
										} else {
											payResult.inErrorMessage(939301205);
										}
									}else if("OS".equals(insertDatamap.get("out_trade_no").substring(0, 2))){
										List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code",dm.get("big_order_code")));
										
										for(MDataMap orderInfoMap : list){
											if (DbUp.upTable("oc_order_pay").count("order_code",orderInfoMap.get("order_code"), "pay_type",OC_ORDER_PAY_PAYTYPE_Alipay) == 0) {
												
												MDataMap map = new MDataMap();
												map.put("out_trade_no", orderInfoMap.get("order_code"));
												map.put("trade_no", insertDatamap.get("trade_no"));
												map.put("total_fee", orderInfoMap.get("due_money"));
												map.put("seller_email", insertDatamap.get("seller_email"));
												if(StringUtils.isNotBlank(insertDatamap.get("extra_common_param"))) {
													map.put("extra_common_param", insertDatamap.get("extra_common_param"));
												}
												insertOcOrderPayVoid(map,mark); // 插入数据
											} else {
												payResult.inErrorMessage(939301205);
											}
										}
										// 已付的款
										BigDecimal payed_money = new BigDecimal(dm.get("payed_money")).add(new BigDecimal(insertDatamap.get("total_fee")));
										MDataMap updateDataBigMap = new MDataMap();
										updateDataBigMap.put("payed_money",String.valueOf(payed_money));
										updateDataBigMap.put("due_money","0.00");
										updateDataBigMap.put("update_time",FormatHelper.upDateTime());
										updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
										// 更新大订单表
										DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
										
										//此订单如果是家有汇的订单，需要将大订单下的所有小订单，payed_money字段更新成due_money字段的值(2015-12-07更新)
										if("SI2009".equals(dm.get("seller_code"))){
											List<Map<String, Object>> infoList = DbUp.upTable("oc_orderinfo").dataSqlList("select * from oc_orderinfo where big_order_code = '"+dm.get("big_order_code")+"'", new MDataMap());
											for(Map<String, Object> infoMap : infoList){
												DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("payed_money",String.valueOf(infoMap.get("due_money")),"order_code",String.valueOf(infoMap.get("order_code"))), "payed_money", "order_code");
											}
										}
										payResult.setOrderCode(dm.get("big_order_code"));
									}
									
									WebHelper.unLock(sLockUuid);

								} else {
									payResult.inErrorMessage(939301206);
								}

							} else {
								payResult.inErrorMessage(939301203);
							}

						} else {
							payResult.inErrorMessage(939301202);
						}

					} else {
						payResult.inErrorMessage(939301201, sPayCode);
					}
				}

			}

			// 更新流水表中的记录
			DbUp.upTable("oc_payment").dataUpdate(
					new MDataMap("payment_code", sPayCode, "process_result",
							payResult.upJson(), "process_time",
							FormatHelper.upDateTime(), "flag_success",
							payResult.upFlagTrue() ? "1" : "0"),
					"process_result,process_time,flag_success", "payment_code");
		} catch (Exception e) {
			e.printStackTrace();
			payResult.inErrorMessage(939301204);
		}
		return payResult;
	}

	/**
	 * 移动支付(回调)
	 * 
	 * @param insertDatamap
	 */
	public PayResult resultValue(MDataMap insertDatamap, String paramValue) {

		PayResult payResult = new PayResult();

		try {

			
			OcOrderPay ocOrderPay = null;
			int mark = Integer.parseInt(insertDatamap.get("mark")); // 支付标示(web:网页支付

			String sPayCode = WebHelper.upCode("PM");

			insertDatamap.put("param_value", paramValue);

			MDataMap insertDataMapNew = new MDataMap();
			insertDataMapNew.put("out_trade_no",insertDatamap.get("out_trade_no")); //
			insertDataMapNew.put("trade_status",insertDatamap.get("trade_status")); //
			insertDataMapNew.put("sign_type", insertDatamap.get("sign_type"));
			insertDataMapNew.put("sign", insertDatamap.get("sign"));
			insertDataMapNew.put("trade_no", insertDatamap.get("trade_no")); //
			insertDataMapNew.put("buyer_email",insertDatamap.get("buyer_email"));
			insertDataMapNew.put("total_fee", insertDatamap.get("total_fee")); //
			insertDataMapNew.put("seller_email",insertDatamap.get("seller_email")); //
			if (insertDatamap.containsKey("gmt_payment")) {
				insertDataMapNew.put("gmt_payment",insertDatamap.get("gmt_payment"));
			} else {
				insertDataMapNew.put("gmt_payment", "");
			}

			if (insertDatamap.containsKey("gmt_create")) {
				insertDataMapNew.put("gmt_create",insertDatamap.get("gmt_create"));
			} else {
				insertDataMapNew.put("gmt_create", "");
			}
			insertDataMapNew.put("param_value",insertDatamap.get("param_value"));
			insertDataMapNew.put("mark", insertDatamap.get("mark"));
			insertDataMapNew.put("create_time", FormatHelper.upDateTime()); //
			insertDataMapNew.put("payment_code", sPayCode);

			MDataMap queryMap = new MDataMap();
			queryMap.put("out_trade_no", insertDatamap.get("out_trade_no"));

			// 插入日志流水
			if (payResult.upFlagTrue()) {
				DbUp.upTable(OC_PAYMENT).dataInsert(insertDataMapNew); // 插入支付日志流水信息
			}

			// 执行校验规则
			if (payResult.upFlagTrue()) {
				Map<String, String> alipayMoveConfigMap = new HashMap<String, String>();
				AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();
				MDataMap dm = null;
				
				if ("DD".equals(insertDatamap.get("out_trade_no").substring(0,2))) {
					dm = DbUp.upTable("oc_orderinfo").one("order_code", insertDatamap.get("out_trade_no")); // 查询订单

				} else if ("OS".equals(insertDatamap.get("out_trade_no").substring(0, 2))) {
					dm = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",insertDatamap.get("out_trade_no")); // 查询订单
				}
				
					if (dm != null) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(dm.get("seller_code")); // 获取支付相关配置信息
						

						if (RSA.verify(paramValue, insertDatamap.get("sign"),
								alipayMoveConfigMap.get("public_key"), "UTF-8")) {
							// 判断交易状态是否为成功
							if ("TRADE_SUCCESS".equals(insertDatamap.get("trade_status"))
									|| "TRADE_FINISHED".equals(insertDatamap.get("trade_status"))) {
								

								// 判断金额是否和应付一致
								if (dm.get("due_money").equals(insertDatamap.get("total_fee"))) {
									
									// 定义即将锁定的编号
									String sLockKey = insertDatamap.get("out_trade_no")+ insertDatamap.get("trade_status");

									// 获取锁定唯一约束值
									String sLockUuid = WebHelper.addLock(60,sLockKey);

									// 开始锁定交易的流水号和交易类型60秒
									if (StringUtils.isNotEmpty(sLockUuid)) {
										
										if("DD".equals(insertDatamap.get("out_trade_no").substring(0,2))){
											// 判断是否有成功支付流水的记录
											if (DbUp.upTable("oc_order_pay").count("order_code",dm.get("order_code"),"pay_type",OC_ORDER_PAY_PAYTYPE_Alipay) == 0) {
												insertOcOrderPayVoid(insertDatamap,"001"); // 插入数据
												// 查询大订单
												MDataMap bigOrderMap = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",dm.get("big_order_code"));
												if (bigOrderMap != null) {
													// 已付的款
													BigDecimal payed_money = new BigDecimal(bigOrderMap.get("payed_money")).add(new BigDecimal(insertDatamap.get("total_fee")));
													// 应该要付的款
													BigDecimal due_money = new BigDecimal(bigOrderMap.get("order_money")).min(new BigDecimal(bigOrderMap.get("payed_money")));

													MDataMap updateDataBigMap = new MDataMap();
													updateDataBigMap.put("payed_money",String.valueOf(payed_money));
													updateDataBigMap.put("due_money",String.valueOf(due_money));
													updateDataBigMap.put("update_time",FormatHelper.upDateTime());
													updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
													// 更新大订单表
													DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
												}

												payResult.setOrderCode(dm.get("order_code"));
											} else {
												payResult.inErrorMessage(939301205);
											}
										}else if("OS".equals(insertDatamap.get("out_trade_no").substring(0, 2))){
											List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code",dm.get("big_order_code")));
											
											for(MDataMap orderInfoMap : list){
												if (DbUp.upTable("oc_order_pay").count("order_code",orderInfoMap.get("order_code"),"pay_type",OC_ORDER_PAY_PAYTYPE_Alipay) == 0) {
													
													MDataMap map = new MDataMap();
													map.put("out_trade_no", orderInfoMap.get("order_code"));
													map.put("trade_no", insertDatamap.get("trade_no"));
													map.put("total_fee", orderInfoMap.get("due_money"));
													map.put("seller_email", insertDatamap.get("seller_email"));
													insertOcOrderPayVoid(map,"001"); // 插入数据
												} else {
													payResult.inErrorMessage(939301205);
												}
											}
											// 已付的款
											BigDecimal payed_money = new BigDecimal(dm.get("payed_money")).add(new BigDecimal(insertDatamap.get("total_fee")));
											MDataMap updateDataBigMap = new MDataMap();
											updateDataBigMap.put("payed_money",String.valueOf(payed_money));
											updateDataBigMap.put("due_money","0.00");
											updateDataBigMap.put("update_time",FormatHelper.upDateTime());
											updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
											// 更新大订单表
											DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
											
											
											//此订单如果是惠家有的订单，需要将大订单下的所有小订单，payed_money字段更新成due_money字段的值
											if("SI2003".equals(dm.get("seller_code")) || StringUtils.equals(AppConst.MANAGE_CODE_CDOG, dm.get("seller_code"))){
												List<Map<String, Object>> infoList = DbUp.upTable("oc_orderinfo").dataSqlList("select * from oc_orderinfo where big_order_code = '"+dm.get("big_order_code")+"'", new MDataMap());
												
												for(Map<String, Object> infoMap : infoList){
													DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("payed_money",String.valueOf(infoMap.get("due_money")),"order_code",String.valueOf(infoMap.get("order_code"))), "payed_money", "order_code");
												}
											}
											payResult.setOrderCode(dm.get("big_order_code"));
										}
										WebHelper.unLock(sLockUuid);
									} else {
										payResult.inErrorMessage(939301206);
									}
								} else {
									payResult.inErrorMessage(939301203);
								}

							} else {
								payResult.inErrorMessage(939301202);
							}

						} 
						else {
							payResult.inErrorMessage(939301201, sPayCode);
						}

					} else {
						payResult.inErrorMessage(939301200,insertDatamap.get("out_trade_no"));
					}
				}
			
			// 更新流水表中的记录
			DbUp.upTable("oc_payment").dataUpdate(
					new MDataMap("payment_code", sPayCode, "process_result",
							payResult.upJson(), "process_time",
							FormatHelper.upDateTime(), "flag_success",
							payResult.upFlagTrue() ? "1" : "0"),
					"process_result,process_time,flag_success", "payment_code");
			

		} catch (Exception e) {
			e.printStackTrace();
			payResult.inErrorMessage(939301204);

		}
		return payResult;
	}
	
	
	
	/**
	 * 移动支付(回调)最新
	 * 
	 * @param insertDatamap
	 */
	public PayResult resultValueNew(MDataMap insertDatamap, String paramValue) {

		PayResult payResult = new PayResult();

		try {

			OcOrderPay ocOrderPay = null;
			int mark = Integer.parseInt(insertDatamap.get("mark")); // 支付标示(web:网页支付

			String sPayCode = WebHelper.upCode("PM");

			insertDatamap.put("param_value", paramValue);

			MDataMap insertDataMapNew = new MDataMap();
			insertDataMapNew.put("out_trade_no",insertDatamap.get("out_trade_no")); //
			insertDataMapNew.put("trade_status",insertDatamap.get("trade_status")); //
			insertDataMapNew.put("sign_type", insertDatamap.get("sign_type"));
			insertDataMapNew.put("sign", insertDatamap.get("sign"));
			insertDataMapNew.put("trade_no", insertDatamap.get("trade_no")); //
			insertDataMapNew.put("buyer_email",insertDatamap.get("buyer_email"));
			insertDataMapNew.put("total_fee", insertDatamap.get("total_fee")); //
			insertDataMapNew.put("seller_email",insertDatamap.get("seller_email")); //
			if (insertDatamap.containsKey("gmt_payment")) {
				insertDataMapNew.put("gmt_payment",insertDatamap.get("gmt_payment"));
			} else {
				insertDataMapNew.put("gmt_payment", "");
			}

			if (insertDatamap.containsKey("gmt_create")) {
				insertDataMapNew.put("gmt_create",insertDatamap.get("gmt_create"));
			} else {
				insertDataMapNew.put("gmt_create", "");
			}
			insertDataMapNew.put("param_value",insertDatamap.get("param_value"));
			insertDataMapNew.put("mark", insertDatamap.get("mark"));
			insertDataMapNew.put("create_time", FormatHelper.upDateTime()); //
			insertDataMapNew.put("payment_code", sPayCode);

			MDataMap queryMap = new MDataMap();
			queryMap.put("out_trade_no", insertDatamap.get("out_trade_no"));

			// 插入日志流水
			if (payResult.upFlagTrue()) {
				DbUp.upTable(OC_PAYMENT).dataInsert(insertDataMapNew); // 插入支付日志流水信息
			}

			// 执行校验规则
			if (payResult.upFlagTrue()) {
				Map<String, String> alipayMoveConfigMap = new HashMap<String, String>();
				AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();
				List<Map<String, Object>> listClient =  new ArrayList<Map<String, Object>>();
				MDataMap dm = null;
				
				if("PP".equals(insertDatamap.get("out_trade_no").substring(0,2))){
					dm = DbUp.upTable("oc_pay_info").one("pay_code", insertDatamap.get("out_trade_no")); // 查询订单
				}
				
					if (dm != null) {
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(dm.get("seller_code")); // 获取支付相关配置信息
						
						if (RSA.verify(paramValue, insertDatamap.get("sign"),
								alipayMoveConfigMap.get("public_key"), "UTF-8")) {
							// 判断交易状态是否为成功
							if ("TRADE_SUCCESS".equals(insertDatamap.get("trade_status"))
									|| "TRADE_FINISHED".equals(insertDatamap.get("trade_status"))) {
								

								// 判断金额是否和应付一致
								if (dm.get("due_money").equals(insertDatamap.get("total_fee"))) {
									
									// 定义即将锁定的编号
									String sLockKey = insertDatamap.get("out_trade_no")+ insertDatamap.get("trade_status");

									// 获取锁定唯一约束值
									String sLockUuid = WebHelper.addLock(60,sLockKey);

									// 开始锁定交易的流水号和交易类型60秒
									if (StringUtils.isNotEmpty(sLockUuid)) {
										if("PP".equals(insertDatamap.get("out_trade_no").substring(0,2))){
											
											//查询支付宝单号
											listClient = DbUp.upTable("oc_paydetail").dataSqlList(
													"select * from oc_paydetail where pay_code = '"+insertDatamap.get("out_trade_no")+"' order by create_time desc", new MDataMap());
										
											for(Map<String, Object> map : listClient){
												
												String orderCodeBig = String.valueOf(map.get("big_order_code"));
												String orderCodeSmall = String.valueOf(map.get("order_code"));
												// 判断是否有成功支付流水的记录
												if(DbUp.upTable("oc_order_pay").count("order_code",orderCodeSmall,"pay_type",OC_ORDER_PAY_PAYTYPE_Alipay) == 0){
													MDataMap insertDatamapParams = new MDataMap();
													insertDatamapParams.put("order_code", orderCodeSmall);
													insertDatamapParams.put("trade_no", insertDatamap.get("trade_no"));
													insertDatamapParams.put("total_fee", insertDatamap.get("total_fee"));
													insertDatamapParams.put("seller_email", insertDatamap.get("seller_email"));
													insertDatamapParams.put("pay_code", insertDatamap.get("out_trade_no"));
													// 向oc_order_pay表中插入数据
													insertOcOrderPayVoid(insertDatamap,"001"); 
													
													
													//目前只有惠家有   有大小订单之分
													if("SI2003".equals(dm.get("seller_code"))){
														// 查询大订单
														MDataMap bigOrderMap = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",orderCodeBig);
														if (bigOrderMap != null) {
															// 已付的款
															BigDecimal payed_money = new BigDecimal(bigOrderMap.get("payed_money")).add(new BigDecimal(String.valueOf(map.get("total_fee"))));
															// 应该要付的款
															BigDecimal due_money = new BigDecimal(bigOrderMap.get("order_money")).min(new BigDecimal(bigOrderMap.get("payed_money")));

															MDataMap updateDataBigMap = new MDataMap();
															updateDataBigMap.put("payed_money",String.valueOf(payed_money));
															updateDataBigMap.put("due_money",String.valueOf(due_money));
															updateDataBigMap.put("update_time",FormatHelper.upDateTime());
															updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
															// 更新大订单表
															DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
															
														}else {
															payResult.inErrorMessage(939301205);
														}
													}
												}else{
													payResult.inErrorMessage(939301205);
												}
											}
											//更新支付单 和 支付单详情 信息状态，更改为以支付
											DbUp.upTable("oc_pay_info").dataUpdate(
													new MDataMap("seller_email",insertDatamap.get("seller_email"),"trade_no",insertDatamap.get("trade_no"),"pay_code", insertDatamap.get("out_trade_no"),"state","2"), "state", "pay_code");
											
											DbUp.upTable("oc_paydetail").dataUpdate(new MDataMap("pay_code",insertDatamap.get("out_trade_no"),"state","2"), "state", "pay_code");
										}
										payResult.setOrderCode(dm.get("pay_code"));
										
										WebHelper.unLock(sLockUuid);
										
									} else {
										payResult.inErrorMessage(939301206);
									}
								} else {
									payResult.inErrorMessage(939301203);
								}

							} else {
								payResult.inErrorMessage(939301202);
							}
						} else {
							payResult.inErrorMessage(939301201, sPayCode);
						}

					} else {
						payResult.inErrorMessage(939301200,insertDatamap.get("out_trade_no"));
					}
				}
			
			// 更新流水表中的记录
			DbUp.upTable("oc_payment").dataUpdate(
					new MDataMap("payment_code", sPayCode, "process_result",
							payResult.upJson(), "process_time",
							FormatHelper.upDateTime(), "flag_success",
							payResult.upFlagTrue() ? "1" : "0"),
					"process_result,process_time,flag_success", "payment_code");

		} catch (Exception e) {
			e.printStackTrace();
			payResult.inErrorMessage(939301204);

		}
		return payResult;
	}

	
	
	
	/**
	 * 和包回调结果业务处理(回调)
	 * 
	 * @param insertDatamap
	 * @return
	 */
	public String unionpayResultValue(MDataMap insertDatamap) {
		OcOrderPay ocOrderPay = null;
		List<MDataMap> paymentList = new ArrayList<MDataMap>();
		MDataMap queryMap = new MDataMap();
		queryMap.put("orderId", insertDatamap.get("orderId"));

		try {

			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
					insertDatamap.get("orderId")); // 查询订单
			if (dm != null) {
				String due_money = String.valueOf(Double.parseDouble(dm
						.get("due_money")) * 100); // 由于回调回来的金额是以分为单位的，库里存的金额需乘以100
				// 判断金额是否相同
				if (due_money.equals(insertDatamap.get("amount"))) {
					paymentList = DbUp.upTable(OC_UNIONPAY).queryAll("", "",
							"", queryMap); // 查询此订单是否调用过和包接口

					if (!paymentList.isEmpty()) {
						DbUp.upTable(OC_UNIONPAY)
								// 更新此订单信息
								.dataUpdate(
										insertDatamap,
										"merchantId,payNo,returnCode,message,signType,type,version,amount,amtItem,bankAbbr,mobile,orderId,payDate,accountDate,"
												+ "reserved1,reserved2,status,orderDate,fee,serverCert,hmac",
										"orderId");
					} else {
						DbUp.upTable(OC_UNIONPAY).dataInsert(insertDatamap); // 插入订单信息
					}

					if ("SUCCESS".equals(insertDatamap.get("status"))) {
						OrderService os = new OrderService();
						ocOrderPay = resultOcOrderUnionPay(insertDatamap); // 转换成网页版支付宝完成接口所需的参数
						os.paySucess(ocOrderPay); // 调网页版支付完成接口
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			insertDatamap.put("error_date", FormatHelper.upDateTime());
			insertDatamap.put("error_expection", e.getMessage());
			DbUp.upTable(LC_PAY_LOG).dataInsert(insertDatamap); // 插入订单信息

		}
		return "SUCCESS";

	}

	public static String unionpayPayment(MDataMap orderin) {
		String requesterURL = null;
		HttpRequester url = new HttpRequester(); // 请求连接
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		// 商户会计日期（自定义）
		String merAcDate = sdf.format(new Date()).substring(0, 8);
		String type = "DirectPayConfirm";

		try {
			// String[] amountArray = orderin.get("due_money").split(".00"); //
			// 截取订单金额
			// 这么做有可能会是不对的
			// 12.50元
			// 怎么办
			String amount = String.valueOf(Double.parseDouble(orderin
					.get("due_money")) * 100);// amountArray[0];// 订单金额
			String bankAbbr = ""; // 银行卡代号
			String currency = "00"; // 币种
			// String orderDate = orderin.get("create_time"); //订单提交日期
			String orderDate = merAcDate; // 订单提交日期
			String orderId = orderin.get("order_code"); // 商品订单号
			String period = "15"; // 有效期数量
			String periodUnit = "01"; // 有效期单位
			String merchantAbbr = orderin.get("product_name").substring(0, 10); // 商户展示名称
			String productDesc = ""; // 商品描述
			String productId = UnionpayConfig.merchantId; // 商品编号
			String productName = orderin.get("product_name").substring(0, 10); // 商品名称
			String productNum = ""; // 商品数目
			String reserved1 = ""; // 保留字段1
			String reserved2 = ""; // 保留字段2
			String userToken = ""; // 用户标示
			String payType = orderin.get("pay_type"); // 支付类型
			String showUrl = ""; // 商品展示地址
			String couponsFlag = ""; // 营销工具使用控制

			// -- 签名报文
			String signData = UnionpayConfig.characterSet
					+ UnionpayConfig.callbackUrl + UnionpayConfig.notifyUrl
					+ UnionpayConfig.ipAddress + UnionpayConfig.merchantId
					+ UnionpayConfig.requestId + UnionpayConfig.signType + type
					+ UnionpayConfig.version + amount + bankAbbr + currency
					+ orderDate + orderId + merAcDate + period + periodUnit
					+ merchantAbbr + productDesc + productId + productName
					+ productNum + reserved1 + reserved2 + userToken + showUrl
					+ couponsFlag;

			// 待请求参数数组
			MDataMap queryMap = new MDataMap();
			queryMap.put("total_fee", orderin.get("due_money"));
			// queryMap.put("订单提交日期", orderDate); //订单提交日期
			queryMap.put("service", type);
			queryMap.put("sign_type", UnionpayConfig.signType);
			queryMap.put("partner", UnionpayConfig.merchantId);
			queryMap.put("_input_charset", UnionpayConfig.characterSet);
			queryMap.put("subject", merchantAbbr);
			queryMap.put("payment_type", payType);
			queryMap.put("out_trade_no", orderId);
			queryMap.put("notify_url", UnionpayConfig.notifyUrl);
			requestParamsRecord(queryMap, "和包"); // 记录传递和包支付参数
			// queryMap.put("页面通知地址", UnionpayConfig.callbackUrl); //页面通知地址

			UnionpayHiiposmUtil util = new UnionpayHiiposmUtil();
			// 数据签名
			String hmac = util.MD5Sign(signData, UnionpayConfig.signKey);

			// -- 请求报文
			String buf = "characterSet=" + UnionpayConfig.characterSet
					+ "&callbackUrl=" + UnionpayConfig.callbackUrl
					+ "&notifyUrl=" + UnionpayConfig.notifyUrl + "&ipAddress="
					+ UnionpayConfig.ipAddress + "&merchantId="
					+ UnionpayConfig.merchantId + "&requestId="
					+ UnionpayConfig.requestId + "&signType="
					+ UnionpayConfig.signType + "&type=" + type + "&version="
					+ UnionpayConfig.version + "&amount=" + amount
					+ "&bankAbbr=" + bankAbbr + "&currency=" + currency
					+ "&orderDate=" + orderDate + "&orderId=" + orderId
					+ "&merAcDate=" + merAcDate + "&period=" + period
					+ "&periodUnit=" + periodUnit + "&merchantAbbr="
					+ merchantAbbr + "&productDesc=" + productDesc
					+ "&productId=" + productId + "&productName=" + productName
					+ "&productNum=" + productNum + "" + "&reserved1="
					+ reserved1 + "&reserved2=" + reserved2 + "&userToken="
					+ userToken + "&showUrl=" + showUrl + "&couponsFlag="
					+ couponsFlag;
			// -- 带上消息摘要
			buf = "hmac=" + hmac + "&" + buf;
			// 发起http请求，并获取响应报文
			String res = util.sendAndRecv(UnionpayConfig.req_url, buf,
					UnionpayConfig.characterSet);
			// 获得手机支付平台的消息摘要，用于验签,
			String hmac1 = util.getValue(res, "hmac");
			String vfsign = util.getValue(res, "merchantId")
					+ util.getValue(res, "requestId")
					+ util.getValue(res, "signType")
					+ util.getValue(res, "type")
					+ util.getValue(res, "version")
					+ util.getValue(res, "returnCode")
					+ URLDecoder.decode(util.getValue(res, "message"), "UTF-8")
					+ util.getValue(res, "payUrl");
			// 响应码
			String code = util.getValue(res, "returnCode");
			// 下单交易成功
			if (!code.equals("000000")) {
				return "下单错误:"
						+ code
						+ URLDecoder.decode(util.getValue(res, "message"),
								"UTF-8");
			}
			// -- 验证签名
			boolean flag = false;
			flag = util.MD5Verify(vfsign, hmac1, UnionpayConfig.signKey);
			if (!flag) {
				return "验签失败";
			}
			String payUrl = util.getValue(res, "payUrl");
			String submit_url = util.getRedirectUrl(payUrl);
			//System.out.println("submit_url:" + submit_url);
			requesterURL = buildRequest(submit_url, "post"); // 跳转到和包指定页面
			return requesterURL;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public OcOrderPay resultOcOrderPay(MDataMap insertDatamap, MDataMap orderMap) {
		OcOrderPay pay = new OcOrderPay();
		pay.setOrderCode(insertDatamap.get("out_trade_no"));
		pay.setPaySequenceid(insertDatamap.get("trade_no"));
		pay.setPayedMoney(Float.valueOf(insertDatamap.get("total_fee")));
		pay.setPayType(PAYTYPE);

		pay.setMerchantId(orderMap.get("buyer_code"));
		pay.setPassWord("");
		pay.setPayRemark(insertDatamap.get(""));

		return pay;
	}

	public OcOrderPay resultOcOrderUnionPay(MDataMap insertDatamap) {
		OcOrderPay pay = new OcOrderPay();
		pay.setOrderCode(insertDatamap.get("orderId"));
		pay.setPaySequenceid(insertDatamap.get("payNo"));
		pay.setPayedMoney(Float.parseFloat(insertDatamap.get("amount")));
		pay.setPayType(UNIONPAYTYPE);
		return pay;
	}

	/**
	 * 
	 * @param alipayProcessInput
	 * @param type    1:即时到账接口      2:纯网关接口
	 * @return
	 */
	private MDataMap objectToMap(AlipayProcessInput alipayProcessInput) {
		MDataMap map = new MDataMap();
		map.put("service", alipayProcessInput.getService());
		map.put("partner", alipayProcessInput.getPartner());
		map.put("_input_charset", alipayProcessInput.getInput_charset());
		map.put("payment_type", "1");
		// 返回结果地址
		map.put("notify_url", alipayProcessInput.getNotify_url());

		map.put("out_trade_no", alipayProcessInput.getOut_trade_no());
		map.put("subject", alipayProcessInput.getSubject());

		map.put("total_fee", alipayProcessInput.getTotal_fee());
		
		map.put("seller_email", alipayProcessInput.getSeller_email()); // 卖家支付包账号
		map.put("return_url", alipayProcessInput.getReturn_url());

		map.put("seller_id", alipayProcessInput.getPartner()); //卖家支付宝用户号(用的也是partner)
		
		if("directPay".equals(alipayProcessInput.getPaymethod())){   //支付宝
			map.put("paymethod", "directPay");//creditPay
		}else{   //银联
//			map.put("defaultbank", alipayProcessInput.getDefaultbank());
//			map.put("enable_paymethod", "directPay^bankPay^cartoon^cash");
//			map.put("need_ctu_check", "Y");
			
//			map.put("paymethod", "bankPay");//creditPay
//			map.put("defaultbank", "CMB");
			
			map.put("paymethod", alipayProcessInput.getPaymethod());//creditPay
			map.put("defaultbank", alipayProcessInput.getDefaultbank());
			map.put("extra_common_param", alipayProcessInput.getDefaultbank());//用于返回结果中判断是否是银行卡支付
			
//			map.put("show_url", "");
//			map.put("anti_phishing_key", "");
//			map.put("exter_invoke_ip", "8.8.8.8");
		}

		// 外网IP(还在测试中)
		// map.put("exter_invoke_ip", "124.127.118.106");
		// map.put("price", "10.00");
		// map.put("quantity", "2");

		return map;
	}

	public static void requestParamsRecord(Map<String, String> sPara,
			String state) {
		MDataMap queryMap = new MDataMap();
		if ("支付宝".equals(state)) {
			queryMap.put("sign", sPara.get("sign"));
			queryMap.put("sign_type", sPara.get("sign_type"));
			queryMap.put("service", sPara.get("service"));
			queryMap.put("partner", sPara.get("partner"));
			queryMap.put("input_charset", sPara.get("_input_charset"));
			queryMap.put("subject", sPara.get("subject"));
			queryMap.put("payment_type", sPara.get("payment_type"));
			queryMap.put("total_fee", sPara.get("total_fee"));
			queryMap.put("out_trade_no", sPara.get("out_trade_no"));
			queryMap.put("notify_url", sPara.get("notify_url"));
			queryMap.put("create_time", FormatHelper.upDateTime());
		} else if ("和包".equals(state)) {
			queryMap.put("sign_type", sPara.get("sign_type"));
			queryMap.put("service", sPara.get("service"));
			queryMap.put("partner", sPara.get("partner"));
			queryMap.put("input_charset", sPara.get("_input_charset"));
			queryMap.put("subject", sPara.get("subject"));
			queryMap.put("payment_type", sPara.get("payment_type"));
			queryMap.put("total_fee", sPara.get("total_fee"));
			queryMap.put("out_trade_no", sPara.get("out_trade_no"));
			queryMap.put("notify_url", sPara.get("notify_url"));
			queryMap.put("create_time", FormatHelper.upDateTime());
		}
		String sUid = DbUp.upTable(OC_PAYMENT_PASS).dataInsert(queryMap);
	}

	public static String buildRequest(String url, String strMethod) {
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\""
				+ url + "\" method=\"" + strMethod + "\">");
		sbHtml.append("<input type=\"submit\" value=\"提交\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
		return sbHtml.toString();
	}

	public static void insertOcOrderPayVoid(MDataMap insertDatamap, String mark) {
		//System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		MDataMap orderInfoMap = new MDataMap();
		orderInfoMap.put("order_code", insertDatamap.get("out_trade_no"));
		Map<String, Object> dmap = DbUp.upTable(OC_ORDERINFO).dataSqlOne(
				"select * from oc_orderinfo where order_code=:order_code",
				orderInfoMap);

		MDataMap insertOcOrderPay = new MDataMap();
		insertOcOrderPay.put("order_code", insertDatamap.get("out_trade_no"));
		insertOcOrderPay.put("pay_sequenceid", insertDatamap.get("trade_no"));
		insertOcOrderPay.put("payed_money", insertDatamap.get("total_fee"));
		insertOcOrderPay.put("create_time", FormatHelper.upDateTime());
		// 449746280005
		insertOcOrderPay.put("pay_remark", "");
		//System.out.println("=================mark==============================="+mark);
		if ("001".equals(mark) || "002".equals(mark)) { // 支付宝支付
			//System.out.println("***********************mark****************"+mark);
			insertOcOrderPay.put("pay_type", "449746280003");
			if(StringUtils.isNotBlank(insertDatamap.get("extra_common_param"))) {
				insertOcOrderPay.put("pay_remark", "银行卡支付"); 
			}
		} else if ("100".equals(mark) || "101".equals(mark)) { // 微信支付
			insertOcOrderPay.put("pay_type", "449746280005");
		}
		if (dmap == null || dmap.size() < 1) {
			insertOcOrderPay.put("merchant_id", "");
		} else {
			insertOcOrderPay.put("merchant_id",
					String.valueOf(dmap.get("buyer_code")));
		}
		insertOcOrderPay.put("php_code", insertDatamap.get("seller_email"));
		insertOcOrderPay.put("payed_all_fee", insertDatamap.get("total_fee"));
		insertOcOrderPay.put("payed_fee", "0.00");
		insertOcOrderPay.put("status", "0");
		

		DbUp.upTable("oc_order_pay").dataInsert(insertOcOrderPay); // 插入支付日志流水信息
		//System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
	}
	
	public static void insertOcOrderPayVoidNew(MDataMap insertDatamap, String mark) {
		MDataMap orderInfoMap = new MDataMap();
		orderInfoMap.put("order_code", insertDatamap.get("order_code"));
		Map<String, Object> dmap = DbUp.upTable(OC_ORDERINFO).dataSqlOne(
				"select * from oc_orderinfo where order_code=:order_code",
				orderInfoMap);

		MDataMap insertOcOrderPay = new MDataMap();
		insertOcOrderPay.put("order_code", insertDatamap.get("order_code"));
		insertOcOrderPay.put("pay_sequenceid", insertDatamap.get("trade_no"));
		insertOcOrderPay.put("payed_money", insertDatamap.get("total_fee"));
		insertOcOrderPay.put("create_time", FormatHelper.upDateTime());
		
		//System.out.println("=================mark==============================="+mark);
		if ("001".equals(mark) || "002".equals(mark)) { // 支付宝支付
		//	System.out.println("***********************mark****************"+mark);
			insertOcOrderPay.put("pay_type", "449746280003");
		} else if ("100".equals(mark)) { // 微信支付
			insertOcOrderPay.put("pay_type", "449746280005");
		}
		// 449746280005
		insertOcOrderPay.put("pay_remark", "");
		if (dmap == null || dmap.size() < 1) {
			insertOcOrderPay.put("merchant_id", "");
		} else {
			insertOcOrderPay.put("merchant_id",
					String.valueOf(dmap.get("buyer_code")));
		}
		insertOcOrderPay.put("php_code", insertDatamap.get("seller_email"));
		insertOcOrderPay.put("payed_all_fee", insertDatamap.get("total_fee"));
		insertOcOrderPay.put("payed_fee", "0.00");
		insertOcOrderPay.put("status", "0");
		insertOcOrderPay.put("pay_code", insertDatamap.get("pay_code"));

		DbUp.upTable("oc_order_pay").dataInsert(insertOcOrderPay); // 插入支付日志流水信息

	}
	
}
