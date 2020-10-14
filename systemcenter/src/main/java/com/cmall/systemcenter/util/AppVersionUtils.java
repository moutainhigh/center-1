package com.cmall.systemcenter.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class AppVersionUtils {

	/**
	 * 对比两个版本字符串，格式：  x.y.z<br>
	 * 5.0.1 <> 5.0.2 => -1<br>
	 * 5.0.2 <> 5.0.1 => 1<br>
	 * 5.0.2 <> 5.0.10 => -1
	 * 5.0.1 <> 5.0.1 => 0<br>
	 * 5.0.0 <> 5.0 => 1<br>
	 * 5.0 <> 5.0.1 => -1<br>
	 * @param v1
	 * @param v2
	 * @return 0: 相等、 1: 参数v1大于参数v2、-1: 参数v1小于参数v2
	 */
	public static int compareTo(String v1, String v2){
		v1 = StringUtils.trimToEmpty(v1);
		v2 = StringUtils.trimToEmpty(v2);
		
		if(v1.equals(v2)) {
			return 0;
		}
		
		// 长度相等时直接进行字符串对比
		if(v1.length() == v2.length()){
			return v1.compareTo(v2);
		}
		
		String[] v1s = v1.split("\\.");
		String[] v2s = v2.split("\\.");
		
		int len = v1s.length > v2s.length ? v1s.length : v2s.length;
		for(int i = 0; i< len; i++){
			
			if(v1s.length <= i){
				return -1;
			}
			
			if(v2s.length <= i){
				return 1;
			}
			
			if(NumberUtils.toInt(v1s[i]) > NumberUtils.toInt(v2s[i])){
				return 1;
			}
			
			if(NumberUtils.toInt(v1s[i]) < NumberUtils.toInt(v2s[i])){
				return -1;
			}
		}
		
		return 0;
	}
}
