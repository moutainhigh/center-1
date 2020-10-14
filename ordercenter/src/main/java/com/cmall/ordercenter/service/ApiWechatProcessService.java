package com.cmall.ordercenter.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.Encoder;
import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;
import com.cmall.ordercenter.alipay.config.ConstantUtil;
import com.cmall.ordercenter.alipay.sign.RSA;
import com.cmall.ordercenter.alipay.util.WXUtil;
import com.cmall.ordercenter.alipay.util.httpClient.ClientResponseHandler;
import com.cmall.ordercenter.alipay.util.httpClient.TenpayHttpClient;
import com.cmall.ordercenter.alipay.wechat.AccessTokenRequestHandler;
import com.cmall.ordercenter.alipay.wechat.ClientRequestHandler;
import com.cmall.ordercenter.alipay.wechat.PackageRequestHandler;
import com.cmall.ordercenter.alipay.wechat.PrepayIdRequestHandler;
import com.cmall.ordercenter.alipay.wechat.RequestHandler;
import com.cmall.ordercenter.alipay.wechat.ResponseHandler;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.PayResult;
import com.cmall.ordercenter.util.DateNewUtils;
import com.cmall.systemcenter.ali.sign.MD5;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.util.AnalysisXmlUtil;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.xmassystem.support.PlusSupportPay;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.support.PlusSupportSystem;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微信支付
 * @author wz
 *
 */
public class ApiWechatProcessService extends BaseClass{
	public static final String OC_ORDER_PAY_PAYTYPE_WECHAT = "449746280005";  //oc_order_pay中记录的微信支付类型
	
	public static final String MOVE_TYPE = "100";  //微信APP
	
	public static final String MOVE_TYPE_SHAPIGOU = "100ShaPiGouAPP";   //沙皮狗微信APP
	 
	public static final String WAP_TYPE = "101";    //微信WAP
	
	public static final String WAP_NEY_TYPE = "102";   //.net微信WAP
	
	private static final String TYPE_APP = "APP";
	
	private static final String TYPE_WAP_NET = "WAP_NET";
	
	private static final String TYPE_WAP_HUIJIAYOU = "WAP_HUIJIAYOU";
	
	private static final String SELLER_CODE_HUIJIAYOU = "SI2003";
	
	private static final String SELLER_CODE_SHAPIGOU = "SI3003";
	
	
	/**
	 * 微信支付
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public MDataMap wechatMovePayment(String orderCode, String ip,RootResult rootResult)
			throws UnsupportedEncodingException {

		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_WEB");

		// ---------------生成订单号 开始------------------------
		// 当前时间 yyyyMMddHHmmss
		// String currTime = TenpayUtil.getCurrTime();
		// // 8位日期
		// String strTime = currTime.substring(8, currTime.length());
		// // 四位随机数
		// String strRandom = TenpayUtil.buildRandom(4) + "";
		// // 10位序列号,可以自行调整。
		// String strReq = strTime + strRandom;
		// // 订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
		//String out_trade_no = orderCode;
		// ---------------生成订单号 结束------------------------

		PackageRequestHandler packageReqHandler = new PackageRequestHandler();// 生成package的请求类
		PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler();// 获取prepayid的请求类
		ClientRequestHandler clientHandler = new ClientRequestHandler();// 返回客户端支付参数的请求类
		packageReqHandler.setKey(bConfig("ordercenter.PARTNER_KEY"));

		int retcode;
		String retmsg = "";
		String xml_body = "";
		MDataMap mapBody = new MDataMap();
		
		// 获取token值
		String token = AccessTokenRequestHandler.getAccessToken();
		// String token = tokenNew;

		if (!"".equals(token)) {
			if (orderCode != null && !"".equals(orderCode)) {
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("order_code", orderCode);
				mDataMap.put("delete_flag", "0");
				mDataMap.put("order_status", "4497153900010001");   //未付款

				// 查询订单相关信息
				Map<String, Object> orderMap = DbUp.upTable("oc_orderinfo")
						.dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status and delete_flag=:delete_flag",mDataMap);
				
				//判断订单信息是否存在
				if (orderMap != null && !"".equals(orderMap)
						&& orderMap.size() > 0) {
					/*
					 *  设置package订单参数
					 */
					packageReqHandler.setParameter("bank_type", "WX");// 银行渠道
					/**
					 * 微信支付商品名称  不能有空格！
					 */
					//String bodyString = URLEncoder.encode(String.valueOf(orderMap.get("product_name")).replaceAll(" ", ""));
					packageReqHandler.setParameter("body", bConfig("ordercenter.PRODUCT_NAME")); // 商品描述
					packageReqHandler.setParameter("notify_url", notify_url); // 接收财付通通知的URL
					packageReqHandler.setParameter("partner",bConfig("ordercenter.PARTNER")); // 商户号
					packageReqHandler.setParameter("out_trade_no", orderCode); // 商家订单号
					//String.valueOf(((BigDecimal)orderMap.get("due_money")).setScale(0,BigDecimal.ROUND_HALF_UP))+"00"
					packageReqHandler.setParameter("total_fee", String.valueOf((((BigDecimal)orderMap.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP))); // 商品金额,以分为单位String.valueOf(orderMap.get("due_money"))
					packageReqHandler.setParameter("spbill_create_ip", ip); // 订单生成的机器IP，指用户浏览器端IP
					packageReqHandler.setParameter("fee_type", "1"); // 币种，1人民币// 66
					packageReqHandler.setParameter("input_charset", "UTF-8"); // 字符编码GBK
						
					// 获取package包
					String packageValue = packageReqHandler.getRequestURL();

					String noncestr = WXUtil.getNonceStr();
					String timestamp = WXUtil.getTimeStamp();
					String traceid = "";

					/*
					 * 设置获取prepayid支付参数
					 */
					prepayReqHandler.setParameter("appid", bConfig("ordercenter.APP_ID"));    
					prepayReqHandler.setParameter("appkey",bConfig("ordercenter.APP_KEY")); // 商户密钥// 缺少
					prepayReqHandler.setParameter("noncestr", noncestr);
					prepayReqHandler.setParameter("package", packageValue);
					prepayReqHandler.setParameter("timestamp", timestamp);
					prepayReqHandler.setParameter("traceid", traceid);
					
					// 生成获取预支付签名
					String sign = prepayReqHandler.createSHA1Sign();
					// 增加非参与签名的额外参数HH20104125HH20104125HH20104125HH20104125HH20104125DD140220100006DD140220100006DD140220100006
					prepayReqHandler.setParameter("app_signature", sign);
					prepayReqHandler.setParameter("sign_method",ConstantUtil.SIGN_METHOD);
					
					String gateUrl = ConstantUtil.GATEURL + token;
					prepayReqHandler.setGateUrl(gateUrl);

					// 获取prepayId
					String prepayid = prepayReqHandler.sendPrepay();

					// 吐回给客户端的参数
					if (null != prepayid && !"".equals(prepayid)) {
						// 输出参数列表
						clientHandler.setParameter("appid", bConfig("ordercenter.APP_ID"));  //商家在微信开放平台申请的应用id
						clientHandler.setParameter("appkey",bConfig("ordercenter.APP_KEY"));
						clientHandler.setParameter("noncestr", noncestr);     /** 随机串，防重发 */
						clientHandler.setParameter("package", "Sign=" + packageValue);
						clientHandler.setParameter("package", "Sign=WXPay");
						clientHandler.setParameter("partnerid",bConfig("ordercenter.PARTNER"));
						clientHandler.setParameter("prepayid", prepayid);
						clientHandler.setParameter("timestamp", timestamp);    /** 时间戳，防重发 */
						// 生成签名
						sign = clientHandler.createSHA1Sign();
						clientHandler.setParameter("sign", sign);
//						String packageString = "out_trade_no="+packageReqHandler.getParameter("out_trade_no")+"&partner="+packageReqHandler.getParameter("partner")+"&key="+packageReqHandler.getKey()+"";
//						clientHandler.setParameter("package", packageString);
						
						mapBody = clientHandler.getMapBody();
						retcode = 0;
						retmsg = "OK";
					} else {
						rootResult.setResultCode(939301222);
						rootResult.setResultMessage("错误：获取prepayId失败！");
					}
				}
			}else{
				rootResult.setResultCode(939301221);
				rootResult.setResultMessage("订单号不能为空！");
			}
		} else {
			rootResult.setResultCode(939301220);
			rootResult.setResultMessage("获取不到Token");
		}
		/**
		 * 打印debug信息
		 */
