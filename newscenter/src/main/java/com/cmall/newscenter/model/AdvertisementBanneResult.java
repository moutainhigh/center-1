package com.cmall.newscenter.model;

import java.util.*;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 广告输出类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class AdvertisementBanneResult extends RootResultWeb {
	
	@ZapcomApi(value = "广告相关内容")
	private List<AdvertisementBanner> banners = new ArrayList<AdvertisementBanner>();

	public List<AdvertisementBanner> getBanners() {
		return banners;
	}

	public void setBanners(List<AdvertisementBanner> banners) {
		this.banners = banners;
	}

}
