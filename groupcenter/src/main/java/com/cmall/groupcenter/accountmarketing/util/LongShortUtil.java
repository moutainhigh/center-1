package com.cmall.groupcenter.accountmarketing.util;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.url.ShortUrl;
import com.srnpr.zapdata.dbdo.DbUp;

public class LongShortUtil extends BaseClass {
	
	public String getShortUrl(String longUrl ){
		

		String shortUrl = "";
		
		MDataMap map = DbUp.upTable("nc_short_url").one("long_url",longUrl);
		
		if(map!=null){
			
			shortUrl = bConfig("groupcenter.short_url")+map.get("short_url");
			
		}else{
			
			String short_url = ShortUrl
					.upShortUrl(longUrl);
			
			DbUp.upTable("nc_short_url").insert("long_url",
					longUrl, "short_url", short_url);
			
			shortUrl = bConfig("groupcenter.short_url")+short_url;
			
		}
		return shortUrl;
			
	
		
		
	}

}