//		System.out.println("\r\ndebuginfo:\r\n" + new Date());
//		System.out.println(packageReqHandler.getDebugInfo());
//		System.out.println(prepayReqHandler.getDebugInfo());
//		System.out.println(clientHandler.getDebugInfo());
		// out.println("<retcode>" + retcode + "</retcode");
		// out.println("<retmsg>" + retmsg + "<retmsg>");
		if (!"".equals(mapBody) && mapBody!=null && mapBody.size()>0) {
			return mapBody;
		}
		// out.println("</root>");
		return null;

	}
	
	/**
	 * 微信支付APP支付(最新版本)  惠家有
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
	public Map wechatMovePaymentVersionNew(String orderCode, String ip,RootResult rootResult){
		
		List<Map<String, Object>> flashOrder = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> joinOrder = new ArrayList<Map<String, Object>>();
		String time_start = "";
		String time_expire = "";
		
		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_HUIJIAYOU");

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		Map<String,Object> mapOrder = new HashMap<String, Object>(); 
		
		if("DD".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status",
					new MDataMap("order_code",orderCode,"order_status","4497153900010001"));
		}else if("OS".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo_upper").dataSqlOne("select * from oc_orderinfo_upper where big_order_code=:big_order_code and delete_flag=:delete_flag",
					new MDataMap("big_order_code",orderCode,"delete_flag","0"));
			
			//把此订单的支付方式  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
			new PlusSupportPay().fixPayFrom(orderCode, "449746280005");   
		}
		//判断此订单是否存在
		if(mapOrder!=null && !"".equals(mapOrder) && mapOrder.size()>0){
			Map<String,String> map = new HashMap<String,String>();
			map.put("appid", bConfig("ordercenter.APP_ID_HUJIAYOU"));
//			map.put("appid", bConfig("ordercenter.APP_ID"));
//			map.put("attach", "支付   测试");
			map.put("body", "商品");
			map.put("mch_id", bConfig("ordercenter.PARTNER_HUJIAYOU"));
//			map.put("mch_id", bConfig("ordercenter.PARTNER"));
			map.put("nonce_str", noncestr);
//			map.put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
			map.put("notify_url", notify_url);
			
			map.put("out_trade_no", orderCode);
			map.put("spbill_create_ip", ip);
			map.put("total_fee", String.valueOf((((BigDecimal)mapOrder.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP)));
			//map.put("openid", "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
			map.put("trade_type", "APP");
			
			time_start = String.valueOf(mapOrder.get("create_time")).replace("-", "").replace(":", "").replace(" ", "");
			
			int time = getOrderCancelTime(orderCode);
			
			int timeParam = 0;
			
			//为闪够订单
			if(time > 0){   
				
				timeParam = time;
				
				
			}else{      //此处可不加  但害怕有这种情况，此订单不是闪够订单   也不是普通订单，也许是其他情况  也让其24小时后失效
				
				timeParam = 24*60;
				
			}
			
			
			time_expire = DateNewUtils.addMinNew(time_start, timeParam);
			
			map.put("time_start", time_start);
			map.put("time_expire", time_expire);
//			map.put("goods_tag", "WXG");
//			map.put("product_id", "12235413214070356458058");
			
			//获取签名
			String signnew = signVersionNew(map,MOVE_TYPE);
			if(signnew!=null && !"".equals(signnew)){
				map.put("sign", signnew);
				
				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
				
				try {
					URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
			        URLConnection con = url.openConnection();  
			        con.setDoOutput(true);  
			        con.setRequestProperty("Pragma:", "no-cache");  
			        con.setRequestProperty("Cache-Control", "no-cache");  
			        con.setRequestProperty("Content-Type", "text/xml");  

			        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
			        String xmlInfo = clientRequestHandler.getXmlBody(map);
			        //String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
			        //System.out.println(xmlInfo);  
			        //out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
			        out.write(xmlInfo);  
			        out.flush();  
			        out.close();  
			        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			        String linenew = "";
			        String line = null;  
			        //微信返回信息
			        for (line = br.readLine(); line != null; line = br.readLine()) {  
			        	linenew = linenew + line;
			        } 
			        //解析xml
			        Map wechatMoveMap = AnalysisXmlUtil.readStringXmlOut(linenew);
			        if(wechatMoveMap!=null && !"".equals(wechatMoveMap) && wechatMoveMap.size()>0){
			        	wechatMoveMap.put("timestamp", timestamp);   //时间戳
			        	
			        	Map mm = new HashMap();
			        	mm.put("appid", String.valueOf(wechatMoveMap.get("appid")));
			        	mm.put("noncestr", String.valueOf(wechatMoveMap.get("nonce_str")));
			        	mm.put("package", "Sign=WXPay");
			        	mm.put("partnerid", String.valueOf(wechatMoveMap.get("mch_id")));
			        	mm.put("prepayid", String.valueOf(wechatMoveMap.get("prepay_id")));
			        	mm.put("timestamp", timestamp);
			        	
			        	//生成客户端唤起微信支付签名
			        	String sign = genAppSign(mm,"100");
			        	
			        	if(sign!=null && !"".equals(sign)){
			        		wechatMoveMap.put("sign", sign);
			        		return wechatMoveMap;
			        	}else{
			        		rootResult.setResultMessage("生成唤起微信支付的签名失败!");
			        	}
			        }else{
			        	rootResult.setResultCode(939301401);
						rootResult.setResultMessage("解析微信支付返回的xml失败!");
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				rootResult.setResultCode(939301400);
				rootResult.setResultMessage("签名不能为空");
			}
			
		
		}else{
			rootResult.setResultCode(939301200);
			rootResult.setResultMessage("订单编号不存在");
		}

		return null;
	}
	
	/**
	 * 微信APP支付(沙皮狗)
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
	public Map wechatMovePaymentVersionShaPIGou(String orderCode, String ip,RootResult rootResult){
		
		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_HUIJIAYOU");

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		Map<String,Object> mapOrder = new HashMap<String, Object>(); 
		
		if("DD".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status",
					new MDataMap("order_code",orderCode,"order_status","4497153900010001"));
		}else if("OS".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo_upper").dataSqlOne("select * from oc_orderinfo_upper where big_order_code=:big_order_code and delete_flag=:delete_flag",
					new MDataMap("big_order_code",orderCode,"delete_flag","0"));
			
			//把此订单的支付状态  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
			new PlusSupportPay().fixPayFrom(orderCode, "449746280005");   
		}
		//判断此订单是否存在
		if(mapOrder!=null && !"".equals(mapOrder) && mapOrder.size()>0){
			Map<String,String> map = new HashMap<String,String>();
			map.put("appid", bConfig("ordercenter.APP_ID_SHAPIGOU"));
//			map.put("appid", bConfig("ordercenter.APP_ID"));
//			map.put("attach", "支付   测试");
			map.put("body", "商品");
			map.put("mch_id", bConfig("ordercenter.PARTNER_SHAPIGOU"));
//			map.put("mch_id", bConfig("ordercenter.PARTNER"));
			map.put("nonce_str", noncestr);
//			map.put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
			map.put("notify_url", notify_url);
			
			map.put("out_trade_no", orderCode);
			map.put("spbill_create_ip", ip);
			map.put("total_fee", String.valueOf((((BigDecimal)mapOrder.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP)));
			//map.put("openid", "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
			map.put("trade_type", "APP");
			
			String time_start = String.valueOf(mapOrder.get("create_time")).replace("-", "").replace(":", "").replace(" ", "");
			
			int time = getOrderCancelTime(orderCode);
			
			int timeParam = 0;
			
			//为闪够订单
			if(time > 0){   
				
				timeParam = time;
				
				
			}else{      //此处可不加  但害怕有这种情况，此订单不是闪够订单   也不是普通订单，也许是其他情况  也让其24小时后失效
				
				timeParam = 24*60;
				
			}
			
			
			String time_expire = DateNewUtils.addMinNew(time_start, timeParam);
			
			map.put("time_start", time_start);
			map.put("time_expire", time_expire);
			
			//获取签名
			String signnew = signVersionNew(map,MOVE_TYPE_SHAPIGOU);
			if(signnew!=null && !"".equals(signnew)){
				map.put("sign", signnew);
				
				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
				
				try {
					URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
			        URLConnection con = url.openConnection();  
			        con.setDoOutput(true);  
			        con.setRequestProperty("Pragma:", "no-cache");  
			        con.setRequestProperty("Cache-Control", "no-cache");  
			        con.setRequestProperty("Content-Type", "text/xml");  

			        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
			        String xmlInfo = clientRequestHandler.getXmlBody(map);
			        //String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
			        //System.out.println(xmlInfo);  
			        //out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
			        out.write(xmlInfo);  
			        out.flush();  
			        out.close();  
			        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			        String linenew = "";
			        String line = null;  
			        //微信返回信息
			        for (line = br.readLine(); line != null; line = br.readLine()) {  
			        	linenew = linenew + line;
			        } 
			        //解析xml
			        Map wechatMoveMap = AnalysisXmlUtil.readStringXmlOut(linenew);
			        if(wechatMoveMap!=null && !"".equals(wechatMoveMap) && wechatMoveMap.size()>0){
			        	wechatMoveMap.put("timestamp", timestamp);   //时间戳
			        	
			        	Map mm = new HashMap();
			        	mm.put("appid", String.valueOf(wechatMoveMap.get("appid")));
			        	mm.put("noncestr", String.valueOf(wechatMoveMap.get("nonce_str")));
			        	mm.put("package", "Sign=WXPay");
			        	mm.put("partnerid", String.valueOf(wechatMoveMap.get("mch_id")));
			        	mm.put("prepayid", String.valueOf(wechatMoveMap.get("prepay_id")));
			        	mm.put("timestamp", timestamp);
			        	
			        	//生成客户端唤起微信支付签名
			        	String sign = genAppSign(mm,MOVE_TYPE_SHAPIGOU);
			        	
			        	if(sign!=null && !"".equals(sign)){
			        		wechatMoveMap.put("sign", sign);
			        		return wechatMoveMap;
			        	}else{
			        		rootResult.setResultMessage("生成唤起微信支付的签名失败!");
			        	}
			        }else{
			        	rootResult.setResultCode(939301401);
						rootResult.setResultMessage("解析微信支付返回的xml失败!");
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				rootResult.setResultCode(939301400);
				rootResult.setResultMessage("签名不能为空");
			}
			
		
		}else{
			rootResult.setResultCode(939301200);
			rootResult.setResultMessage("订单编号不存在");
		}

		return null;
	
		
	
	}
	
	public static void main(String[] args) {
		RootResult rootResult = new RootResult();
		ApiWechatProcessService apiWechatProcessService = new ApiWechatProcessService();
		apiWechatProcessService.wechatMovePaymentVersionNew("OS148468103", "8.8.8.8", rootResult);
		
	}
	/**
	 * 微信JSAPI支付(最新版本(手机端嵌入网页))
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
	public Map wechatMovePaymentWapVersionNew(String orderCode, String ip,RootResult rootResult,String openId){
		System.out.println("==================wechatMovePaymentWapVersionNew========================");
		
		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_HUIJIAYOU_WAP");

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		Map<String,Object> mapOrder = new HashMap<String, Object>(); 
		
		if("DD".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status",
					new MDataMap("order_code",orderCode,"order_status","4497153900010001"));
		}else if("OS".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo_upper").dataSqlOne("select * from oc_orderinfo_upper where big_order_code=:big_order_code and delete_flag=:delete_flag",
					new MDataMap("big_order_code",orderCode,"delete_flag","0"));
			//把此订单的支付状态  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
			new PlusSupportPay().fixPayFrom(orderCode, "449746280005");  
		}
		//判断此订单是否存在
		if(mapOrder!=null && !"".equals(mapOrder) && mapOrder.size()>0){
			Map<String,String> map = new HashMap<String,String>();
			if(SELLER_CODE_HUIJIAYOU.equals(mapOrder.get("seller_code"))){
				map.put("appid", bConfig("ordercenter.APP_ID_HUJIAYOU_WAP"));
				map.put("mch_id", bConfig("ordercenter.PARTNER_HUJIAYOU_WAP"));
			
			}else if(SELLER_CODE_SHAPIGOU.equals(mapOrder.get("seller_code"))){
				map.put("appid", bConfig("ordercenter.APP_ID_SHAPIGOU_WAP"));
				map.put("mch_id", bConfig("ordercenter.PARTNER_SHAPIGOU_WAP"));
				
				System.out.println("appid===================="+map.get("appid"));
				System.out.println("mch_id===================="+map.get("mch_id"));
//				map.put("appid", bConfig("ordercenter.APP_ID_NET_WAP"));
//				map.put("mch_id", bConfig("ordercenter.PARTNER_NET_WAP"));
			}
//			map.put("appid", bConfig("ordercenter.APP_ID_NET_WAP"));
//			map.put("mch_id", bConfig("ordercenter.PARTNER_NET_WAP"));
			
//			map.put("appid", "wx2421b1c4370ec43b");
			
//			map.put("appid", bConfig("ordercenter.APP_ID"));
//			map.put("attach", "支付   测试");
			map.put("body", "商品");
			
			
//			map.put("mch_id", "10000100");
			
			
//			map.put("mch_id", bConfig("ordercenter.PARTNER"));
			map.put("nonce_str", noncestr);
//			map.put("nonce_str", "1add1a30ac87aa2db72f57a2375d8fec");
			
			
//			map.put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
			map.put("notify_url", notify_url);
			
			map.put("out_trade_no", orderCode);
			map.put("spbill_create_ip", ip);
			
			map.put("total_fee", String.valueOf((((BigDecimal)mapOrder.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP)));
			//map.put("openid", "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
			map.put("trade_type", "JSAPI");
			map.put("openid", openId);
			
			
//			map.put("device_info", "013467007045764");
//			map.put("detail", "Ipadmini");
//			map.put("fee_type", "CNY");
//			map.put("time_start", "20091225091010");
//			map.put("time_expire", "20091227091010");
//			map.put("goods_tag", "WXG");
//			map.put("product_id", "12235413214070356458058");
			
			String signnew = "";
			//获取签名
			if(SELLER_CODE_HUIJIAYOU.equals(mapOrder.get("seller_code"))){
				signnew = signVersionNew(map,WAP_TYPE);   //惠家有
			}else if(SELLER_CODE_SHAPIGOU.equals(mapOrder.get("seller_code"))){
				signnew = signVersionNew(map,MOVE_TYPE_SHAPIGOU);   //沙皮狗
			}
			
			//String signnew = signVersionNew(map,WAP_TYPE);   正式
			if(signnew!=null && !"".equals(signnew)){
				map.put("sign", signnew);
				
				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
				
				try {
					URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
			        URLConnection con = url.openConnection();  
			        con.setDoOutput(true);  
			        con.setRequestProperty("Pragma:", "no-cache");  
			        con.setRequestProperty("Cache-Control", "no-cache");  
			        con.setRequestProperty("Content-Type", "text/xml");  
			        
			        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
			        String xmlInfo = clientRequestHandler.getXmlBody(map);
			        //String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
			        //out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
			        out.write(xmlInfo);  
			        out.flush();  
			        out.close();  
			        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			        String linenew = "";
			        String line = null;  
			        //微信返回信息
			        for (line = br.readLine(); line != null; line = br.readLine()) {  
			        	linenew = linenew + line;
			        } 
			        System.out.println("=============================="+linenew);
			       // System.out.println(linenew);
			        //解析xml
			        Map wechatMoveMap = AnalysisXmlUtil.readStringXmlOut(linenew);
			        if(wechatMoveMap!=null && !"".equals(wechatMoveMap) && wechatMoveMap.size()>0){
			        	wechatMoveMap.put("timestamp", timestamp);   //时间戳
			        	
			        	/**
			        	 * wap验签的时候只需要以下5个参数，并且区分大小。
			        	 * 这是一个神坑，参数竟然和官网上的不一样
			        	 */
			        	Map mm = new HashMap();
			        	mm.put("appId", String.valueOf(wechatMoveMap.get("appid")));
			        	mm.put("timeStamp", timestamp);
			        	mm.put("nonceStr", String.valueOf(wechatMoveMap.get("nonce_str")));
			        	mm.put("package", "prepay_id="+String.valueOf(wechatMoveMap.get("prepay_id")));
