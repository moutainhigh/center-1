package com.cmall.groupcenter.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * 把父类属性拷贝到子类
 * @param <F>父类
 * @param <S>子类
 * */
public class SubCopyFatHelper<F,S> {

	public void clone(F a,S b) {
		Class<?> type = a.getClass();
		try {
			do {
				for (Field f : type.getDeclaredFields()) {
					String fieldName = f.getName();
					String firstCharUpper =  fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					String getFieldName = "get" + firstCharUpper;
					String setFieldName = "set" + firstCharUpper;
//					System.out.println(getFieldName + "  " + setFieldName);
					Object date = type.getMethod(getFieldName).invoke(a);					
					if((date instanceof Integer)){
						type.getMethod(setFieldName,Integer.TYPE).invoke(b,date);
					}else if((date instanceof List)){
						type.getMethod(setFieldName,List.class).invoke(b,date);
					}else if((date instanceof Map)){
						type.getMethod(setFieldName,Map.class).invoke(b,date);
					}else{
						type.getMethod(setFieldName,date.getClass()).invoke(b,date);
					}							
				}
				type = type.getSuperclass();
			} while (null != type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
