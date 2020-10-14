package com.cmall.systemcenter.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StringUtility {
	
	/**
	   * 产生随机字符串
	   * */
	private static Random randGen = new Random();;
	private static char[] numbersAndLetters = ("123456789ABCDEFGHIJKLMNPQRSTUVWXYZ").toCharArray();

    public static boolean isNull(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotNull(String value) {
        return value != null && value.length() > 0;
    }

    public static String toHexString(byte[] datas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < datas.length; i++) {
            String hex = Integer.toHexString(datas[i] & 0xFF);
            if (hex.length() <= 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String toJson(List list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(',');
            }
            Object o = list.get(i);
            if (o instanceof String) {
                String os = (String) o;
                // " => \", however regex expression need double, so it become
                // complex
                os = os.replaceAll("\"", "\\\\\"");
                sb.append('"').append(os).append('"');
                ;
            } else {
                sb.append(list.get(i));
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public static String toJson(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (!set.isEmpty()) {
            for (String key : set) {
                sb.append("\"").append(key).append("\",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(']');
        return sb.toString();
    }
    
    /** 
	* @Description:生成随机串
	*@param length 长度
	*@param num 生成数量
	*@param start 开头字符串
	* @author 张海生
	* @date 2015-6-3 下午2:58:53
	* @return Set<String> 
	* @throws 
	*/
	public static Set<String> randomString(int length,int num,String start) {
	         if (length < 1||num < 1) {
	             return null;
	         }
	         Set<String> set = new HashSet<String>();
	         for (int j = 0; j < num; j++) {
	        	 char [] randBuffer = new char[length];
		         for (int i=0; i<randBuffer.length; i++) {
		          randBuffer[i] = numbersAndLetters[randGen.nextInt(34)];
		         }
		         set.add(start+new String(randBuffer));
	         }
	         return set;
	}

}
