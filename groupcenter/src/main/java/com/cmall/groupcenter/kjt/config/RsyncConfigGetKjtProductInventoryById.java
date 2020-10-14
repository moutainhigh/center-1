package com.cmall.groupcenter.kjt.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
/**
 * 商品分销渠道库存批量获取
 * @author zmm
 *
 */
public class RsyncConfigGetKjtProductInventoryById extends RsyncConfigRsyncBase implements IRsyncDateCheck{

	public String getRsyncTarget() {
		return "Inventory.ChannelQ4SBatchGet";
	}

	public String getBaseStartTime() {
		return "2015-05-01 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 36;
	}

	public int getBackSecond() {
		return 0;
	}

}
