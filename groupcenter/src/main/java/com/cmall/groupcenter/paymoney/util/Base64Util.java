package com.cmall.groupcenter.paymoney.util;


import org.apache.commons.codec.binary.Base64;

public class Base64Util {

	 /** 
     * 编码 
     * @param bstr 
     * @return String 
     */  
    public static String encode(byte[] bstr){  
    return Base64.encodeBase64String(bstr);
    }  
  
    /** 
     * 解码 
     * @param str 
     * @return string 
     */  
    public static byte[] decode(String str){  
    byte[] bt = null;  
    try {  
        bt = Base64.decodeBase64(str);
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
  
        return bt;  
    }  
}
