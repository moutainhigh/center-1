package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/***
 * 广告输入类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class AdvertisementBannerInput extends RootInput {

	@ZapcomApi(value = "所在位置",remark = "所在位置",demo = "1",require = 1)
	private int location;

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

}
