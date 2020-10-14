/**
 * Project Name:systemcenter
 * File Name:UsePresentCardService.java
 * Package Name:com.cmall.systemcenter.service
 * Date:2013年11月4日下午5:30:39
 *
*/

package com.cmall.systemcenter.service;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cmall.systemcenter.util.Http_Request_Post;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;

/**
 * ClassName:UsePresentCardService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月4日 下午5:30:39 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class UsePresentCardService extends BaseClass {
	/**
	 * 
	 * userPresentCard:(这里用一句话描述这个方法的作用). <br/>
	 * @author hxd
	 * @param jsonString  jsong字符串
	 * @param timestp     时间戳10位整形
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @since JDK 1.6
	 */
	 public String userPresentCard(String jsonString,String timestp) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		 	Map<String, String> map = new HashMap<String, String>();
		 	String key = bConfig("systemcenter.comononkey");
			String method = bConfig("systemcenter.method");
			String requerst_url = bConfig("systemcenter.requerst_url");
			String secrurity_key = bConfig("systemcenter.secrurity_key");
			map.put("apiMethod", method);
			map.put("jsonData", jsonString);
			map.put("security_key", secrurity_key);
			map.put("t", timestp);
			map.put("sign",  SecrurityHelper.getEncoderByMd5(key+method+timestp+key));
			String temp = Http_Request_Post.doPost(requerst_url, map, "utf-8");
			return temp;
		 
	}
	 
	/**
	 * 
	 * userPresentCard:(这里用一句话描述这个方法的作用). <br/>
	 * @author hxd
	 * @param jsonString  jsong字符串
	 * @param timestp     时间戳10位整形
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @since JDK 1.6
	 */
	 public String cancelPresentCard(String jsonString,String timestp) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		 	Map<String, String> map = new HashMap<String, String>();
		 	String key = bConfig("systemcenter.comononkey");
			String method = bConfig("systemcenter.methodforcancel");
			String requerst_url = bConfig("systemcenter.requerst_url");
			String secrurity_key = bConfig("systemcenter.secrurity_key");
			map.put("apiMethod", method);
			map.put("jsonData", jsonString);
			map.put("security_key", secrurity_key);
			map.put("t", timestp);
			map.put("sign",  SecrurityHelper.getEncoderByMd5(key+method+timestp+key));
			String temp = Http_Request_Post.doPost(requerst_url, map, "utf-8");
			return temp;
		 
	}
}

