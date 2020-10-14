package com.cmall.groupcenter.behavior.common;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.topdo.TopUp;

/**
 * 操作标识枚举
 * @author pang_jhui
 *
 */
public enum OperFlagEnum {
	
	orderdetail(10,"4497477200010001"),
	productdetail(10,"4497477200020001"),
	paysuccess(10,"4497477200030001"),
	maybelove(50,"4497477200040001"),
	shopcart(50,"");
	
	/*返回限制的数量*/
	private int limitNum;
	
	/*推荐栏defineId*/
	private String defineId;
	
	OperFlagEnum(int limitNum,String defineId){
		
		this.limitNum = limitNum;
		
		this.defineId = defineId;
		
	}
	
	/**
	 * 获取返回限制的条数
	 * @return
	 */
	public int getLimitNum() {
		
		String sKey = "maybelove_"+name()+"_limit";
		
		String limitNumStr = TopUp.upConfig(sKey);
		
		if(StringUtils.isNotBlank(limitNumStr)){
			
			limitNum = Integer.parseInt(limitNumStr);
			
		}
		
		return limitNum;
	}
	
	/**
	 * 根据枚举名称获取限制数
	 * @param name
	 * 		枚举名称
	 * @return 限制数
	 */
	public static OperFlagEnum getByName(String name){
		
		for (OperFlagEnum element : OperFlagEnum.values()) {
			
			if(StringUtils.equals(name, element.name())){
				
				return element;
				
			}
			
		}
		
		return null;
		
	}

	/**
	 * 获取推荐栏定义defineId
	 * @return sc_define code
	 */
	public String getDefineId() {
		
		return defineId;
		
	}

}
