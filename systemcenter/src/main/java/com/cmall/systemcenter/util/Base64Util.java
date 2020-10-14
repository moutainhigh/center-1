package com.cmall.systemcenter.util;

import sun.misc.BASE64Decoder; 

/**
 * BASE64解析
 * @author zhouguohui
 *
 */
public class Base64Util {

	public static String getFromBASE64(String s) {
			if (s == null) return null;
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				byte[] b = decoder.decodeBuffer(s);
				return new String(b);
				} catch (Exception e) {
				return null;
			}
		}
	
}
