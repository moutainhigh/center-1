package com.cmall.systemcenter.util;

import java.math.BigDecimal;

/**
 * 各种转换方法
 * @author ligj
 * 
 */
public class FormatUtil {
	private static final String PERCENT_SYMBOL = "%";
	/**
	 * String类型的数字转换为百分号格式
	 * @param number	要转换的数字
	 * @param digits	保留到小数后几位(四舍五入)
	 * @return
	 */
	public String stringToPercent(String number,int digits){
		String result = "0";
		try {
			if (digits > -1) {
				result = String.valueOf(
						new BigDecimal(number).multiply(new BigDecimal(100))
						.setScale(digits, BigDecimal.ROUND_HALF_UP)
						);
			}else{
				result = String.valueOf(new BigDecimal(number).multiply(new BigDecimal(100))); 
			}
		} catch (Exception e) {
			return number;
		}
		result = this.subZeroAndDot(result);
		return ( result + FormatUtil.PERCENT_SYMBOL );
	}
	 /** 
     * 使用java正则表达式去掉多余的.与0 
     * @param s 
     * @return  
     */  
    public String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    }  
}
