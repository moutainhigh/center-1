package com.cmall.ordercenter.alipay.config;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;

public class AlipayMoveConfig extends BaseClass{

//	//	public static String partner = "2088511816887140"; 
//	//public static String partner = "2088501700700194"; // 合作身份者ID，以2088开头由16位纯数字组成的字符串
//	//商户私钥
//	public static String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMCKIVvh6FMB50R1mUBeKR05nFTW/bHTnaAaRfR2eQ0+C31YHuGG61estWB6XzFersHYKb18BdZLb/Ra+s4vQauRGFZiFfsqc7HKrRVCfYDaPs9u5LEDRr2975sAYZiKOffgcUgwcR9Oj0JaqWI91xL8Pnl3gyVtIsLf7iD7rhlXAgMBAAECgYAqyMb+5uU8RMkCQmuKjSHvt5SQmbGIKXD2WcA/wW/GzIm7EbDTBqsXMW6ggLDUhKiqtIEZ9QxLATpgfzMKTB/4QB0A3OJ6Uw9qpKdR8x7XMPRMehZnbw6JQ7ZwbGPjqkMN0U7MlqUM0e+ufekSjUBWmSPH63yJlZGMgl3LQvdE2QJBAOkv17uzcuoJ6pmGEzUjlbWhg0LYNisAa8iOUnUkrqzn/y7mtEx4p9zqTjRAu2yy0/ck2LpNY9GuMlg+NL5vS1sCQQDTYEbtWDE1pFaZPLCMkprLQLhT3eeLQzP7d60mfQW96W3QjprlipvHj4kDssX1/RnQleivNY/JfepFcelOzBa1AkAZ44HkCOw9J5SwLr57K9Q3MhNMnIyHAaj1vzdQYh4yfB9MqbhitRKN6EV+b6FfVAtMaP7W0DjA0sIsIdvhOKH5AkBft5BGuBIIlXN1jqrv7Q9VjOgraigIwxTOAcKR1Dl+Zy8IKxtvaFXkh1XnK9RC8Sr4bnngpWOIPZGRguTAfuClAkEAoMteToYMktB3/Skb+mkL0OZrU6GPdJ2HO9XHnYlXQNdpB3v28DTXJLDYEVAhUVZ5JScuoJWpBW2H4BCoG2mBXw==";
//	
//	//支付宝的公钥，无需修改该值
//	//public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
//	
//	public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
//	
////	public static String seller_email = "weigongshe@52yungo.com"; // //乾和企业级账号
//	//public static String seller_email = "webpay2011@jiayougo.com";
//	
//	// 移动支付即时到帐接口名称
//	public static String serviceMove = "mobile.securitypay.pay";
//
//	// 签名方式 不需修改
//	public static String sign_type = "RSA";
//	// 字符编码格式 目前支持 gbk 或 utf-8
//	public static String input_charset = "UTF-8";
//	
//	//支付宝http链接
//	public static String ali_url_http = "https://wappaygw.alipay.com/service/rest.htm?";
//	
	public static final String HUIMEILI_SELLORCODE = "SI2007";  //惠美丽商品编号(通和支付账号)
	public static final String HUIJIAYOU_SELLORCODE = "SI2003";  //惠家有商品编号(惠家有支付宝账号)
	public static final String LIUJIALING_SELLORCODE = "SI2001";  //刘嘉玲商品编号(目前在用家有)
	public static final String XIAOSHIDAI_SELLORCODE = "SI2013";  //小时代商品编号(小时代账号)
	public static final String SHAPIGOU_SELLORCODE = "SI3003";  //沙皮狗sellerCode(惠家有支付宝账号)
	
	
	/**
	 * 获取支付需要的必须参数
	 * @param sellerCode
	 * @return
	 */
	public  Map<String,String> alipayConfig(String sellerCode){
		Map<String,String> map = new HashMap<String,String>();
		
		if(HUIMEILI_SELLORCODE.equals(sellerCode)){
			map.put("partner", bConfig("ordercenter.partner_TongHe"));  //合作身份者ID(通和)
			map.put("seller_email", bConfig("ordercenter.seller_email_TongHe"));   //企业级账号
			map.put("key", bConfig("ordercenter.key_move"));   //私钥
			map.put("public_key", bConfig("ordercenter.public_key"));   //公钥
			
		}else if(HUIJIAYOU_SELLORCODE.equals(sellerCode)){
			map.put("partner", bConfig("ordercenter.partner_Huijiayou"));
			map.put("seller_email", bConfig("ordercenter.seller_email_Huijiayou"));
			map.put("key", bConfig("ordercenter.key_move"));
			map.put("public_key", bConfig("ordercenter.public_key"));
			
			
//			map.put("partner", bConfig("ordercenter.partner_jiayou"));
//			map.put("seller_email", bConfig("ordercenter.seller_email_jiayou"));
//			map.put("key", bConfig("ordercenter.key_move"));
//			map.put("public_key", bConfig("ordercenter.public_key"));
			
		}else if(LIUJIALING_SELLORCODE.equals(sellerCode)){
			map.put("partner", bConfig("ordercenter.partner_jiayou"));
			map.put("seller_email", bConfig("ordercenter.seller_email_jiayou"));
			map.put("key", bConfig("ordercenter.key_move"));
			map.put("public_key", bConfig("ordercenter.public_key"));
		}else if(XIAOSHIDAI_SELLORCODE.equals(sellerCode)){
			map.put("partner", bConfig("ordercenter.partner_xiaoshidai"));  //合作身份者ID(通和)
			map.put("seller_email", bConfig("ordercenter.seller_email_xiaoshidai"));   //企业级账号
			map.put("key", bConfig("ordercenter.key_move"));   //私钥
			map.put("public_key", bConfig("ordercenter.public_key"));   //公钥
		}else if(SHAPIGOU_SELLORCODE.equals(sellerCode)){
			map.put("partner", bConfig("ordercenter.partner_Huijiayou"));
			map.put("seller_email", bConfig("ordercenter.seller_email_Huijiayou"));
			map.put("key", bConfig("ordercenter.key_move"));
			map.put("public_key", bConfig("ordercenter.public_key"));
		}
		
		return map;
	}
	
	
}