//			        	mm.put("mch_id", String.valueOf(wechatMoveMap.get("mch_id")));
			        	mm.put("signType", "MD5");
			        	
			        	//mm.put("package", "Sign=WXPay");
			        	//mm.put("partnerid", String.valueOf(wechatMoveMap.get("mch_id")));
//			        	mm.put("prepayid", String.valueOf(wechatMoveMap.get("prepay_id")));
//			        	mm.put("trade_type", String.valueOf(wechatMoveMap.get("trade_type")));
			        	
			        	String sign = "";
			        	//生成客户端唤起微信支付签名
			        	if(SELLER_CODE_HUIJIAYOU.equals(mapOrder.get("seller_code"))){
			        		sign = genAppSign(mm,WAP_TYPE);
			        	}else if(SELLER_CODE_SHAPIGOU.equals(mapOrder.get("seller_code"))){
			        		sign = genAppSign(mm,MOVE_TYPE_SHAPIGOU);
			        	}
			        	
			        	if(sign!=null && !"".equals(sign)){
			        		wechatMoveMap.put("sign", sign);
			        		return wechatMoveMap;
			        	}else{
			        		rootResult.setResultMessage("生成唤起微信支付的签名失败!");
			        	}
			        }else{
			        	rootResult.setResultCode(939301401);
						rootResult.setResultMessage("解析微信支付返回的xml失败!");
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				rootResult.setResultCode(939301400);
				rootResult.setResultMessage("签名不能为空");
			}
			
		
		}else{
			rootResult.setResultCode(939301200);
			rootResult.setResultMessage("订单编号不存在");
		}

		return null;
	}
	
	/**
	 * 微信扫码支付NATIVE 支付
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
	public Map wechatPcPaymentNATIVE(String orderCode, String ip,RootResult rootResult){

		
		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_HUIJIAYOU");

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		Map<String,Object> mapOrder = new HashMap<String, Object>(); 
		
		if("DD".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status",
					new MDataMap("order_code",orderCode,"order_status","4497153900010001"));
		}else if("OS".equals(orderCode.substring(0, 2))){
			mapOrder = DbUp.upTable("oc_orderinfo_upper").dataSqlOne("select * from oc_orderinfo_upper where big_order_code=:big_order_code and delete_flag=:delete_flag",
					new MDataMap("big_order_code",orderCode,"delete_flag","0"));
			
			//把此订单的支付状态  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
			new PlusSupportPay().fixPayFrom(orderCode, "449746280005");   
		}
		//判断此订单是否存在
		if(mapOrder!=null && !"".equals(mapOrder) && mapOrder.size()>0){
			Map<String,String> map = new HashMap<String,String>();
			map.put("appid", bConfig("ordercenter.APP_ID_HUJIAYOU_WAP"));
			map.put("mch_id", bConfig("ordercenter.PARTNER_HUJIAYOU_WAP"));
			map.put("nonce_str", noncestr);
			map.put("body", "商品");
			map.put("out_trade_no", orderCode);
			map.put("total_fee", String.valueOf((((BigDecimal)mapOrder.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP)));
			map.put("spbill_create_ip", "8.8.8.8");
			map.put("notify_url", notify_url);
			map.put("trade_type", "NATIVE");
			

			
			//获取签名
			String signnew = signVersionNew(map,WAP_TYPE);
			if(signnew!=null && !"".equals(signnew)){
				map.put("sign", signnew);
				
				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
				
				try {
					URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
			        URLConnection con = url.openConnection();  
			        con.setDoOutput(true);  
			        con.setRequestProperty("Pragma:", "no-cache");  
			        con.setRequestProperty("Cache-Control", "no-cache");  
			        con.setRequestProperty("Content-Type", "text/xml");  
			        
			        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
			        String xmlInfo = clientRequestHandler.getXmlBody(map);
			        //String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
			        //out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
			        out.write(xmlInfo);  
			        out.flush();  
			        out.close();  
			        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			        String linenew = "";
			        String line = null;  
			        //微信返回信息
			        for (line = br.readLine(); line != null; line = br.readLine()) {  
			        	linenew = linenew + line;
			        } 
			        
					
			        //解析xml
			        Map wechatMoveMap = AnalysisXmlUtil.readStringXmlOut(linenew);
			        if(wechatMoveMap!=null && !"".equals(wechatMoveMap) && wechatMoveMap.size()>0){
			        	/*
						 * 获取二维码连接 
						 */
						PlusSupportSystem plusSupport = new PlusSupportSystem();
						String qrCodeSrc = plusSupport.upQrCode(String.valueOf(wechatMoveMap.get("code_url")),500);
						
			        	wechatMoveMap.put("payUrl", qrCodeSrc);
			        	return wechatMoveMap;
			        }else{
			        	rootResult.setResultCode(939301401);
						rootResult.setResultMessage("解析微信支付返回的xml失败!");
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				rootResult.setResultCode(939301400);
				rootResult.setResultMessage("签名不能为空");
			}
			
		
		}else{
			rootResult.setResultCode(939301200);
			rootResult.setResultMessage("订单编号不存在");
		}

		return null;
	
	}
	
	/**
	 * 微信WAP   .net
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @param openId
	 * @return
	 */
