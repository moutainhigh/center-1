package com.cmall.systemcenter.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class MD5Code {

	static private MD5Code instance = null;
	private Logger log = Logger.getLogger(MD5Code.class);

//	static synchronized public MD5Code Instance() {
//		if (instance == null) {
//			instance = new MD5Code();
//		}
//		return instance;
//	}
	
	
	private final static String[] hexDigits = {
		"0","1","2","3","4","5","6","7",
		"8","9","a","b","c","d","e","f"};
	
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(byteToHexString(b[i]));
		}
		return sb.toString();
	}
	
	public static String  byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n += 256;
		}
		return hexDigits[n/16] + hexDigits[n%16];
	}
	
	public  static String encode(String origin) throws UnsupportedEncodingException {
		String s = new String(origin);
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			s = byteArrayToHexString(md.digest(s.getBytes("gb2312")));
		} catch (NoSuchAlgorithmException e) {
			//log.warn(e,e);
			e.printStackTrace();
		}
		return s;
	}

}
