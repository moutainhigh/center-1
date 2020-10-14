package com.cmall.groupcenter.kjt.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;

/**
 * 同步的配置选项
 * 
 * 
 */
public class RsyncConfigGetKlProductIdByDate extends RsyncConfigRsyncBase implements
		IRsyncDateCheck {

	public String getRsyncTarget() {

		//return "Product.ProductIDGetQuery";
		//这个暂不返回信息
		return "";
	}

	
	public String getBaseStartTime() {
		return "2018-10-01 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 36;
	}

	public int getBackSecond() {
		return 3600;
	}

}
