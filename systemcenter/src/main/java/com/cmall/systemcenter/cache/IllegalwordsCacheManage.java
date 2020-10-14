package com.cmall.systemcenter.cache;

import java.util.ArrayList;
import java.util.List;

import com.cmall.systemcenter.model.ScIllegalKeywords;



public class IllegalwordsCacheManage {
	
	private static IllegalwordsCache psc = new IllegalwordsCache();
	
	
	/**
	 * 取得所有需要缓存的数据
	 * @return
	 */
	public List<ScIllegalKeywords> getScIllegalKeywordsList(){
		
		//return psc.upOne(IllegalwordsCache.cacheKey);
		return psc.upValue(IllegalwordsCache.cacheKey);
	}
}
