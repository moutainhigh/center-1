package com.cmall.groupcenter.init;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.srnpr.zapcom.rootclass.RootInit;

public class InitGroup extends RootInit {

	@Override
	public boolean onInit() {

		// 判断如果定义过默认级别 则取定义的默认级别标记
		if (StringUtils.isNotBlank(bConfig("groupcenter.main_default_level"))) {
			GroupConst.DEFAULT_LEVEL_CODE = bConfig("groupcenter.main_default_level");
		}

		return true;
	}

	@Override
	public boolean onDestory() {
		// TODO Auto-generated method stub
		return false;
	}

}
