package com.cmall.newscenter.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmall.groupcenter.weixin.WeiXinUtil;
import com.cmall.ordercenter.util.HttpRequestUrlUtil;
import com.srnpr.zapcom.basemodel.MDataMap;

public class PastUtil {
	
    public static String token = null;
    public static String time = null;
    public static String jsapi_ticket = null;
    
    
    
    public static String getJsApiTicket(String appId,String appSecret){
    	
    	
    	if(token == null){
            token = getAccess_token(appId, appSecret);
            jsapi_ticket = getJsApiTicket(token);
            time = getTime();
        }else{
            if(!time.substring(0, 13).equals(getTime().substring(0, 13))){ //每小时刷新一次
                token = null;
                token = getAccess_token(appId, appSecret);
                jsapi_ticket = getJsApiTicket(token);
                time = getTime();
            }
        }
    	
    	return jsapi_ticket;
    	
    }
    
    
    /**
     * 
     * @param appId   公账号appId
     * @param appSecret
     * @param url    当前网页的URL，不包含#及其后面部分
     * @return
     */
    public static MDataMap getParam(String appId,String appSecret,String url){
        if(token == null){
            token = getAccess_token(appId, appSecret);
            jsapi_ticket = getJsApiTicket(token);
            time = getTime();
        }else{
            if(!time.substring(0, 13).equals(getTime().substring(0, 13))){ //每小时刷新一次
                token = null;
                token = getAccess_token(appId, appSecret);
                jsapi_ticket = getJsApiTicket(token);
                time = getTime();
            }
        }
         
        Map<String, String> params = sign(jsapi_ticket, url);
        params.put("appid", appId);
         
        // JSONObject jsonObject = JSONObject.valueToString(params);  
        HttpRequestUrlUtil httpRequestUrl = new HttpRequestUrlUtil();
        
        String reponseParams = httpRequestUrl.requestURL(url, params);
        
       // JSONObject jsonObject =null;
       // JsonUtil.getJsonValues(reponseParams);
        MDataMap map = xmlToMDataMap(reponseParams);
        
        //String jsonStr = JSONObject.valueToString(params);
        return map;
    }
     
    
    private static String getUrl(){
        //HttpServletRequest request = ServletActionContext.getRequest();
         
       // StringBuffer requestUrl = request.getRequestURL();
         
       // String queryString = request.getQueryString();
        String url = "";
        //		requestUrl +"?"+queryString;
        return url;
    }
     
    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String str;
        String signature = "";
 
        //注意这里参数名必须全部小写，且必须有序
        str = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
 
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
 
        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
 
        return ret;
    }
 
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
 
    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }
 
    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
     
    //获取当前系统时间 用来判断access_token是否过期
    public static String getTime(){
        Date dt=new Date();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }
    
    
    /**
     * 获取接口访问凭证
     * 
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public static String getAccess_token(String appid, String appsecret) {
            //凭证获取(GET)
    	
    	//
        String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        // 发起GET请求获取凭证
        JSONObject jsonObject = new WeiXinUtil().httpsRequest(requestUrl, "GET", null);
                String access_token = null;
        if (null != jsonObject) {
            try {
                access_token = jsonObject.getString("access_token");
            } catch (JSONException e) {
            	e.printStackTrace();
            }
        }
        return access_token;
    }
    
    
    /**
     * 调用微信JS接口的临时票据
     * 
     * @param access_token 接口访问凭证
     * @return
     */
    public static String getJsApiTicket(String access_token) {
        String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        String requestUrl = url.replace("ACCESS_TOKEN", access_token);
        // 发起GET请求获取凭证
        JSONObject jsonObject = new WeiXinUtil().httpsRequest(requestUrl, "GET", null);
        String ticket = null;
        if (null != jsonObject) {
            try {
                ticket = jsonObject.getString("ticket");
            } catch (JSONException e) {
            	
             e.printStackTrace();
            }
        }
        return ticket;
    }
    
    
	/**
	 * 将xml字符串转换为Map
	 * @param xmlStr
	 * 		待转换xml字符串
	 * @return 转换后map集合
	 */
	public static  MDataMap xmlToMDataMap(String xmlStr){
		
		MDataMap mDataMap = null;
		
		try {
			
			Document document = DocumentHelper.parseText(xmlStr);
			
			Element rootElement = document.getRootElement();
			
			Iterator<?> elements = rootElement.elementIterator(); 
			
			if(elements.hasNext()){
				
				mDataMap = new MDataMap();
				
				while (elements.hasNext()) {
					
					Element element = (Element) elements.next();
					
					if(element.elements().size() > 0){
						
						String childXmlStr = convertXmlStr(element.elementIterator());
						
						mDataMap.put(element.getName(), childXmlStr);
						
					}else{
						
						mDataMap.put(element.getName(), element.getTextTrim());
						
					}
					
								
					
				}
				
			}
			 
			 
			
		} catch (DocumentException e) {
			
			e.printStackTrace();
			
		}
		
		return mDataMap;
		
	}
	
	/**
	 * 将子节点保存在为字符串
	 * @param elments
	 * 		子节点集合
	 * @return 子节点字符串
	 */
	public static String convertXmlStr(Iterator<?> elments){
		
		StringBuffer buffer = new StringBuffer();
		
		while (elments.hasNext()) {
			
			Element element = (Element) elments.next();
			
			buffer.append(element.asXML());
			
		}
		
		return buffer.toString();
		
	}
}