//	public Map wechatMovePaymentWapVersionNewNet(String orderCode, String ip,RootResult rootResult,String openId){
//		
//		
//		// 接收财付通通知的URL
//		String notify_url = bConfig("ordercenter.NOTIFY_URL_HUIJIAYOU_WAP");
//
//		String noncestr = WXUtil.getNonceStr();
//		String timestamp = WXUtil.getTimeStamp();
//		Map<String,Object> mapOrder = new HashMap<String, Object>(); 
//		
//		if("DD".equals(orderCode.substring(0, 2))){
//			mapOrder = DbUp.upTable("oc_orderinfo").dataSqlOne("select * from oc_orderinfo where order_code=:order_code and order_status=:order_status",
//					new MDataMap("order_code",orderCode,"order_status","4497153900010001"));
//		}else if("OS".equals(orderCode.substring(0, 2))){
//			mapOrder = DbUp.upTable("oc_orderinfo_upper").dataSqlOne("select * from oc_orderinfo_upper where big_order_code=:big_order_code and delete_flag=:delete_flag",
//					new MDataMap("big_order_code",orderCode,"delete_flag","0"));
//			//把此订单的支付状态  放入缓存中,  目的是为了在详情页中查询此订单的支付方式
//			new PlusSupportPay().fixPayFrom(orderCode, "449746280005");  
//		}
//		//判断此订单是否存在
//		if(mapOrder!=null && !"".equals(mapOrder) && mapOrder.size()>0){
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("appid", bConfig("ordercenter.APP_ID_NET_WAP"));
////			map.put("appid", "wx2421b1c4370ec43b");
//			
////			map.put("appid", bConfig("ordercenter.APP_ID"));
////			map.put("attach", "支付   测试");
//			map.put("body", "商品");
//			
//			
//			map.put("mch_id", bConfig("ordercenter.PARTNER_NET_WAP"));
////			map.put("mch_id", "10000100");
//			
//			
////			map.put("mch_id", bConfig("ordercenter.PARTNER"));
//			map.put("nonce_str", noncestr);
////			map.put("nonce_str", "1add1a30ac87aa2db72f57a2375d8fec");
//			
//			
////			map.put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
//			map.put("notify_url", notify_url);
//			
//			map.put("out_trade_no", orderCode);
//			map.put("spbill_create_ip", ip);
//			
//			map.put("total_fee", String.valueOf((((BigDecimal)mapOrder.get("due_money")).multiply(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_HALF_UP)));
//			//map.put("openid", "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
//			map.put("trade_type", "JSAPI");
//			map.put("openid", openId);
//			
//			
////			map.put("device_info", "013467007045764");
////			map.put("detail", "Ipadmini");
////			map.put("fee_type", "CNY");
////			map.put("time_start", "20091225091010");
////			map.put("time_expire", "20091227091010");
////			map.put("goods_tag", "WXG");
////			map.put("product_id", "12235413214070356458058");
//			
//			//获取签名
//			String signnew = signVersionNew(map,WAP_NEY_TYPE);
//			if(signnew!=null && !"".equals(signnew)){
//				map.put("sign", signnew);
//				
//				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
//				
//				try {
//					URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
//			        URLConnection con = url.openConnection();  
//			        con.setDoOutput(true);  
//			        con.setRequestProperty("Pragma:", "no-cache");  
//			        con.setRequestProperty("Cache-Control", "no-cache");  
//			        con.setRequestProperty("Content-Type", "text/xml");  
//			        
//			        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
//			        String xmlInfo = clientRequestHandler.getXmlBody(map);
//			        //String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
//			        //out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
//			        out.write(xmlInfo);  
//			        out.flush();  
//			        out.close();  
//			        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
//			        String linenew = "";
//			        String line = null;  
//			        //微信返回信息
//			        for (line = br.readLine(); line != null; line = br.readLine()) {  
//			        	linenew = linenew + line;
//			        } 
//			       // System.out.println(linenew);
//			        //解析xml
//			        Map wechatMoveMap = AnalysisXmlUtil.readStringXmlOut(linenew);
//			        if(wechatMoveMap!=null && !"".equals(wechatMoveMap) && wechatMoveMap.size()>0){
//			        	wechatMoveMap.put("timestamp", timestamp);   //时间戳
//			        	
//			        	/**
//			        	 * wap验签的时候只需要以下5个参数，并且区分大小。
//			        	 * 这是一个神坑，参数竟然和官网上的不一样
//			        	 */
//			        	Map mm = new HashMap();
//			        	mm.put("appId", String.valueOf(wechatMoveMap.get("appid")));
//			        	mm.put("timeStamp", timestamp);
//			        	mm.put("nonceStr", String.valueOf(wechatMoveMap.get("nonce_str")));
//			        	mm.put("package", "prepay_id="+String.valueOf(wechatMoveMap.get("prepay_id")));
////			        	mm.put("mch_id", String.valueOf(wechatMoveMap.get("mch_id")));
//			        	mm.put("signType", "MD5");
//			        	
//			        	//mm.put("package", "Sign=WXPay");
//			        	//mm.put("partnerid", String.valueOf(wechatMoveMap.get("mch_id")));
////			        	mm.put("prepayid", String.valueOf(wechatMoveMap.get("prepay_id")));
////			        	mm.put("trade_type", String.valueOf(wechatMoveMap.get("trade_type")));
//			        	
//			        	
//			        	//生成客户端唤起微信支付签名
//			        	String sign = genAppSign(mm,WAP_NEY_TYPE);
//			        	
//			        	if(sign!=null && !"".equals(sign)){
//			        		wechatMoveMap.put("sign", sign);
//			        		return wechatMoveMap;
//			        	}else{
//			        		rootResult.setResultMessage("生成唤起微信支付的签名失败!");
//			        	}
//			        }else{
//			        	rootResult.setResultCode(939301401);
//						rootResult.setResultMessage("解析微信支付返回的xml失败!");
//			        }
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}else{
//				rootResult.setResultCode(939301400);
//				rootResult.setResultMessage("签名不能为空");
//			}
//			
//		
//		}else{
//			rootResult.setResultCode(939301200);
//			rootResult.setResultMessage("订单编号不存在");
//		}
//
//		return null;
//	
//		
//	}
	
//	public static void main(String[] args) {
//		RootResult rootResult = new RootResult();
////		String aa = URLEncoder.encode("http://qhbeta-cfamily.qhw.srnpr.com/cfamily/manage/wechatWAP_response.ftl");
//		ApiWechatProcessService apiWechatProcessService = new ApiWechatProcessService();
//		apiWechatProcessService.wechatMovePaymentVersionShaPIGou("OS150824100003", "8.8.8.8", rootResult);
//		//System.out.println(aa);
//		
//	}
	
	public String genAppSign(Map map,String type){ 
		StringBuilder sb = new StringBuilder();
		StringBuffer str = new StringBuffer();
		String stringSignTemp = "";
		
		List<String> list = new ArrayList<String>();

		for(Object oKey : map.keySet()){
			if(map.get(oKey.toString())!=null && !"".equals(map.get(oKey.toString()))){
				list.add(oKey.toString() + "="
						+ map.get(oKey.toString()));
			}
		}
		Collections.sort(list); // 对List内容进行排序

		for (String nameString : list) {
			str.append(nameString + "&");
		}
		
		//System.out.println("---------------------------"+str);
		if("100".equals(type)){  //APP微信支付
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU");
		}else if("101".equals(type)){   //WAP微信支付
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU_WAP");
		}else if("102".equals(type)){   //.net微信支付WAP
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_NET_WAP");
		}else if("100ShaPiGouAPP".equals(type)){   //APP微信支付(沙皮狗)
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_SHAPIGOU");
		}
		
		
		//System.out.println("=========================="+stringSignTemp);
		String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(stringSignTemp, "UTF8").toUpperCase();
		//System.out.println("ccccccccccccccccccccccccccccccccccccccccccc");
		return sign;
	}
	
	
	
	/**
	 * 微信支付最新版本回调
	 * @param request
	 * @return
	 */
	public PayResult responseWechatMoveVersionNewService(Map wechatMoveParamsMap,String mark){
		PayResult payResult = new PayResult();
		try {
			//系统自定义的支付编号
			String sPayCode = WebHelper.upCode("PM");  
			//String mark = "100";    //移动微信支付
			
			//---------------------------------------------------------
	    	//财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
	    	//---------------------------------------------------------
	    	//商户号
//			String partner = "";
//			//密钥
//			String key = "";
//			
//			if(TYPE_APP.equals(type)){
//				partner = bConfig("ordercenter.APP_ID_HUJIAYOU");
//				key = bConfig("ordercenter.PARTNER_KEY_HUJIAYOU"); 
//			}else if(TYPE_WAP_HUIJIAYOU.equals(type)){
//				partner = bConfig("ordercenter.APP_ID_HUJIAYOU_WAP");
//				key = bConfig("ordercenter.PARTNER_KEY_HUJIAYOU_WAP"); 
//			}else if(TYPE_WAP_NET.equals(type)){
//				partner = bConfig("ordercenter.APP_ID_NET_WAP");
//				key = bConfig("ordercenter.PARTNER_KEY_NET_WAP"); 
//			}
	    	//String partner = bConfig("ordercenter.APP_ID_HUJIAYOU");
	    	//密钥
//	    	String key = bConfig("ordercenter.PARTNER_KEY_HUJIAYOU");   
	    	
	    	// 插入日志流水
			if (payResult.upFlagTrue()) {
				 // 插入支付日志流水信息
				OcPaymentWechatService ocPaymentWechatService = new OcPaymentWechatService();
				ocPaymentWechatService.insertOcPaymentWechatNew(wechatMoveParamsMap, sPayCode, mark);
			}
			
			MDataMap dm = null;
			
			String orderCode = String.valueOf(wechatMoveParamsMap.get("out_trade_no"));
			
			if (payResult.upFlagTrue()) {
				
				if("SUCCESS".equals(wechatMoveParamsMap.get("return_code"))){
					
					if ("DD".equals(orderCode.substring(0,2))) {
						dm = DbUp.upTable("oc_orderinfo").one("order_code", orderCode); // 查询订单

					} else if ("OS".equals(orderCode.substring(0,2))) {
						dm = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",orderCode); // 查询订单
					}
					//String dueMoney = String.valueOf(((new BigDecimal(String.valueOf(wechatMoveParamsMap.get("total_fee")))).multiply(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_HALF_UP));
					String dueMoney = String.valueOf(((new BigDecimal(String.valueOf(wechatMoveParamsMap.get("total_fee")))).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
					
					// 判断金额是否和应付一致
					if (dm!=null && !"".equals(dm) && dm.get("due_money").equals(dueMoney)) {
						// 定义即将锁定的编号
						String sLockKey = orderCode;

						// 获取锁定唯一约束值
						String sLockUuid = WebHelper.addLock(60,sLockKey);

						// 开始锁定交易的流水号和交易类型60秒
						if (StringUtils.isNotEmpty(sLockUuid)) {
							AlipayProcessService alipayProcessService = new AlipayProcessService();
							
							if("DD".equals(orderCode.substring(0,2))){
								// 判断是否有成功支付流水的记录
								if (DbUp.upTable("oc_order_pay").count("order_code",dm.get("order_code"),"pay_type",OC_ORDER_PAY_PAYTYPE_WECHAT) == 0) {
									MDataMap map = new MDataMap();
									map.put("out_trade_no", orderCode);
									map.put("trade_no", String.valueOf(wechatMoveParamsMap.get("transaction_id")));
									map.put("total_fee", dueMoney);
									//微信支付分配的商户号
									map.put("seller_email", String.valueOf(wechatMoveParamsMap.get("mch_id")));

									
									alipayProcessService.insertOcOrderPayVoid(map,mark); // 插入数据
									
									// 查询大订单
									MDataMap bigOrderMap = DbUp.upTable("oc_orderinfo_upper").one("big_order_code",dm.get("big_order_code"));
									if (bigOrderMap != null) {
										// 已付的款
										BigDecimal payed_money = new BigDecimal(bigOrderMap.get("payed_money")).add(new BigDecimal(dueMoney));
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
							}else if("OS".equals(orderCode.substring(0, 2))){
								List<MDataMap> list =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", new MDataMap("big_order_code",dm.get("big_order_code")));
								
								for(MDataMap orderInfoMap : list){
									if (DbUp.upTable("oc_order_pay").count("order_code",orderInfoMap.get("order_code"),"pay_type",OC_ORDER_PAY_PAYTYPE_WECHAT) == 0) {
										
										MDataMap map = new MDataMap();
										map.put("out_trade_no", orderInfoMap.get("order_code"));
										map.put("trade_no", String.valueOf(wechatMoveParamsMap.get("transaction_id")));
										map.put("total_fee", orderInfoMap.get("due_money"));
										//微信支付分配的商户号
										map.put("seller_email", String.valueOf(wechatMoveParamsMap.get("mch_id")));
										alipayProcessService.insertOcOrderPayVoid(map,mark); // 插入数据
									} else {
										payResult.inErrorMessage(939301205);
									}
								}
								// 已付的款
								BigDecimal payed_money = new BigDecimal(dm.get("payed_money")).add(new BigDecimal(dueMoney));
								MDataMap updateDataBigMap = new MDataMap();
								updateDataBigMap.put("payed_money",String.valueOf(payed_money));
								updateDataBigMap.put("due_money","0.00");
								updateDataBigMap.put("update_time",FormatHelper.upDateTime());
								updateDataBigMap.put("big_order_code",dm.get("big_order_code"));
								// 更新大订单表
								DbUp.upTable("oc_orderinfo_upper").dataUpdate(updateDataBigMap,"payed_money,due_money,update_time","big_order_code");
								
								
								//此订单如果是惠家有的订单，需要将大订单下的所有小订单，payed_money字段更新成due_money字段的值
								if(AppConst.MANAGE_CODE_HOMEHAS.equals(dm.get("seller_code")) || StringUtils.equals(AppConst.MANAGE_CODE_CDOG, dm.get("seller_code"))){
									List<Map<String, Object>> infoList = DbUp.upTable("oc_orderinfo").dataSqlList("select * from oc_orderinfo where big_order_code = '"+dm.get("big_order_code")+"'", new MDataMap());
									
									for(Map<String, Object> infoMap : infoList){
										DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("payed_money",String.valueOf(infoMap.get("due_money")),"order_code",String.valueOf(infoMap.get("order_code"))), "payed_money", "order_code");
									}
								}
								payResult.setOrderCode(dm.get("big_order_code"));
							}
							WebHelper.unLock(sLockUuid);
						
							
						}
					}else{
						payResult.inErrorMessage(939301203);
					}
					
				}else{
					payResult.inErrorMessage(939302103);
				}
				//处理业务完毕(返回给微信支付成功信息)
	    		MDataMap mDataMap = new MDataMap();
	    		mDataMap.put("payment_code", sPayCode);
	    		mDataMap.put("process_result", payResult.upJson());
	    		mDataMap.put("process_time", FormatHelper.upDateTime());
	    		mDataMap.put("flag_success", payResult.upFlagTrue() ? "1" : "0");
	    		DbUp.upTable("oc_payment_wechatNew").dataUpdate(mDataMap, "process_result,process_time,flag_success", "payment_code");
				//resHandler.sendToCFT("Success");
			}

		}catch(Exception e){
			e.printStackTrace();
		}
				    	
		return payResult;
	
	}
	/**
	 * 微信支付生成验签(腾讯最新版本，以后一直使用)
	 * 
	 * @param map: 验签参数
	 * @return
	 */
	public String signVersionNew(Map<String,String> map,String type){
		
		StringBuffer str = new StringBuffer();
		String stringSignTemp = "";
		List<String> list = new ArrayList<String>();

		for(Object oKey : map.keySet()){
			if(map.get(oKey.toString())!=null && !"".equals(map.get(oKey.toString()))){
				list.add(oKey.toString() + "="
						+ map.get(oKey.toString()));
			}
		}
		Collections.sort(list); // 对List内容进行排序

		for (String nameString : list) {
			str.append(nameString + "&");
		}
		
		if("100".equals(type)){   // 微信移动支付  验签
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU");
		}else if("101".equals(type)){   //微信JSAPI、NATIVE支付验签
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU_WAP");
		}else if("102".equals(type)){    //微信WAP支付  .net 测试
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_NET_WAP");
		}else if("100ShaPiGouAPP".equals(type)){
			stringSignTemp = str.substring(0, str.toString().length()) + "key="+ bConfig("ordercenter.PARTNER_KEY_SHAPIGOU");
		}
		String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(stringSignTemp, "UTF8").toUpperCase();
		
		//String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(stringSignTemp.toUpperCase(), "UTF8");
		
		return sign;
	}
	
	public String signVersionReponseNew(Map<String,String> map){

		StringBuffer str = new StringBuffer();
		String stringSignTemp = "";
		List<String> list = new ArrayList<String>();

		for(Object oKey : map.keySet()){
			if(map.get(oKey.toString())!=null && !"".equals(map.get(oKey.toString()))){
				list.add(oKey.toString() + "="
						+ map.get(oKey.toString()));
			}
		}
		Collections.sort(list); // 对List内容进行排序

		for (String nameString : list) {
			str.append(nameString + "&");
		}

		stringSignTemp = str.substring(0, str.toString().length()-1);
		
		String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(stringSignTemp, "UTF-8").toUpperCase();
		
		return sign;
	
		
	}


	

	/**
	 * 微信支付最新
	 * @author wz
	 *
	 */
	public MDataMap wechatMovePaymentNew(String orderPayCode, String ip,RootResult rootResult)
			throws UnsupportedEncodingException {

		// 接收财付通通知的URL
		String notify_url = bConfig("ordercenter.NOTIFY_URL_WEB");

		// ---------------生成订单号 开始------------------------
		// 当前时间 yyyyMMddHHmmss
		// String currTime = TenpayUtil.getCurrTime();
		// // 8位日期
		// String strTime = currTime.substring(8, currTime.length());
		// // 四位随机数
		// String strRandom = TenpayUtil.buildRandom(4) + "";
		// // 10位序列号,可以自行调整。
		// String strReq = strTime + strRandom;
		// // 订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
		//String out_trade_no = orderCode;
		// ---------------生成订单号 结束------------------------

		PackageRequestHandler packageReqHandler = new PackageRequestHandler();// 生成package的请求类
		PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler();// 获取prepayid的请求类
		ClientRequestHandler clientHandler = new ClientRequestHandler();// 返回客户端支付参数的请求类
		packageReqHandler.setKey(bConfig("ordercenter.PARTNER_KEY"));

		int retcode;
		String retmsg = "";
		String xml_body = "";
		MDataMap mapBody = new MDataMap();
		
		// 获取token值
		String token = AccessTokenRequestHandler.getAccessToken();
		// String token = tokenNew;

		if (!"".equals(token)) {
			if (orderPayCode != null && !"".equals(orderPayCode)) {
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("pay_code", orderPayCode);
				mDataMap.put("state", "0");
				
				// 查询订单相关信息
				Map<String, Object> orderMap = DbUp.upTable("oc_pay_info")
						.dataSqlOne("select * from oc_pay_info where pay_code=:pay_code and state=:state",mDataMap);
				
				//判断订单信息是否存在
				if (orderMap != null && !"".equals(orderMap)
						&& orderMap.size() > 0) {
					/*
					 *  设置package订单参数
					 */
					packageReqHandler.setParameter("bank_type", "WX");// 银行渠道
					/**
					 * 微信支付商品名称  不能有空格！
					 */
					//String bodyString = URLEncoder.encode(String.valueOf(orderMap.get("product_name")).replaceAll(" ", ""));
					packageReqHandler.setParameter("body", bConfig("ordercenter.PRODUCT_NAME")); // 商品描述
					packageReqHandler.setParameter("notify_url", notify_url); // 接收财付通通知的URL
					packageReqHandler.setParameter("partner",bConfig("ordercenter.PARTNER")); // 商户号
					packageReqHandler.setParameter("out_trade_no", orderPayCode); // 商家订单号
					//String.valueOf(((BigDecimal)orderMap.get("due_money")).setScale(0,BigDecimal.ROUND_HALF_UP))+"00"
					packageReqHandler.setParameter("total_fee", String.valueOf((((BigDecimal)orderMap.get("due_money")).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP))); // 商品金额,以分为单位String.valueOf(orderMap.get("due_money"))
					packageReqHandler.setParameter("spbill_create_ip", ip); // 订单生成的机器IP，指用户浏览器端IP
					packageReqHandler.setParameter("fee_type", "1"); // 币种，1人民币// 66
					packageReqHandler.setParameter("input_charset", "UTF-8"); // 字符编码GBK
						
					// 获取package包
					String packageValue = packageReqHandler.getRequestURL();

					String noncestr = WXUtil.getNonceStr();
					String timestamp = WXUtil.getTimeStamp();
					String traceid = "";

					/*
					 * 设置获取prepayid支付参数
					 */
					prepayReqHandler.setParameter("appid", bConfig("ordercenter.APP_ID"));    
					prepayReqHandler.setParameter("appkey",bConfig("ordercenter.APP_KEY")); // 商户密钥// 缺少
					prepayReqHandler.setParameter("noncestr", noncestr);
					prepayReqHandler.setParameter("package", packageValue);
					prepayReqHandler.setParameter("timestamp", timestamp);
					prepayReqHandler.setParameter("traceid", traceid);
					
					// 生成获取预支付签名
					String sign = prepayReqHandler.createSHA1Sign();
					// 增加非参与签名的额外参数HH20104125HH20104125HH20104125HH20104125HH20104125DD140220100006DD140220100006DD140220100006
					prepayReqHandler.setParameter("app_signature", sign);
					prepayReqHandler.setParameter("sign_method",ConstantUtil.SIGN_METHOD);
					
					String gateUrl = ConstantUtil.GATEURL + token;
					prepayReqHandler.setGateUrl(gateUrl);

					// 获取prepayId
					String prepayid = prepayReqHandler.sendPrepay();

					// 吐回给客户端的参数
					if (null != prepayid && !"".equals(prepayid)) {
						// 输出参数列表
						clientHandler.setParameter("appid", bConfig("ordercenter.APP_ID"));  //商家在微信开放平台申请的应用id
						clientHandler.setParameter("appkey",bConfig("ordercenter.APP_KEY"));
						clientHandler.setParameter("noncestr", noncestr);     /** 随机串，防重发 */
						//clientHandler.setParameter("package", "Sign=" + packageValue);
						clientHandler.setParameter("package", "Sign=WXPay");
						clientHandler.setParameter("partnerid",bConfig("ordercenter.PARTNER"));
						clientHandler.setParameter("prepayid", prepayid);
						clientHandler.setParameter("timestamp", timestamp);    /** 时间戳，防重发 */
						// 生成签名
						sign = clientHandler.createSHA1Sign();
						clientHandler.setParameter("sign", sign);
//						String packageString = "out_trade_no="+packageReqHandler.getParameter("out_trade_no")+"&partner="+packageReqHandler.getParameter("partner")+"&key="+packageReqHandler.getKey()+"";
//						clientHandler.setParameter("package", packageString);
						
						mapBody = clientHandler.getMapBody();
						retcode = 0;
						retmsg = "OK";
					} else {
						rootResult.setResultCode(939301222);
						rootResult.setResultMessage("错误：获取prepayId失败！");
					}
				}
			}else{
				rootResult.setResultCode(939301221);
				rootResult.setResultMessage("订单号不能为空！");
			}
		} else {
			rootResult.setResultCode(939301220);
			rootResult.setResultMessage("获取不到Token");
		}
		/**
		 * 打印debug信息
		 */
//		System.out.println("\r\ndebuginfo:\r\n" + new Date());
//		System.out.println(packageReqHandler.getDebugInfo());
//		System.out.println(prepayReqHandler.getDebugInfo());
//		System.out.println(clientHandler.getDebugInfo());
		// out.println("<retcode>" + retcode + "</retcode");
		// out.println("<retmsg>" + retmsg + "<retmsg>");
		if (!"".equals(mapBody) && mapBody!=null && mapBody.size()>0) {
			return mapBody;
		}
		// out.println("</root>");
		return null;

	}
	
	
	
	/**
	 * 微信回调
	 * @param request
	 * @return
	 */
	public PayResult responseWechatMoveService(HttpServletRequest request){
		
		PayResult payResult = new PayResult();
		try {
			//系统自定义的支付编号
			String sPayCode = WebHelper.upCode("PM");  
			boolean resultSuccess = false;   //确定成功后，返回给微信状态标示
			String mark = "100";    //移动微信支付
			
			//---------------------------------------------------------
	    	//财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
	    	//---------------------------------------------------------
	    	//商户号
	    	String partner = bConfig("ordercenter.PARTNER");
	    	//密钥
	    	String key = bConfig("ordercenter.PARTNER_KEY");   

	    	//创建支付应答对象
	    	ResponseHandler resHandler = new ResponseHandler(request);
	    	resHandler.setKey(key);
	    	
	    	// 插入日志流水
			if (payResult.upFlagTrue()) {
				 // 插入支付日志流水信息
				OcPaymentWechatService ocPaymentWechatService = new OcPaymentWechatService();
				ocPaymentWechatService.insertOcPaymentWechat(request, sPayCode, mark);
			}
			
			if (payResult.upFlagTrue()) {
		    	//判断签名
		    	if(resHandler.isTenpaySign()) {
		    		//通知id
		    		String notify_id = resHandler.getParameter("notify_id");
		    		
		    		//创建请求对象
		    		RequestHandler queryReq = new RequestHandler(null, null);
		    		//通信对象
		    		TenpayHttpClient httpClient = new TenpayHttpClient();
		    		//应答对象
		    		ClientResponseHandler queryRes = new ClientResponseHandler();
		    		
		    		//通过通知ID查询，确保通知来至财付通
		    		queryReq.init();
		    		queryReq.setKey(key);
		    		queryReq.setGateUrl("https://gw.tenpay.com/gateway/verifynotifyid.xml");
		    		queryReq.setParameter("partner", partner);
		    		queryReq.setParameter("notify_id", notify_id);
		    		
		    		//通信对象
		    		httpClient.setTimeOut(5);
		    		//设置请求内容
		    		httpClient.setReqContent(queryReq.getRequestURL());
		    		//System.out.println("queryReq:" + queryReq.getRequestURL());
		    		
		    		//后台调用
		    		if(httpClient.call()) {
		    			
		    			//设置结果参数
		    			queryRes.setContent(httpClient.getResContent());
		    		//	System.out.println("queryRes:" + httpClient.getResContent());
		    			queryRes.setKey(key);
		    				
		    				
		    			//获取返回参数
		    			String retcode = queryRes.getParameter("retcode");
		    			String trade_state = queryRes.getParameter("trade_state");
		    			String trade_mode = queryRes.getParameter("trade_mode");
		    			
		    			// 查询订单
		    			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
								queryRes.getParameter("out_trade_no")); 
		    			
		    			
		    			if(dm!=null){
		    				//判断签名及结果
			    			if(queryRes.isTenpaySign()&& "0".equals(retcode) && "0".equals(trade_state) && "1".equals(trade_mode)) {
			    				/*
			    				 * 保持库中金额与微信金额一致(微信以分为单位)
			    				 *  BigDecimal中setScale(x,y)   x是控制小数位数的
			    				 */
			    				String dueMoney = String.valueOf(((new BigDecimal(dm.get("due_money"))).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
			    				//判断付款金额是否相等(库里以元为单位、微信以分为单位)
			    				if(dueMoney.equals(queryRes.getParameter("total_fee"))){
			    					
									// 定义即将锁定的编号
									String sLockKey = queryRes.getParameter("out_trade_no")
											+ queryRes.getParameter("trade_state");

									// 获取锁定唯一约束值
									String sLockUuid = WebHelper.addLock(60,
											sLockKey);

									// 开始锁定交易的流水号和交易类型60秒
									if (StringUtils.isNotEmpty(sLockUuid)) {
										// 判断是否有成功支付流水的记录
										if (DbUp.upTable("oc_order_pay").count(
												"order_code", dm.get("order_code"), "pay_type",OC_ORDER_PAY_PAYTYPE_WECHAT) == 0) {
											MDataMap insertDatamap = new MDataMap();
											insertDatamap.put("out_trade_no", queryRes.getParameter("out_trade_no"));
											insertDatamap.put("trade_no", queryRes.getParameter("transaction_id"));  //微信交易号
											insertDatamap.put("total_fee", dm.get("due_money"));
											insertDatamap.put("seller_email", "");   //买家微信账号无 (不知道写什么)
											
											AlipayProcessService alipayProcessService = new AlipayProcessService();
											alipayProcessService.insertOcOrderPayVoid(insertDatamap,mark); // 插入数据oc_order_pay表
											
											payResult.setOrderCode(dm.get("order_code"));
											
											//resultSuccess = true;
										} else {
											payResult.inErrorMessage(939301205);
										}

										WebHelper.unLock(sLockUuid);

									} else {
										payResult.inErrorMessage(939301206);
									}
			    				}else {
			    					payResult.inErrorMessage(939301203);
								}
			    			}else{
			    				payResult.inErrorMessage(939301225);
			    			}
		    			}else {
							payResult.inErrorMessage(939301200,queryRes.getParameter("out_trade_no"));
						}
		    		} else {
		    			payResult.inErrorMessage(939301226);
		    		}
		    	}
		    	else{
		    		payResult.inErrorMessage(939301227);
		    	}
			}
			
	    	
	    	
	    	//处理业务完毕(返回给微信支付成功信息)
	    		MDataMap mDataMap = new MDataMap();
	    		mDataMap.put("payment_code", sPayCode);
	    		mDataMap.put("process_result", payResult.upJson());
	    		mDataMap.put("process_time", FormatHelper.upDateTime());
	    		mDataMap.put("flag_success", payResult.upFlagTrue() ? "1" : "0");
	    		DbUp.upTable("oc_payment_wechat").dataUpdate(mDataMap, "process_result,process_time,flag_success", "payment_code");
				//resHandler.sendToCFT("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payResult;
	}
	
	
	
	/**
	 * 微信回调最新
	 * @param request
	 * @return
	 */
	public PayResult responseWechatMoveServiceNew(HttpServletRequest request){
		
		PayResult payResult = new PayResult();
		try {
			List<Map<String, Object>> listClient =  new ArrayList<Map<String, Object>>();
			//系统自定义的支付编号
			String sPayCode = WebHelper.upCode("PM");  
			boolean resultSuccess = false;   //确定成功后，返回给微信状态标示
			String mark = "100";    //移动微信支付
			
			//---------------------------------------------------------
	    	//财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
	    	//---------------------------------------------------------
	    	//商户号
	    	String partner = bConfig("ordercenter.PARTNER");
	    	//密钥
	    	String key = bConfig("ordercenter.PARTNER_KEY");   

	    	//创建支付应答对象
	    	ResponseHandler resHandler = new ResponseHandler(request);
	    	resHandler.setKey(key);
	    	
	    	// 插入日志流水
			if (payResult.upFlagTrue()) {
				 // 插入支付日志流水信息
				OcPaymentWechatService ocPaymentWechatService = new OcPaymentWechatService();
				ocPaymentWechatService.insertOcPaymentWechat(request, sPayCode, mark);
			}
			
			if (payResult.upFlagTrue()) {
		    	//判断签名
		    	if(resHandler.isTenpaySign()) {
		    		//通知id
		    		String notify_id = resHandler.getParameter("notify_id");
		    		
		    		//创建请求对象
		    		RequestHandler queryReq = new RequestHandler(null, null);
		    		//通信对象
		    		TenpayHttpClient httpClient = new TenpayHttpClient();
		    		//应答对象
		    		ClientResponseHandler queryRes = new ClientResponseHandler();
		    		
		    		//通过通知ID查询，确保通知来至财付通
		    		queryReq.init();
		    		queryReq.setKey(key);
		    		queryReq.setGateUrl("https://gw.tenpay.com/gateway/verifynotifyid.xml");
		    		queryReq.setParameter("partner", partner);
		    		queryReq.setParameter("notify_id", notify_id);
		    		
		    		//通信对象
		    		httpClient.setTimeOut(5);
		    		//设置请求内容
		    		httpClient.setReqContent(queryReq.getRequestURL());
		    		//System.out.println("queryReq:" + queryReq.getRequestURL());
		    		
		    		//后台调用
		    		if(httpClient.call()) {
		    			
		    			//设置结果参数
		    			queryRes.setContent(httpClient.getResContent());
		    		//	System.out.println("queryRes:" + httpClient.getResContent());
		    			queryRes.setKey(key);
		    				
		    				
		    			//获取返回参数
		    			String retcode = queryRes.getParameter("retcode");
		    			String trade_state = queryRes.getParameter("trade_state");
		    			String trade_mode = queryRes.getParameter("trade_mode");
		    			
		    			// 查询订单
		    			MDataMap dm = DbUp.upTable("oc_pay_info").one("pay_code",
								queryRes.getParameter("out_trade_no")); 
		    			
		    			
		    			if(dm!=null){
		    				//判断签名及结果
			    			if(queryRes.isTenpaySign()&& "0".equals(retcode) && "0".equals(trade_state) && "1".equals(trade_mode)) {
			    				/*
			    				 * 保持库中金额与微信金额一致(微信以分为单位)
			    				 *  BigDecimal中setScale(x,y)   x是控制小数位数的
			    				 */
			    				String dueMoney = String.valueOf(((new BigDecimal(dm.get("due_money"))).multiply(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
			    				//判断付款金额是否相等(库里以元为单位、微信以分为单位)
			    				if(dueMoney.equals(queryRes.getParameter("total_fee"))){
			    					
									// 定义即将锁定的编号
									String sLockKey = queryRes.getParameter("out_trade_no")
											+ queryRes.getParameter("trade_state");

									// 获取锁定唯一约束值
									String sLockUuid = WebHelper.addLock(60,
											sLockKey);

									// 开始锁定交易的流水号和交易类型60秒
									if (StringUtils.isNotEmpty(sLockUuid)) {
										
										//查询支付宝单号
										listClient = DbUp.upTable("oc_paydetail").dataSqlList(
												"select * from oc_paydetail where pay_code = '"+queryRes.getParameter("out_trade_no")+"' order by create_time desc", new MDataMap());
										
										for(Map<String, Object> map : listClient){
											
											String orderCodeBig = String.valueOf(map.get("big_order_code"));
											
											String orderCodeSmall = String.valueOf(map.get("order_code"));
											
											
											// 判断是否有成功支付流水的记录
											if (DbUp.upTable("oc_order_pay").count(
													"order_code", orderCodeSmall, "pay_type",OC_ORDER_PAY_PAYTYPE_WECHAT) == 0) {
												
												MDataMap insertDatamap = new MDataMap();
												insertDatamap.put("out_trade_no", orderCodeSmall);
												insertDatamap.put("trade_no", queryRes.getParameter("transaction_id"));  //微信交易号
												insertDatamap.put("total_fee", dm.get("due_money"));
												insertDatamap.put("seller_email", "");   //买家微信账号无 (不知道写什么)
												insertDatamap.put("pay_code", dm.get("pay_code"));
												
												AlipayProcessService alipayProcessService = new AlipayProcessService();
												alipayProcessService.insertOcOrderPayVoidNew(insertDatamap,mark); // 插入数据oc_order_pay表
												
												//resultSuccess = true;
											} else {
												payResult.inErrorMessage(939301205);
											}
										}
										//更新支付单 和 支付单详情 信息状态，更改为以支付
										DbUp.upTable("oc_pay_info").dataUpdate(
												new MDataMap("seller_email","","trade_no",queryRes.getParameter("transaction_id"),
														"pay_code", dm.get("pay_code"),"state","2"), "state", "pay_code");
										
										DbUp.upTable("oc_paydetail").dataUpdate(
												new MDataMap("pay_code",dm.get("pay_code"),"state","2"), "state", "pay_code");
										
										payResult.setOrderCode(dm.get("pay_code"));
										//解锁
										WebHelper.unLock(sLockUuid);

									} else {
										payResult.inErrorMessage(939301206);
									}
			    				}else {
			    					payResult.inErrorMessage(939301203);
								}
			    			}else{
			    				payResult.inErrorMessage(939301225);
			    			}
		    			}else {
							payResult.inErrorMessage(939301200,queryRes.getParameter("out_trade_no"));
						}
		    		} else {
		    			payResult.inErrorMessage(939301226);
		    		}
		    	}
		    	else{
		    		payResult.inErrorMessage(939301227);
		    	}
			}
			
	    	
	    	
	    	//处理业务完毕(返回给微信支付成功信息)
	    		MDataMap mDataMap = new MDataMap();
	    		mDataMap.put("payment_code", sPayCode);
	    		mDataMap.put("process_result", payResult.upJson());
	    		mDataMap.put("process_time", FormatHelper.upDateTime());
	    		mDataMap.put("flag_success", payResult.upFlagTrue() ? "1" : "0");
	    		DbUp.upTable("oc_payment_wechat").dataUpdate(mDataMap, "process_result,process_time,flag_success", "payment_code");
				//resHandler.sendToCFT("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payResult;
	
		
	}
	
	/**
	 * 获取交易取消的时间
	 * @param orderCode
	 * 		订单编号
	 * @return 交易取消的时间
	 */
	public int getOrderCancelTime(String orderCode) {

		int time = 0;

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
					time = temp * 60;
				} else if ("449747280002".equals(type)) {
					time = temp;
				} else {	
					time = temp/60;
				}
			}
			
		}
		
		return time;

	}
}
