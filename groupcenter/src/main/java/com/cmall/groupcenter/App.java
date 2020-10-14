package com.cmall.groupcenter;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.cmall.systemcenter.bill.HexUtil;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;

public class App extends BaseClass {
	
	
	public static void main(String[] args) throws Exception {
		
		String encode="UTF-8";
		String endpoint="http://preapi.kjt.com/open.api";
		String method="Product.ProductPriceBatchGet";
		String version="1.0";
		String appid="seller268";
		String secretkey="248e6b7662234b92a41de83d02f76b33";
		String format="json";
		String timestamp=DateUtil.getSysDateTimeString(DateUtil.sdfDateTimeTamp);
		
		String nonce=String.valueOf(new Random().nextInt(100000));
		
		JsonHelper<Map<String, Object>> mjsonHelper = new JsonHelper<Map<String, Object>>();
		//参数
		String ProductIDs [] = new String[]{"123456"};
		String SaleChannelSysNo="62";
		
		Map<String, Object> mm= new HashMap<String, Object>();
		mm.put("ProductIDs", ProductIDs);
		mm.put("SaleChannelSysNo", SaleChannelSysNo);
		
		
		MDataMap dataMap = new MDataMap();
		dataMap.put("method", method);
		dataMap.put("format", format);
		dataMap.put("version", version);
		dataMap.put("appid", appid);
		dataMap.put("timestamp", timestamp);
		dataMap.put("nonce", nonce);
		dataMap.put("data", mjsonHelper.ObjToString(mm));
		
		//签名顺序
		String sign="appid="+appid+"&data="+URLEncoder.encode(mjsonHelper.ObjToString(mm), encode)+"&format="+format+"&method="+method+"&nonce="+nonce+"&timestamp="+timestamp+"&version="+version;
		sign=HexUtil.toHexString(MD5Util.md5(sign+"&"+secretkey));
		
		dataMap.put("sign", sign);
		//System.out.println(WebClientSupport.upPost(endpoint, dataMap));
		
	}
	
}
