package com.cmall.newscenter.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unused", "unchecked" })
public class Bean2MapUtil {

	@SuppressWarnings({ "unused", "unchecked" })
	private static Map<String, String> bean2Map(Object obj) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		if (obj != null) {
			Class c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			for (Field field : fs) {
				String fieldName = field.getName();
				String getname = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				String value = c.getMethod(getname, null).invoke(obj, null)
						.toString();
				if (value != null && !value.equals("")) {
					map.put(fieldName, value);
				}
			}
		}
		return map;
	}
}
