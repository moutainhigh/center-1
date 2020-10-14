package com.cmall.systemcenter.common;

import com.srnpr.zapcom.basemodel.MDataMap;

public class MDataMapUtil {
	
	public static MDataMap getDataMapFromDataScop(String sParams){
		MDataMap data = new MDataMap().inUrlParams(sParams);
		return data;
	}

}
