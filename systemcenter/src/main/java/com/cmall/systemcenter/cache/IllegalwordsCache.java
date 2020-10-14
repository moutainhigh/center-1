package com.cmall.systemcenter.cache;

import java.util.ArrayList;
import java.util.List;

import com.cmall.systemcenter.model.ScIllegalKeywords;
import com.cmall.systemcenter.service.ScIllegalKeywordsServices;
import com.srnpr.zapcom.rootclass.RootCache;

public class IllegalwordsCache extends RootCache<String, List<ScIllegalKeywords>> {

	public static String cacheKey = "ScIllegalKeywordsCaches";
	
	public void refresh() {
		
		ScIllegalKeywordsServices ss = new ScIllegalKeywordsServices();
		
		List<ScIllegalKeywords> list = ss.getIllegalKeywordsList();
		if(list == null)
			list = new ArrayList<ScIllegalKeywords>();

		this.inElement(IllegalwordsCache.cacheKey, list);
	}

	@Override
	public List<ScIllegalKeywords> upOne(String k) {
		ScIllegalKeywordsServices ss = new ScIllegalKeywordsServices();
		
		List<ScIllegalKeywords> list = ss.getIllegalKeywordsList();
		if(list == null)
			list = new ArrayList<ScIllegalKeywords>();
		
		return list;

	}

}
