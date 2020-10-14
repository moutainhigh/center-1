package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.List;

import com.cmall.systemcenter.cache.IllegalwordsCacheManage;
import com.cmall.systemcenter.model.ScIllegalKeywords;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;

public class ScIllegalKeywordsServices {
	
	
	/**
	 * 获取替换之后的合法数据
	 * @param text
	 * @return
	 */
	public String getLegalKeyWords(String text){
		
		IllegalwordsCacheManage im  = new IllegalwordsCacheManage();
		String ret = text;
		
		if(text == null)
			return ret;
		else{
			String regex= "";
			
			List<ScIllegalKeywords> list = im.getScIllegalKeywordsList();
			
			if(list!=null){
				for(ScIllegalKeywords sk : list){
					regex = sk.getIllegalWords();
					ret = ret.replace(regex, "");
				}
			}
		}
		
		return ret;
		
	}
	
	
	public List<ScIllegalKeywords> getIllegalKeywordsList(){
		List<ScIllegalKeywords> retList = new ArrayList<ScIllegalKeywords>();
		 
		MDataMap afMapParam = new MDataMap();
		
		List<MDataMap> pListMap = DbUp.upTable("sc_illegal_keywords").query(
				"", "", "",	afMapParam, -1, -1);
		 
		SerializeSupport  sSku= new SerializeSupport<ScIllegalKeywords>();
		
		if(pListMap != null)
		{
			int size = pListMap.size();
			
			for(int j=0;j<size;j++)
			{
				ScIllegalKeywords pic = new ScIllegalKeywords();
				sSku.serialize(pListMap.get(j), pic);
				retList.add(pic);
			}
		}
		
		return retList;
	}

}
